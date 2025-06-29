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
import fipu.diplomski.dmaglica.repo.entity.UserEntity
import fipu.diplomski.dmaglica.util.getSurroundingHalfHours
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import kotlin.jvm.optionals.getOrElse

@Service
class ReservationService(
    private val reservationRepository: ReservationRepository,
    private val userRepository: UserRepository,
    private val venueRepository: VenueRepository,
) {

    companion object {
        private val logger = KotlinLogging.logger(ReservationService::class.java.name)
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    fun create(request: CreateReservationRequest): BasicResponse {
        val user: UserEntity = userRepository.findById(request.userId).getOrElse {
            return BasicResponse(false, "User not found.")
        }

        val venue = venueRepository.findById(request.venueId).orElseThrow {
            VenueNotFoundException("Venue with id ${request.venueId} not found")
        }

        val currentTimestamp: LocalDateTime = request.reservationDate
        val (lowerBound, upperBound) = getSurroundingHalfHours(currentTimestamp)

        val reservations = reservationRepository.findByVenueIdAndDatetimeBetween(
            request.venueId, lowerBound, upperBound
        )

        if (reservations.isNotEmpty()) {
            val totalGuests = reservations.sumOf { it.numberOfGuests } + request.numberOfPeople
            if (totalGuests > venue.maximumCapacity) {
                return BasicResponse(
                    false,
                    "The venue is fully booked for the selected time. Please choose a different time."
                )
            }
        }

        val reservation = ReservationEntity().apply {
            userId = user.id
            venueId = request.venueId
            datetime = request.reservationDate
            numberOfGuests = request.numberOfPeople
        }

        try {
            reservationRepository.save(reservation)
        } catch (e: Exception) {
            logger.error(e) { "Error while creating reservation: ${e.message}" }
            return BasicResponse(false, "Error while creating reservation. Please try again later.")
        }

        return BasicResponse(true, "Reservation created successfully.")
    }

    @Transactional(readOnly = true)
    fun getAll(userId: Int): List<Reservation> {
        userRepository.findById(userId).getOrElse { return emptyList() }

        return reservationRepository.findByUserId(userId).map { it.toReservation() }
    }

    @Transactional
    fun update(request: UpdateReservationRequest): BasicResponse {
        userRepository.findById(request.userId).orElseThrow {
            throw UserNotFoundException("User with id ${request.userId} not found.")
        }

        val reservation = reservationRepository.findById(request.reservationId).orElseThrow {
            ReservationNotFoundException("Reservation not found")
        }

        if (!isRequestValid(request)) return BasicResponse(false, "Request is not valid.")
        if (!containsReservationChanges(request, reservation)) return BasicResponse(
            false,
            "No modifications found. Please change at least one field."
        )

        venueRepository.findById(request.venueId).orElseThrow {
            VenueNotFoundException("Venue with id ${request.venueId} not found")
        }

        reservation.apply {
            numberOfGuests = request.numberOfPeople ?: reservation.numberOfGuests
            datetime = request.reservationDate ?: reservation.datetime
        }

        try {
            reservationRepository.save(reservation)
        } catch (e: Exception) {
            logger.error { "Error while updating reservation with id ${request.reservationId}: ${e.message}" }
            return BasicResponse(false, "Error while updating reservation. Please try again later.")
        }

        return BasicResponse(true, "Reservation updated successfully.")
    }

    @Transactional
    fun delete(userId: Int, reservationId: Int, venueId: Int): BasicResponse {
        userRepository.findById(userId).orElseThrow {
            throw UserNotFoundException("User with id $userId not found.")
        }

        reservationRepository.findById(reservationId).orElseThrow {
            ReservationNotFoundException("Reservation with id $reservationId not found")
        }
        venueRepository.findById(venueId).orElseThrow {
            VenueNotFoundException("Venue with id $venueId not found")
        }

        try {
            reservationRepository.deleteById(reservationId)
        } catch (e: Exception) {
            logger.error { "Error while deleting reservation with id $reservationId: ${e.message}" }
            return BasicResponse(false, "Error while deleting reservation. Please try again later.")
        }

        return BasicResponse(true, "Reservation deleted successfully.")
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
