package fipu.diplomski.dmaglica.controller

import fipu.diplomski.dmaglica.service.VenueService
import fipu.diplomski.dmaglica.util.Paths
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(Paths.VENUE)
class VenueController(private val venueService: VenueService) {

    @PostMapping(Paths.CREATE_VENUE)
    fun createVenue() = venueService.create()

    @GetMapping(Paths.GET_VENUE)
    fun getVenue() = venueService.get()

    @PatchMapping(Paths.UPDATE_VENUE)
    fun updateVenue() = venueService.update()

    @DeleteMapping(Paths.DELETE_VENUE)
    fun deleteVenue() = venueService.delete()

}