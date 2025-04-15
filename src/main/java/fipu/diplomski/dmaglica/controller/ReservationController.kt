package fipu.diplomski.dmaglica.controller

import fipu.diplomski.dmaglica.model.request.CreateReservationRequest
import fipu.diplomski.dmaglica.model.request.UpdateReservationRequest
import fipu.diplomski.dmaglica.service.ReservationService
import fipu.diplomski.dmaglica.util.Paths
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(Paths.RESERVATION)
class ReservationController(private val reservationService: ReservationService) {

    @PostMapping(Paths.CREATE_RESERVATION)
    fun createReservation(
        @RequestBody request: CreateReservationRequest
    ) = reservationService.create(request)

    @GetMapping(Paths.GET_RESERVATIONS)
    fun getReservations(
        @RequestParam("email") email: String
    ) = reservationService.getAll(email)

    @PatchMapping(Paths.UPDATE_RESERVATION)
    fun updateReservation(
        @RequestBody request: UpdateReservationRequest
    ) = reservationService.update(request)

    @DeleteMapping(Paths.DELETE_RESERVATION)
    fun deleteReservation(
        @RequestParam("email") email: String,
        @RequestParam("reservationId") reservationId: Int
    ) = reservationService.delete(email, reservationId)
}
