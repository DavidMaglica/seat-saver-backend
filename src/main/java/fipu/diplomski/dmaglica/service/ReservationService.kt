package fipu.diplomski.dmaglica.service

import fipu.diplomski.dmaglica.exception.ReservationNotFoundException
import fipu.diplomski.dmaglica.exception.UserNotFoundException
import fipu.diplomski.dmaglica.exception.VenueNotFoundException
import fipu.diplomski.dmaglica.model.data.Reservation
import fipu.diplomski.dmaglica.model.request.CreateReservationRequest
import fipu.diplomski.dmaglica.model.request.UpdateReservationRequest
import fipu.diplomski.dmaglica.model.response.BasicResponse
import fipu.diplomski.dmaglica.repo.ReservationRepository
import fipu.diplomski.dmaglica.repo.UserRepository
import fipu.diplomski.dmaglica.repo.VenueRepository
import fipu.diplomski.dmaglica.repo.entity.ReservationEntity
import fipu.diplomski.dmaglica.util.dbActionWithTryCatch
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReservationService(
    private val reservationRepository: ReservationRepository,
    private val userRepository: UserRepository,
    private val venueRepository: VenueRepository,
) {

    @Transactional
    fun create(request: CreateReservationRequest): BasicResponse {
        val user = userRepository.findByEmail(request.userEmail)
            ?: throw UserNotFoundException("User with email ${request.userEmail} not found")

        venueRepository.findById(request.venueId).orElseThrow {
            VenueNotFoundException("Venue with id ${request.venueId} not found")
        }

        val reservation = ReservationEntity().also {
            it.userId = user.id
            it.venueId = request.venueId
            it.datetime = request.reservationDate
            it.numberOfGuests = request.numberOfPeople
        }

        dbActionWithTryCatch("Error while creating reservation for user with email ${request.userEmail}") {
            reservationRepository.save(reservation)
        }

        return BasicResponse(true, "Reservation created successfully")
    }

    @Transactional(readOnly = true)
    fun getAll(email: String): List<Reservation> {
        val user = userRepository.findByEmail(email) ?: throw UserNotFoundException("User with email $email not found")

        return reservationRepository.findByUserId(user.id).map { it.toReservation() }
    }

    @Transactional
    fun update(request: UpdateReservationRequest): BasicResponse {
        userRepository.findByEmail(request.userEmail)
            ?: throw UserNotFoundException("User with email ${request.userEmail} not found")

        val reservation = reservationRepository.findById(request.reservationId)
            .orElseThrow { ReservationNotFoundException("Reservation not found") }

        if (!isRequestValid(request)) return BasicResponse(false, "Request is not valid")
        if (!containsReservationChanges(request, reservation)) return BasicResponse(false, "No changes to update")

        val updatedReservation = reservation.apply {
            numberOfGuests = request.numberOfPeople ?: reservation.numberOfGuests
            datetime = request.reservationDate ?: reservation.datetime
        }
        dbActionWithTryCatch("Error while updating reservation with id ${request.reservationId}") {
            reservationRepository.save(updatedReservation)
        }

        return BasicResponse(true, "Reservation updated successfully")
    }

    @Transactional
    fun delete(email: String, reservationId: Int): BasicResponse {
        userRepository.findByEmail(email) ?: throw UserNotFoundException("User with email $email not found")

        dbActionWithTryCatch("Error while deleting reservation for user with email $email") {
            reservationRepository.deleteById(reservationId)
        }

        return BasicResponse(true, "Reservation deleted successfully")
    }

    private fun ReservationEntity.toReservation() = Reservation(
        reservationId = this.id,
        userId = this.userId,
        venueId = this.venueId,
        datetime = this.datetime,
        numberOfGuests = this.numberOfGuests
    )

    private fun isRequestValid(request: UpdateReservationRequest): Boolean =
        (request.numberOfPeople?.let { it > 0 } == true) || request.reservationDate != null

    private fun containsReservationChanges(request: UpdateReservationRequest, reservation: ReservationEntity): Boolean =
        (request.numberOfPeople != null && request.numberOfPeople != reservation.numberOfGuests) ||
                (request.reservationDate != null && request.reservationDate != reservation.datetime)
}
