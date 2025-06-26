package fipu.diplomski.dmaglica.controller

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

@RestController
@RequestMapping(Paths.VENUE)
class VenueController(
    private val venueService: VenueService
) {

    @GetMapping(Paths.GET_VENUE)
    fun getVenue(
        @RequestParam("venueId") venueId: Int,
    ): VenueEntity = venueService.get(venueId)

    @GetMapping(Paths.GET_ALL_VENUES)
    fun getAllVenues(
        @RequestParam("page", defaultValue = "0") page: Int,
        @RequestParam("size", defaultValue = "20") size: Int,
        @RequestParam("searchQuery", required = false) searchQuery: String? = null,
        @RequestParam("typeIds", required = false) typeIds: List<Int>? = null,
    ): PagedResponse<VenueEntity> = venueService.getAll(PageRequest.of(page, size), searchQuery, typeIds)

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

    @GetMapping(Paths.GET_VENUE_TYPE)
    fun getVenueType(
        @RequestParam("typeId") typeId: Int,
    ): String = venueService.getType(typeId)

    @GetMapping(Paths.GET_VENUE_RATING)
    fun getVenueRating(
        @RequestParam("venueId") venueId: Int,
    ): Double = venueService.getVenueRating(venueId)

    @GetMapping(Paths.GET_ALL_VENUE_RATINGS)
    fun getAllVenueRatings(
        @RequestParam("venueId") venueId: Int,
    ): List<VenueRatingEntity> = venueService.getAllRatings(venueId)

    @GetMapping(Paths.GET_ALL_VENUE_TYPES)
    fun getAllVenueTypes(): List<VenueTypeEntity> = venueService.getAllTypes()

    @GetMapping(Paths.GET_VENUE_IMAGES)
    fun getVenueImages(
        @RequestParam("venueId") venueId: Int,
    ): List<String> = venueService.getVenueImages(venueId)

    @GetMapping(Paths.GET_VENUE_MENU)
    fun getMenuImages(
        @RequestParam("venueId") venueId: Int,
    ): List<String> = venueService.getMenuImages(venueId)

    @PostMapping(Paths.CREATE_VENUE)
    fun createVenue(
        @RequestBody request: CreateVenueRequest,
    ): BasicResponse = venueService.create(request)

    @PostMapping(Paths.UPLOAD_VENUE_IMAGE)
    fun uploadVenueImage(
        @RequestParam("venueId") venueId: Int,
        @RequestParam("image") image: MultipartFile
    ): BasicResponse = venueService.uploadVenueImage(venueId, image)

    @PostMapping(Paths.UPLOAD_MENU_IMAGE)
    fun uploadMenuImage(
        @RequestParam("venueId") venueId: Int,
        @RequestParam("image") image: MultipartFile
    ): BasicResponse = venueService.uploadMenuImage(venueId, image)

    @PatchMapping("${Paths.UPDATE_VENUE}/{venueId}")
    fun updateVenue(
        @PathVariable("venueId") venueId: Int,
        @RequestBody(required = false) request: UpdateVenueRequest?
    ): BasicResponse = venueService.update(venueId, request)

    @PostMapping(Paths.RATE_VENUE)
    fun rateVenue(
        @RequestParam("venueId") venueId: Int,
        @RequestParam("rating") rating: Double,
        @RequestParam("userId") userId: Int,
        @RequestParam("comment", required = false) comment: String? = null
    ): BasicResponse = venueService.rate(venueId, rating, userId, comment)

    @DeleteMapping(Paths.DELETE_VENUE)
    fun deleteVenue(
        @RequestParam("venueId") venueId: Int,
    ): BasicResponse = venueService.delete(venueId)
}
