package fipu.diplomski.dmaglica.service

import fipu.diplomski.dmaglica.exception.ReservationNotFoundException
import fipu.diplomski.dmaglica.exception.VenueNotFoundException
import fipu.diplomski.dmaglica.model.data.Reservation
import fipu.diplomski.dmaglica.model.request.CreateReservationRequest
import fipu.diplomski.dmaglica.model.request.UpdateReservationRequest
import fipu.diplomski.dmaglica.model.response.BasicResponse
import fipu.diplomski.dmaglica.repo.ReservationRepository
import fipu.diplomski.dmaglica.repo.UserRepository
import fipu.diplomski.dmaglica.repo.VenueRepository
import fipu.diplomski.dmaglica.repo.entity.ReservationEntity
import fipu.diplomski.dmaglica.repo.entity.UserEntity
import fipu.diplomski.dmaglica.util.dbActionWithTryCatch
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrElse

@Service
class ReservationService(
    private val reservationRepository: ReservationRepository,
    private val userRepository: UserRepository,
    private val venueRepository: VenueRepository,
) {

    @Transactional
    fun create(request: CreateReservationRequest): BasicResponse {
        val user: UserEntity = userRepository.findById(request.userId).getOrElse {
            return BasicResponse(false, "User not found.")
        }

        venueRepository.findById(request.venueId).orElseThrow {
            VenueNotFoundException("Venue with id ${request.venueId} not found")
        }

        val reservation = ReservationEntity().apply {
            userId = user.id
            venueId = request.venueId
            datetime = request.reservationDate
            numberOfGuests = request.numberOfPeople
        }

        dbActionWithTryCatch("Error while creating reservation. Please try again later.") {
            reservationRepository.save(reservation)
        }

        return BasicResponse(true, "Reservation created successfully")
    }

    @Transactional(readOnly = true)
    fun getAll(userId: Int): List<Reservation> {
        userRepository.findById(userId).getOrElse { return emptyList() }

        return reservationRepository.findByUserId(userId).map { it.toReservation() }
    }

    @Transactional
    fun update(request: UpdateReservationRequest): BasicResponse {
        userRepository.findById(request.userId).getOrElse {
            return BasicResponse(false, "User not found.")
        }

        val reservation = reservationRepository.findById(request.reservationId)
            .orElseThrow { ReservationNotFoundException("Reservation not found") }

        if (!isRequestValid(request)) return BasicResponse(false, "Request is not valid")
        if (!containsReservationChanges(request, reservation)) return BasicResponse(false, "No changes to update")

        venueRepository.findById(request.venueId)
            .orElseThrow { VenueNotFoundException("Venue with id ${request.venueId} not found") }

        reservation.apply {
            numberOfGuests = request.numberOfPeople ?: reservation.numberOfGuests
            datetime = request.reservationDate ?: reservation.datetime
        }

        dbActionWithTryCatch("Error while updating reservation with id ${request.reservationId}") {
            reservationRepository.save(reservation)
        }

        return BasicResponse(true, "Reservation updated successfully")
    }

    @Transactional
    fun delete(userId: Int, reservationId: Int, venueId: Int): BasicResponse {
        userRepository.findById(userId).getOrElse {
            return BasicResponse(false, "User not found.")
        }

        reservationRepository.findById(reservationId).orElseThrow {
            ReservationNotFoundException("Reservation with id $reservationId not found")
        }
        venueRepository.findById(venueId).orElseThrow {
            VenueNotFoundException("Venue with id $venueId not found")
        }

        dbActionWithTryCatch("Error while deleting reservation. Please try again later.") {
            reservationRepository.deleteById(reservationId)
        }

        return BasicResponse(true, "Reservation deleted successfully")
    }

    private fun ReservationEntity.toReservation() = Reservation(
        id = this.id,
        userId = this.userId,
        venueId = this.venueId,
        datetime = this.datetime,
        numberOfGuests = this.numberOfGuests
    )

    private fun isRequestValid(request: UpdateReservationRequest): Boolean =
        (request.numberOfPeople?.let { it > 0 } == true) || request.reservationDate != null

    private fun containsReservationChanges(
        request: UpdateReservationRequest,
        reservation: ReservationEntity,
    ): Boolean =
        (request.numberOfPeople != null && request.numberOfPeople != reservation.numberOfGuests) ||
                (request.reservationDate != null && request.reservationDate != reservation.datetime) ||
                (request.numberOfPeople != null && request.numberOfPeople != reservation.numberOfGuests)
}
