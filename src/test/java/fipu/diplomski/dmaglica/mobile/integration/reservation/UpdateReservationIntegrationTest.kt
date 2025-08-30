package fipu.diplomski.dmaglica.mobile.integration.reservation

import fipu.diplomski.dmaglica.model.request.UpdateReservationRequest
import fipu.diplomski.dmaglica.model.response.BasicResponse
import jakarta.transaction.Transactional
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@Transactional
class UpdateReservationIntegrationTest : AbstractReservationIntegrationTest() {

    @Test
    fun `should update reservation successfully with new number of guests`() {
        val existingReservation = createReservation(userId = customer.id, venueId = venue.id, numberOfGuests = 2)
        reservationRepository.saveAndFlush(existingReservation)

        val request = UpdateReservationRequest(
            numberOfGuests = 4,
            reservationDate = null
        )

        val response: BasicResponse = reservationService.update(existingReservation.id, request)

        response.success `should be equal to` true
        response.message `should be equal to` "Reservation updated successfully."

        val updatedReservation = reservationRepository.findById(existingReservation.id).get()
        updatedReservation.numberOfGuests `should be equal to` 4
        updatedReservation.datetime `should be equal to` existingReservation.datetime
    }

    @Test
    fun `should update reservation successfully with new date`() {
        val existingReservation = createReservation(userId = customer.id, venueId = venue.id, numberOfGuests = 2)
        reservationRepository.saveAndFlush(existingReservation)
        val workingDays = createWorkingDays(venueId = venue.id, listOf(0, 1, 2, 3, 4, 5, 6))
        workingDaysRepository.saveAll(workingDays)

        val newDate = LocalDateTime.now().withHour(12).plusDays(2)
        val request = UpdateReservationRequest(
            numberOfGuests = null,
            reservationDate = newDate
        )

        val response: BasicResponse = reservationService.update(existingReservation.id, request)

        response.success `should be equal to` true
        response.message `should be equal to` "Reservation updated successfully."

        val updatedReservation = reservationRepository.findById(existingReservation.id).get()
        updatedReservation.numberOfGuests `should be equal to` 2
        updatedReservation.datetime `should be equal to` newDate
    }

    @Test
    fun `should fail when reservation does not exist`() {
        val request = UpdateReservationRequest(numberOfGuests = 3, reservationDate = LocalDateTime.now().plusDays(1))

        try {
            reservationService.update(9999, request)
        } catch (ex: Exception) {
            ex.message `should be equal to` "Reservation not found"
        }
    }

    @Test
    fun `should fail when request has no valid changes`() {
        val existingReservation = createReservation(userId = customer.id, venueId = venue.id, numberOfGuests = 2)
        reservationRepository.saveAndFlush(existingReservation)

        val request = UpdateReservationRequest(numberOfGuests = null, reservationDate = null)

        val response: BasicResponse = reservationService.update(existingReservation.id, request)

        response.success `should be equal to` false
        response.message `should be equal to` "Request is not valid."
    }

    @Test
    fun `should fail when reservation has no modifications`() {
        val existingReservation = createReservation(userId = customer.id, venueId = venue.id, numberOfGuests = 2)
        reservationRepository.saveAndFlush(existingReservation)

        val request = UpdateReservationRequest(numberOfGuests = 2, reservationDate = existingReservation.datetime)

        val response: BasicResponse = reservationService.update(existingReservation.id, request)

        response.success `should be equal to` false
        response.message `should be equal to` "No modifications found. Please change at least one field."
    }
}