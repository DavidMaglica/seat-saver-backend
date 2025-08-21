package fipu.diplomski.dmaglica.controller

import fipu.diplomski.dmaglica.exception.ImageDataException
import fipu.diplomski.dmaglica.model.data.Venue
import fipu.diplomski.dmaglica.model.data.VenueRating
import fipu.diplomski.dmaglica.model.data.VenueType
import fipu.diplomski.dmaglica.model.request.CreateVenueRequest
import fipu.diplomski.dmaglica.model.request.UpdateVenueRequest
import fipu.diplomski.dmaglica.model.response.BasicResponse
import fipu.diplomski.dmaglica.model.response.DataResponse
import fipu.diplomski.dmaglica.model.response.PagedResponse
import fipu.diplomski.dmaglica.repo.entity.VenueEntity
import fipu.diplomski.dmaglica.repo.entity.VenueRatingEntity
import fipu.diplomski.dmaglica.service.VenueService
import fipu.diplomski.dmaglica.util.Paths
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

/**
 * REST Controller for managing venues and related operations.
 *
 * This controller handles all venue-related operations including:
 * - Venue creation, retrieval, updating, and deletion
 * - Rating management and review functionality
 * - Image handling (venue photos and menus)
 * - Location-based venue discovery
 * - Category-based venue filtering
 *
 * Key features:
 * - Comprehensive pagination support for all listing endpoints
 * - Real-time availability calculations for venues
 * - Transactional operations with proper error handling
 *
 * Response types:
 * - [VenueEntity] for detailed venue information
 * - [PagedResponse] for paginated results
 * - [BasicResponse] for simple operation status
 *
 * Security notes:
 * - Rating submissions are tied to authenticated users
 * - Sensitive operations use transactional boundaries
 *
 * @see VenueService for business logic implementation
 * @see VenueEntity for venue data model details
 * @see VenueRatingEntity for venue rating data model details
 * @see PagedResponse for pagination details
 */
@RestController
class VenueController(
    private val venueService: VenueService
) {

    /**
     * Retrieves comprehensive venue details including:
     * - Basic venue information
     * - Calculated average rating
     * - Current available capacity
     *
     * @param venueId id of the venue to retrieve
     * @return [VenueEntity] containing:
     *   - Core venue details
     *   - averageRating: Calculated from all ratings (0.0 if no ratings)
     *   - availableCapacity: Current available slots (updated in real-time)
     * @throws jakarta.persistence.EntityNotFoundException if venue doesn't exist
     *
     * @apiNote This performs multiple real-time calculations:
     * 1. Rating average (ignores NaN/Infinite values)
     * 2. Capacity based on current reservations
     * 3. Time-bound availability (30-minute window)
     *
     */
    @GetMapping(Paths.VENUE_BY_ID)
    fun getVenue(
        @PathVariable venueId: Int,
    ): Venue = venueService.get(venueId)

    /**
     * Retrieves a paginated list of venues with optional filtering capabilities.
     *
     * Supports:
     * - Pagination control (page/size parameters)
     * - Text search across venue names
     * - Filtering by venue type ids
     * - Real-time availability calculation for the current time window
     *
     * @param page Page number (0-indexed, defaults to 0)
     * @param size Number of items per page (defaults to 20)
     * @param searchQuery Optional search string for venue name matching (case-insensitive)
     * @param typeIds Optional list of venue type ids to filter by
     * @return [PagedResponse] containing:
     *   - content: List of matching venues with calculated:
     *     - averageRating
     *     - current availableCapacity
     *   - page: Current page number
     *   - size: Page size
     *   - totalElements: Total matching venues
     *
     * @apiNote The availability window is calculated as 30 minutes before/after current time
     *
     */
    @GetMapping(Paths.VENUES)
    fun getVenues(
        @RequestParam("page", defaultValue = "0") page: Int,
        @RequestParam("size", defaultValue = "20") size: Int,
        @RequestParam("category", required = false) category: String?,
        @RequestParam("searchQuery", required = false) searchQuery: String? = null,
        @RequestParam("typeIds", required = false) typeIds: List<Int>? = null,
        @RequestParam("latitude", required = false) latitude: Double? = null,
        @RequestParam("longitude", required = false) longitude: Double? = null,
    ): PagedResponse<Venue> {
        return when (category?.lowercase()) {
            "nearby" -> venueService.getNearbyVenues(PageRequest.of(page, size), latitude, longitude)
            "new" -> venueService.getNewVenues(PageRequest.of(page, size))
            "trending" -> venueService.getTrendingVenues(PageRequest.of(page, size))
            "suggested" -> venueService.getSuggestedVenues(PageRequest.of(page, size))
            null -> venueService.getAll(PageRequest.of(page, size), searchQuery, typeIds)
            else -> throw IllegalArgumentException("Unsupported venue category.")
        }
    }

    @GetMapping(Paths.VENUE_BY_OWNER)
    fun getVenuesByOwner(
        @PathVariable ownerId: Int,
        @RequestParam("page", defaultValue = "0") page: Int,
        @RequestParam("size", defaultValue = "20") size: Int,
    ): PagedResponse<Venue> = venueService.getByOwner(ownerId, PageRequest.of(page, size))

    @GetMapping(Paths.VENUES_BY_OWNER_COUNT)
    fun getVenuesCountByOwner(
        @PathVariable ownerId: Int,
    ): Int = venueService.getCountByOwner(ownerId)

    /**
     * Retrieves a venue type by its id.
     *
     * @param typeId the unique identifier of the venue type to retrieve
     * @return the name/type of the venue as a String
     * @throws jakarta.persistence.EntityNotFoundException if no venue type is found with the given id
     *
     */
    @GetMapping(Paths.VENUE_TYPE)
    fun getVenueType(
        @PathVariable typeId: Int,
    ): String = venueService.getType(typeId)

    /**
     * Retrieves the average rating for a specific venue.
     *
     * @param venueId the unique identifier of the venue whose rating is being requested
     * @return the average rating of the venue as a Double value
     * @throws jakarta.persistence.EntityNotFoundException if no venue exists with the specified id
     * @apiNote Example usage: GET /api/venues/rating?venueId=123
     *
     */
    @GetMapping(Paths.VENUE_AVERAGE_RATING)
    fun getVenueAverageRating(
        @PathVariable venueId: Int,
    ): Double = venueService.getVenueAverageRating(venueId)

    /**
     * Retrieves all ratings for a specific venue.
     *
     * @param venueId the unique identifier of the venue whose ratings are being requested
     * @return a list of [VenueRatingEntity] objects sorted by most recent first (descending by id)
     * @throws jakarta.persistence.EntityNotFoundException if no venue exists with the specified id
     *
     */
    @GetMapping(Paths.ALL_VENUE_RATINGS)
    fun getAllVenueRatings(
        @PathVariable venueId: Int,
    ): List<VenueRating> = venueService.getAllRatings(venueId)

    @GetMapping(Paths.OVERALL_RATING)
    fun getOverallRating(
        @PathVariable ownerId: Int,
    ): Double = venueService.getOverallRating(ownerId)

    @GetMapping(Paths.RATINGS_COUNT)
    fun getRatingsCount(
        @PathVariable ownerId: Int,
    ): Int = venueService.getRatingsCount(ownerId)

    @GetMapping(Paths.VENUE_UTILISATION_RATE)
    fun getVenueUtilisationRate(
        @PathVariable ownerId: Int,
    ): Double = venueService.getVenueUtilisationRate(ownerId)

    /**
     * Retrieves all available venue types.
     *
     * @return a list of all VenueTypeEntity objects in the system
     * @apiNote Returns an empty list if no venue types are configured
     *
     */
    @GetMapping(Paths.ALL_VENUE_TYPES)
    fun getAllVenueTypes(): List<VenueType> = venueService.getAllTypes()

    @GetMapping(Paths.VENUE_HEADER_IMAGE)
    fun getVenueHeaderImage(
        @PathVariable venueId: Int,
    ): DataResponse<String?> = venueService.getVenueHeaderImage(venueId)

    /**
     * Retrieves all venue images for a specific venue as Base64 encoded strings.
     *
     * @param venueId the unique identifier of the venue whose images are being requested
     * @return a list of Base64 encoded image strings, or empty list if no images exist
     * @throws jakarta.persistence.EntityNotFoundException if no venue exists with the specified id
     * @apiNote The images are:
     *          - Retrieved from the venue image repository
     *          - Decompressed from stored binary format
     *          - Encoded as Base64 strings
     *          - Returns empty list (with warning log) if no images found
     *
     */
    @GetMapping(Paths.VENUE_IMAGES)
    fun getVenueImages(
        @PathVariable venueId: Int,
    ): List<String> = venueService.getVenueImages(venueId)

    /**
     * Retrieves all menu images for a specific venue as Base64 encoded strings.
     *
     * @param venueId the unique identifier of the venue whose menu images are being requested
     * @return a list of Base64 encoded menu image strings, or empty list if no images exist
     * @throws jakarta.persistence.EntityNotFoundException if no venue exists with the specified id
     * @apiNote The menu images are:
     *          - Retrieved from the menu image repository
     *          - Decompressed from stored binary format
     *          - Encoded as Base64 strings
     *          - Returns empty list (with warning log) if no images found
     *
     */
    @GetMapping(Paths.MENU_IMAGES)
    fun getMenuImages(
        @PathVariable venueId: Int,
    ): List<String> = venueService.getMenuImages(venueId)

    /**
     * Creates a new venue with the provided details.
     *
     * @param request The venue creation request containing all required properties
     * @return [BasicResponse] indicating operation status with:
     *         - success: true if creation was successful
     *         - message: Descriptive message including venue name if successful
     *
     * @throws org.springframework.dao.DataAccessException if there's an issue persisting the venue
     *
     * The creation process:
     * 1. Validates the request parameters
     * 2. Creates a new venue entity with default averageRating (0.0)
     * 3. Persists the venue to the database
     *
     * @note Available capacity cannot exceed maximum capacity
     * @warning This operation is transactional - failure will roll back all changes
     *
     */
    @PostMapping(Paths.VENUES)
    fun createVenue(
        @RequestBody request: CreateVenueRequest,
    ): DataResponse<Int> = venueService.create(request)

    /**
     * Uploads and stores a venue image for the specified venue.
     *
     * @param venueId The id of the venue to associate with the image
     * @param image The image file to upload
     * @return [BasicResponse] with:
     *         - success: true if upload was successful
     *         - message: Status message including filename if successful
     *
     * @throws ImageDataException if the image fails validation (invalid format/size)
     *
     * The upload process:
     * 1. Validates image format and size
     * 2. Compresses the image data
     * 3. Stores the image metadata and binary data
     *
     * @note Maximum file size is 5MB (configurable)
     * @warning Image data will be compressed before storage
     *
     */
    @PostMapping(Paths.VENUE_IMAGES)
    fun uploadVenueImage(
        @PathVariable venueId: Int,
        @RequestParam("image") image: MultipartFile
    ): BasicResponse = venueService.uploadVenueImage(venueId, image)

    /**
     * Uploads and stores a menu image for the specified venue.
     *
     * @param venueId The id of the venue to associate with the menu image
     * @param image The menu image file to upload (supported formats: JPEG, PNG)
     * @return [BasicResponse] with:
     *         - success: true if upload was successful
     *         - message: Status message including filename if successful
     *
     * @throws ImageDataException if the image fails validation
     *
     * The upload process:
     * 1. Validates image format and size
     * 2. Compresses the image data
     * 3. Stores the menu image metadata and binary data
     *
     * @note Maximum file size is 5MB (configurable)
     * @warning Image data will be compressed before storage
     *
     */
    @PostMapping(Paths.MENU_IMAGES)
    fun uploadMenuImage(
        @PathVariable venueId: Int,
        @RequestParam("image") image: MultipartFile
    ): BasicResponse = venueService.uploadMenuImage(venueId, image)

    /**
     * Updates a venue's information with the provided request data.
     *
     * This endpoint performs a partial update of the venue's properties. Only non-null fields
     * in the request will be updated, while null fields will preserve their current values.
     *
     * @param venueId The id of the venue to update (from path variable)
     * @param request The update request containing new values (optional - if null or empty, no changes will be made)
     * @return [BasicResponse] indicating operation status with:
     *         - success: true if update was successful
     *         - message: Detailed status message
     *
     * @throws jakarta.persistence.EntityNotFoundException if no venue exists with the specified id
     *
     * The update process:
     * 1. Validates the request contains at least one change
     * 2. Applies non-null fields from request to venue entity
     * 3. Persists the updated venue
     *
     */
    @PatchMapping(Paths.VENUE_BY_ID)
    fun updateVenue(
        @PathVariable venueId: Int,
        @RequestBody(required = false) request: UpdateVenueRequest?
    ): BasicResponse = venueService.update(venueId, request)

    /**
     * Submits a rating for a specific venue.
     * This endpoint allows users to rate a venue and optionally provide a comment.
     * The rating will update the venue's average rating calculation.
     *
     * @param venueId The unique identifier of the venue to be rated
     * @param rating The rating value between 0.5 and 5.0 (inclusive)
     * @param userId The unique identifier of the user submitting the rating
     * @param comment Optional text comment accompanying the rating
     * @return [BasicResponse] indicating operation status with success flag and message
     * @throws jakarta.persistence.EntityNotFoundException if either the venue or user is not found
     *
     */
    @PostMapping(Paths.RATE_VENUE)
    fun rateVenue(
        @PathVariable venueId: Int,
        @RequestParam("rating") rating: Double,
        @RequestParam("userId") userId: Int,
        @RequestParam("comment", required = false) comment: String? = null
    ): BasicResponse = venueService.rate(venueId, rating, userId, comment)

    /**
     * Deletes a venue by its id.
     *
     * This operation permanently removes the venue and all its associated data from the system.
     * The operation is transactional and will be rolled back if any errors occur during deletion.
     *
     * @param venueId the unique identifier of the venue to be deleted
     * @return BasicResponse indicating operation status with success flag and message
     *
     */
    @DeleteMapping(Paths.VENUE_BY_ID)
    fun deleteVenue(
        @PathVariable venueId: Int,
    ): BasicResponse = venueService.delete(venueId)
}
