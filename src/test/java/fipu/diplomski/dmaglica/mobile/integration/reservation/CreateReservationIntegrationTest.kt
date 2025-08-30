package fipu.diplomski.dmaglica.mobile.integration.reservation

import fipu.diplomski.dmaglica.model.request.CreateReservationRequest
import fipu.diplomski.dmaglica.model.response.BasicResponse
import jakarta.transaction.Transactional
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should not be`
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@Transactional
class CreateReservationIntegrationTest : AbstractReservationIntegrationTest() {

    @Test
    fun `should create reservation successfully with valid user and venue`() {
        val workingDays = createWorkingDays(venue.id, listOf(0, 1, 2, 3, 4, 5, 6))
        workingDaysRepository.saveAllAndFlush(workingDays)

        val request = CreateReservationRequest(
            userId = customer.id,
            venueId = venue.id,
            reservationDate = LocalDateTime.now().withHour(12).plusDays(1),
            numberOfGuests = 2
        )

        val response: BasicResponse = reservationService.create(request)

        response.success `should be equal to` true
        response.message `should be equal to` "Reservation created successfully."

        val savedReservation = reservationRepository.findByVenueIdAndDatetimeBetween(
            venue.id,
            request.reservationDate.minusMinutes(30),
            request.reservationDate.plusMinutes(30)
        ).firstOrNull()

        savedReservation `should not be` null
        savedReservation!!.userId `should be equal to` customer.id
        savedReservation.venueId `should be equal to` venue.id
        savedReservation.numberOfGuests `should be equal to` 2
    }

    @Test
    fun `should fail when user does not exist`() {
        val request = CreateReservationRequest(
            userId = 9999,
            venueId = venue.id,
            reservationDate = LocalDateTime.now().plusDays(1),
            numberOfGuests = 1
        )

        val response: BasicResponse = reservationService.create(request)

        response.success `should be equal to` false
        response.message `should be equal to` "User not found. Please try again later."
    }

    @Test
    fun `should fail when venue does not exist`() {
        val request = CreateReservationRequest(
            userId = customer.id,
            venueId = 9999,
            reservationDate = LocalDateTime.now().plusDays(1),
            numberOfGuests = 1
        )

        try {
            reservationService.create(request)
        } catch (ex: Exception) {
            ex.message `should be equal to` "Venue with id 9999 not found"
        }
    }

    @Test
    fun `should fail when venue is fully booked`() {
        val workingDays = createWorkingDays(venue.id, listOf(0, 1, 2, 3, 4, 5, 6))
        workingDaysRepository.saveAllAndFlush(workingDays)
        val dateTime = LocalDateTime.now().withHour(12)
        reservationRepository.save(
            createReservation(
                userId = customer.id,
                venueId = venue.id,
                datetime = dateTime,
                numberOfGuests = venue.maximumCapacity
            )
        )

        val request = CreateReservationRequest(
            userId = customer.id,
            venueId = venue.id,
            reservationDate = dateTime,
            numberOfGuests = 1
        )

        val response: BasicResponse = reservationService.create(request)

        response.success `should be equal to` false
        response.message `should be equal to` "The venue is fully booked for the selected time. Please choose a different time."
    }

    @Test
    fun `should allow creation with user email instead of userId`() {
        val workingDays = createWorkingDays(venue.id, listOf(0, 1, 2, 3, 4, 5, 6))
        workingDaysRepository.saveAllAndFlush(workingDays)

        val request = CreateReservationRequest(
            userEmail = customer.email,
            venueId = venue.id,
            reservationDate = LocalDateTime.now().withHour(12),
            numberOfGuests = 3
        )

        val response: BasicResponse = reservationService.create(request)

        response.success `should be equal to` true
        response.message `should be equal to` "Reservation created successfully."
    }

    @Test
    fun `should fail when non working hours`() {
        val workingDays = createWorkingDays(venue.id, listOf(0, 1, 2, 3, 4, 5, 6))
        workingDaysRepository.saveAllAndFlush(workingDays)

        val request = CreateReservationRequest(
            userId = customer.id,
            venueId = venue.id,
            reservationDate = LocalDateTime.now().withHour(18).withMinute(0),
            numberOfGuests = 2
        )

        val response: BasicResponse = reservationService.create(request)

        response.success `should be equal to` false
        response.message `should be equal to` "The venue is closed at the selected time. Please choose a different time."
    }

    @Test
    fun `should fail when non working day`() {
        val workingDays = createWorkingDays(venue.id, emptyList())
        workingDaysRepository.saveAllAndFlush(workingDays)

        val request = CreateReservationRequest(
            userId = customer.id,
            venueId = venue.id,
            reservationDate = LocalDateTime.now().withHour(10).withMinute(0),
            numberOfGuests = 2
        )

        val response: BasicResponse = reservationService.create(request)

        response.success `should be equal to` false
        response.message `should be equal to` "The venue is closed on the selected day. Please choose a different day."
    }
}