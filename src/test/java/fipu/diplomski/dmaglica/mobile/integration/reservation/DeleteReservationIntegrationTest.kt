package fipu.diplomski.dmaglica.mobile.integration.reservation

import fipu.diplomski.dmaglica.model.response.BasicResponse
import jakarta.transaction.Transactional
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test

@Transactional
class DeleteReservationIntegrationTest : AbstractReservationIntegrationTest() {

    @Test
    fun `should delete reservation successfully`() {
        val reservation = createReservation(userId = customer.id, venueId = venue.id, numberOfGuests = 2)
        reservationRepository.saveAndFlush(reservation)

        val response: BasicResponse = reservationService.delete(reservation.id)

        response.success `should be equal to` true
        response.message `should be equal to` "Reservation deleted successfully."

        reservationRepository.findById(reservation.id).isPresent `should be equal to` false
    }

    @Test
    fun `should fail when trying to delete non-existent reservation`() {
        val nonExistentReservationId = 9999

        try {
            reservationService.delete(nonExistentReservationId)
        } catch (ex: Exception) {
            ex.message `should be equal to` "Reservation with id $nonExistentReservationId not found"
        }
    }
}
