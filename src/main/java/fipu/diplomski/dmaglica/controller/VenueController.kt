package fipu.diplomski.dmaglica.controller

import fipu.diplomski.dmaglica.exception.ImageDataException
import fipu.diplomski.dmaglica.model.request.CreateVenueRequest
import fipu.diplomski.dmaglica.model.request.UpdateVenueRequest
import fipu.diplomski.dmaglica.model.response.BasicResponse
import fipu.diplomski.dmaglica.model.response.PagedResponse
import fipu.diplomski.dmaglica.repo.entity.VenueEntity
import fipu.diplomski.dmaglica.repo.entity.VenueRatingEntity
import fipu.diplomski.dmaglica.repo.entity.VenueTypeEntity
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
 * - Image uploads require proper authentication
 * - Rating submissions are tied to authenticated users
 * - Sensitive operations use transactional boundaries
 *
 * @see VenueService for business logic implementation
 * @see VenueEntity for venue data model details
 * @see VenueRatingEntity for venue rating data model details
 * @see PagedResponse for pagination details
 */
@RestController
@RequestMapping(Paths.VENUE)
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
     * @example
     * GET /api/venues/get?venueId=123
     *
     */
    @GetMapping(Paths.GET_VENUE)
    fun getVenue(
        @RequestParam("venueId") venueId: Int,
    ): VenueEntity = venueService.get(venueId)

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
     * @example
     * GET /api/venues?page=0&size=10&searchQuery=cafe&typeIds=1,2
     *
     */
    @GetMapping(Paths.GET_ALL_VENUES)
    fun getAllVenues(
        @RequestParam("page", defaultValue = "0") page: Int,
        @RequestParam("size", defaultValue = "20") size: Int,
        @RequestParam("searchQuery", required = false) searchQuery: String? = null,
        @RequestParam("typeIds", required = false) typeIds: List<Int>? = null,
    ): PagedResponse<VenueEntity> = venueService.getAll(PageRequest.of(page, size), searchQuery, typeIds)

    /**
     * Retrieves venues by category with filtering.
     *
     * Supported categories:
     * - "nearby": Returns venues near specified coordinates (requires latitude/longitude)
     * - "new": Returns recently added venues (sorted by id descending)
     * - "trending": Returns venues with most reservations
     * - "suggested": Returns highly-rated venues with available capacity
     *
     * @param category The venue category to filter by (case-insensitive)
     * @param page Page number (0-indexed, defaults to 0)
     * @param size Number of items per page (defaults to 20)
     * @param latitude Optional latitude for nearby search
     * @param longitude Optional longitude for nearby search
     * @return [PagedResponse] containing:
     *   - content: List of matching venues with calculated:
     *     - averageRating
     *     - current availableCapacity
     *   - page: Current page number
     *   - size: Page size
     *   - totalElements: Total matching venues
     * @throws IllegalArgumentException if invalid category is provided
     *
     * @apiNote For 'nearby' category:
     * - Falls back to default location (Zagreb) if coordinates not provided
     *
     * @see [VenueService.getNearbyVenues]
     * @see [VenueService.getNewVenues]
     * @see [VenueService.getTrendingVenues]
     * @see [VenueService.getSuggestedVenues]
     *
     * @example
     * GET /api/venues/category?category=nearby&latitude=45.8&longitude=16.0&page=0&size=10
     *
     */
    @GetMapping(Paths.GET_VENUES_BY_CATEGORY)
    fun getVenuesByCategory(
        @RequestParam category: String,
        @RequestParam("page", defaultValue = "0") page: Int,
        @RequestParam("size", defaultValue = "20") size: Int,
        @RequestParam("latitude", required = false) latitude: Double? = null,
        @RequestParam("longitude", required = false) longitude: Double? = null,
    ): PagedResponse<VenueEntity> = when (category.lowercase()) {
        "nearby" -> venueService.getNearbyVenues(PageRequest.of(page, size), latitude, longitude)
        "new" -> venueService.getNewVenues(PageRequest.of(page, size))
        "trending" -> venueService.getTrendingVenues(PageRequest.of(page, size))
        "suggested" -> venueService.getSuggestedVenues(PageRequest.of(page, size))
        else -> throw IllegalArgumentException("Unsupported venue category.")
    }

    /**
     * Retrieves a venue type by its id.
     *
     * @param typeId the unique identifier of the venue type to retrieve
     * @return the name/type of the venue as a String
     * @throws jakarta.persistence.EntityNotFoundException if no venue type is found with the given id
     *
     * @example
     * GET /api/venues/type?typeId=1
     *
     */
    @GetMapping(Paths.GET_VENUE_TYPE)
    fun getVenueType(
        @RequestParam("typeId") typeId: Int,
    ): String = venueService.getType(typeId)

    /**
     * Retrieves the average rating for a specific venue.
     *
     * @param venueId the unique identifier of the venue whose rating is being requested
     * @return the average rating of the venue as a Double value
     * @throws jakarta.persistence.EntityNotFoundException if no venue exists with the specified id
     * @apiNote Example usage: GET /api/venues/rating?venueId=123
     *
     * @example
     * GET /api/venues/rating?venueId=123
     *
     */
    @GetMapping(Paths.GET_VENUE_RATING)
    fun getVenueRating(
        @RequestParam("venueId") venueId: Int,
    ): Double = venueService.getVenueRating(venueId)

    /**
     * Retrieves all ratings for a specific venue.
     *
     * @param venueId the unique identifier of the venue whose ratings are being requested
     * @return a list of [VenueRatingEntity] objects sorted by most recent first (descending by id)
     * @throws jakarta.persistence.EntityNotFoundException if no venue exists with the specified id
     *
     * @example
     * GET /api/venues/ratings?venueId=123
     *
     */
    @GetMapping(Paths.GET_ALL_VENUE_RATINGS)
    fun getAllVenueRatings(
        @RequestParam("venueId") venueId: Int,
    ): List<VenueRatingEntity> = venueService.getAllRatings(venueId)

    /**
     * Retrieves all available venue types.
     *
     * @return a list of all VenueTypeEntity objects in the system
     * @apiNote Returns an empty list if no venue types are configured
     *
     * @example
     * GET /api/venues/types
     *
     */
    @GetMapping(Paths.GET_ALL_VENUE_TYPES)
    fun getAllVenueTypes(): List<VenueTypeEntity> = venueService.getAllTypes()

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
     * @example
     * GET /api/venues/images?venueId=123
     *
     */
    @GetMapping(Paths.GET_VENUE_IMAGES)
    fun getVenueImages(
        @RequestParam("venueId") venueId: Int,
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
     * @example:
     * GET /api/venues/menu?venueId=123
     *
     */
    @GetMapping(Paths.GET_VENUE_MENU)
    fun getMenuImages(
        @RequestParam("venueId") venueId: Int,
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
     * @example
     * POST /api/venues/create
     * Request Body:
     * {
     *   "name": "Venue Name",
     *   "location": "Venue Address",
     *   "description": "Venue Description",
     *   "workingHours": "Venue Working Hours",
     *   "maximumCapacity": 100,
     *   "venueTypeId": 1,
     * }
     *
     */
    @PostMapping(Paths.CREATE_VENUE)
    fun createVenue(
        @RequestBody request: CreateVenueRequest,
    ): BasicResponse = venueService.create(request)

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
     * @example
     * POST /api/venues/upload-image?venueId=123
     * Headers:
     * - Content-Type: multipart/form-data
     *
     * Form Data:
     * - venueId: 123 (number)
     * - image: (binary file) venue.jpg
     */
    @PostMapping(Paths.UPLOAD_VENUE_IMAGE)
    fun uploadVenueImage(
        @RequestParam("venueId") venueId: Int,
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
     * @example
     * POST /api/venues/upload-menu-image?venueId=123
     * Headers:
     * - Content-Type: multipart/form-data
     *
     * Form Data:
     * - venueId: 123 (number)
     * - image: (binary file) menu.jpg
     *
     */
    @PostMapping(Paths.UPLOAD_MENU_IMAGE)
    fun uploadMenuImage(
        @RequestParam("venueId") venueId: Int,
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
     * @example
     * PATCH /api/venues/update/{venueId}
     * Request Body:
     * {
     *   "name": "Updated Venue Name",
     *   "location": "Updated Address",
     *   "description": "Updated description",
     *   "venueTypeId": 2,
     *   "workingHours": "Updated Working Hours",
     *   "maximumCapacity": 150,
     * }
     */
    @PatchMapping("${Paths.UPDATE_VENUE}/{venueId}")
    fun updateVenue(
        @PathVariable("venueId") venueId: Int,
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
     * @example
     * POST /api/venues/rate?venueId=123&rating=4.5&userId=456&comment=Great%20place!
     *
     */
    @PostMapping(Paths.RATE_VENUE)
    fun rateVenue(
        @RequestParam("venueId") venueId: Int,
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
     * @example
     * DELETE /api/venues?id=123
     *
     */
    @DeleteMapping(Paths.DELETE_VENUE)
    fun deleteVenue(
        @RequestParam("venueId") venueId: Int,
    ): BasicResponse = venueService.delete(venueId)
}
