package fipu.diplomski.dmaglica.controller

import fipu.diplomski.dmaglica.service.VenueService
import fipu.diplomski.dmaglica.util.Paths
import org.springframework.web.bind.annotation.*

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
    ) = venueService.create(name, location, description, typeId, workingHours)

    @GetMapping(Paths.GET_VENUE)
    fun getVenue(
        @RequestParam("venueId") venueId: Int,
    ) = venueService.get(venueId)

    @GetMapping(Paths.GET_VENUE_TYPE)
    fun getVenueType(
        @RequestParam("typeId") typeId: Int,
    ) = venueService.getVenueType(typeId)

    @PatchMapping(Paths.UPDATE_VENUE)
    fun updateVenue() = venueService.update()

    @PatchMapping(Paths.RATE_VENUE)
    fun rateVenue(
        @RequestParam("venueId") venueId: Int,
        @RequestParam("rating") rating: Double,
    ) = venueService.rate(venueId, rating)

    @DeleteMapping(Paths.DELETE_VENUE)
    fun deleteVenue() = venueService.delete()

}