package fipu.diplomski.dmaglica.controller

import fipu.diplomski.dmaglica.service.ReservationService
import fipu.diplomski.dmaglica.util.Paths
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Paths.RESERVATION)
class ReservationController(private val reservationService: ReservationService) {

    @PostMapping(Paths.CREATE_RESERVATION)
    fun createReservation() = reservationService.create()

    @GetMapping(Paths.GET_RESERVATION)
    fun getReservation() = reservationService.get()

    @PatchMapping(Paths.UPDATE_RESERVATION)
    fun updateReservation() = reservationService.update()

    @DeleteMapping(Paths.DELETE_RESERVATION)
    fun deleteReservation() = reservationService.delete()

}