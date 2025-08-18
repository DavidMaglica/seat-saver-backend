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
import fipu.diplomski.dmaglica.util.getSurroundingHalfHours
import fipu.diplomski.dmaglica.util.toDto
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import kotlin.jvm.optionals.getOrElse
import kotlin.jvm.optionals.getOrNull

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
        val user: UserEntity? = when {
            request.userId != null -> userRepository.findById(request.userId).orElse(null)
            request.userEmail != null -> userRepository.findByEmail(request.userEmail)
            else -> null
        }

        if (user == null) {
            return BasicResponse(false, "User not found. Please try again later.")
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
            val currentNumberOfGuests = reservations.sumOf { it.numberOfGuests }
            if (currentNumberOfGuests + request.numberOfGuests > venue.maximumCapacity) {
                return BasicResponse(
                    false, "The venue is fully booked for the selected time. Please choose a different time."
                )
            }
        }

        val reservation = ReservationEntity().apply {
            userId = user.id
            venueId = request.venueId
            datetime = request.reservationDate
            numberOfGuests = request.numberOfGuests
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
    fun getByUserId(userId: Int): List<Reservation> {
        userRepository.findById(userId).getOrElse { return emptyList() }

        return reservationRepository.findByUserId(userId).map { it.toDto() }
    }

    @Transactional(readOnly = true)
    fun getByOwnerId(ownerId: Int): List<Reservation> {
        val venues = venueRepository.findByOwnerId(ownerId)

        if (venues.isEmpty()) return emptyList()

        val venueIds = venues.map { it.id }

        return reservationRepository.findByVenueIdIn(venueIds).map { it.toDto() }
    }

    @Transactional(readOnly = true)
    fun getById(reservationId: Int): Reservation? = reservationRepository.findById(reservationId).getOrNull()?.toDto()

    @Transactional(readOnly = true)
    fun getReservationsCount(
        ownerId: Int,
        venueId: Int? = null,
        startDate: LocalDateTime? = null,
        endDate: LocalDateTime?
    ): Int {
        if (venueId != null) {
            return reservationRepository.countByVenueId(venueId)
        }

        val venueIds = venueRepository.findByOwnerId(ownerId).map { it.id }
        if (venueIds.isEmpty()) return 0

        if (startDate != null && endDate != null) {
            return reservationRepository.countByVenueIdInAndDatetimeBetween(venueIds, startDate, endDate)
        }

        return reservationRepository.countByVenueIdIn(venueIds)

    }

    @Transactional
    fun update(reservationId: Int, request: UpdateReservationRequest): BasicResponse {
        val reservation = reservationRepository.findById(reservationId).orElseThrow {
            ReservationNotFoundException("Reservation not found")
        }

        if (!isRequestValid(request)) return BasicResponse(false, "Request is not valid.")
        if (!containsReservationChanges(request, reservation)) return BasicResponse(
            false,
            "No modifications found. Please change at least one field."
        )

        reservation.apply {
            numberOfGuests = request.numberOfGuests ?: reservation.numberOfGuests
            datetime = request.reservationDate ?: reservation.datetime
        }

        try {
            reservationRepository.save(reservation)
        } catch (e: Exception) {
            logger.error { "Error while updating reservation with id ${reservationId}: ${e.message}" }
            return BasicResponse(false, "Error while updating reservation. Please try again later.")
        }

        return BasicResponse(true, "Reservation updated successfully.")
    }

    @Transactional
    fun delete(reservationId: Int): BasicResponse {
        reservationRepository.findById(reservationId).orElseThrow {
            ReservationNotFoundException("Reservation with id $reservationId not found")
        }

        try {
            reservationRepository.deleteById(reservationId)
        } catch (e: Exception) {
            logger.error { "Error while deleting reservation with id $reservationId: ${e.message}" }
            return BasicResponse(false, "Error while deleting reservation. Please try again later.")
        }

        return BasicResponse(true, "Reservation deleted successfully.")
    }

    private fun isRequestValid(request: UpdateReservationRequest): Boolean =
        (request.numberOfGuests?.let { it > 0 } == true) || request.reservationDate != null

    private fun containsReservationChanges(
        request: UpdateReservationRequest,
        reservation: ReservationEntity,
    ): Boolean =
        (request.numberOfGuests != null && request.numberOfGuests != reservation.numberOfGuests) ||
                (request.reservationDate != null && !request.reservationDate.isEqual(reservation.datetime)) ||
                (request.numberOfGuests != null && request.numberOfGuests != reservation.numberOfGuests)
}
