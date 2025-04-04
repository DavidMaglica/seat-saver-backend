package fipu.diplomski.dmaglica.controller

import fipu.diplomski.dmaglica.service.VenueService
import fipu.diplomski.dmaglica.util.Paths
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping(Paths.VENUE)
class VenueController(private val venueService: VenueService) {

    @PostMapping(Paths.CREATE_VENUE)
    fun createVenue(
        @RequestParam("name") name: String,
        @RequestParam("location") location: String,
        @RequestParam("description") description: String,
        @RequestParam("typeId") typeId: Int,
        @RequestParam("workingHours") workingHours: String
    ) = venueService.createVenue(name, location, description, typeId, workingHours)

    @PostMapping(Paths.UPLOAD_VENUE_IMAGE)
    fun uploadImage(
        @RequestParam("venueId") venueId: Int,
        @RequestParam("image") image: MultipartFile
    ) = venueService.uploadVenueImage(venueId, image)

    @GetMapping(Paths.GET_VENUE)
    fun getVenue(
        @RequestParam("venueId") venueId: Int,
    ) = venueService.getVenue(venueId)

    @GetMapping
    fun getVenueImages(
        @RequestParam("venueId") venueId: Int,
        @RequestParam("venueName") venueName: String,
    ) = venueService.getVenueImages(venueId, venueName)

    @GetMapping(Paths.GET_VENUE_TYPE)
    fun getVenueType(
        @RequestParam("typeId") typeId: Int,
    ) = venueService.getVenueType(typeId)

    @GetMapping(Paths.GET_VENUE_MENU)
    fun getMenuImage(
        @RequestParam("venueId") venueId: Int,
        @RequestParam("venueName") venueName: String,
    ) = venueService.getMenuImage(venueId, venueName)

    @PostMapping(Paths.UPLOAD_MENU_IMAGE)
    fun uploadMenuImage(
        @RequestParam("venueId") venueId: Int,
        @RequestParam("image") image: MultipartFile
    ) = venueService.uploadMenuImage(venueId, image)

    @PatchMapping(Paths.UPDATE_VENUE)
    fun updateVenue(
        @RequestParam("venueId") venueId: Int,
        @RequestParam("name") name: String?,
        @RequestParam("location") location: String?,
        @RequestParam("description") description: String?,
        @RequestParam("typeId") typeId: Int?,
        @RequestParam("workingHours") workingHours: String?
    ) = venueService.update(venueId, name, location, description, typeId, workingHours)

    @PatchMapping(Paths.RATE_VENUE)
    fun rateVenue(
        @RequestParam("venueId") venueId: Int,
        @RequestParam("rating") rating: Double,
    ) = venueService.rate(venueId, rating)

    @DeleteMapping(Paths.DELETE_VENUE)
    fun deleteVenue(
        @RequestParam("venueId") venueId: Int,
    ) = venueService.delete(venueId)

}