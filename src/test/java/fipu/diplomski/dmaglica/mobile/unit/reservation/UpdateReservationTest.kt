package fipu.diplomski.dmaglica.mobile.unit.reservation

import fipu.diplomski.dmaglica.exception.ReservationNotFoundException
import fipu.diplomski.dmaglica.model.request.UpdateReservationRequest
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
class UpdateReservationTest : BaseReservationServiceTest() {

    companion object {
        private const val MOCK_RESERVATION_ID = 1
        private val mockedRequest = UpdateReservationRequest(
            reservationDate = LocalDateTime.now(),
            numberOfGuests = 3,
        )
        private val invalidRequest = UpdateReservationRequest(
            reservationDate = null,
            numberOfGuests = 0,
        )
        private val noChangeRequest = UpdateReservationRequest(
            reservationDate = null,
            numberOfGuests = 2,
        )
    }

    @Test
    fun `should throw if reservation does not exist`() {
        `when`(reservationRepository.findById(anyInt())).thenReturn(Optional.empty())

        val exception = assertThrows<ReservationNotFoundException> {
            reservationService.update(MOCK_RESERVATION_ID, mockedRequest)
        }

        exception.message `should be equal to` "Reservation not found"

        verify(reservationRepository, times(1)).findById(MOCK_RESERVATION_ID)
        verifyNoMoreInteractions(reservationRepository)
    }

    @Test
    fun `should return early if request is invalid`() {
        `when`(reservationRepository.findById(anyInt())).thenReturn(Optional.of(mockedReservation))

        val response =
            reservationService.update(MOCK_RESERVATION_ID, invalidRequest)

        response.success `should be` false
        response.message `should be equal to` "Request is not valid."

        verify(reservationRepository, times(1)).findById(mockedReservation.id)
        verifyNoMoreInteractions(reservationRepository)
    }

    @Test
    fun `should return early if request does not contain any changes`() {
        `when`(reservationRepository.findById(anyInt())).thenReturn(Optional.of(mockedReservation))

        val response = reservationService.update(MOCK_RESERVATION_ID, noChangeRequest)

        response.success `should be` false
        response.message `should be equal to` "No modifications found. Please change at least one field."

        verify(reservationRepository, times(1)).findById(mockedReservation.id)
        verifyNoMoreInteractions(reservationRepository)
    }

    @Test
    fun `should throw if unable to update reservation`() {
        `when`(reservationRepository.findById(anyInt())).thenReturn(Optional.of(mockedReservation))
        `when`(reservationRepository.save(any())).thenThrow(RuntimeException())

        val response = reservationService.update(MOCK_RESERVATION_ID, mockedRequest)

        response.success `should be equal to` false
        response.message `should be equal to` "Error while updating reservation. Please try again later."

        verify(reservationRepository, times(1)).findById(mockedReservation.id)
        verify(reservationRepository, times(1)).save(any())
        verifyNoMoreInteractions(reservationRepository)
    }

    @Test
    fun `should update reservation`() {
        `when`(reservationRepository.findById(anyInt())).thenReturn(Optional.of(mockedReservation))

        val response = reservationService.update(MOCK_RESERVATION_ID, mockedRequest)

        response.success `should be` true
        response.message `should be equal to` "Reservation updated successfully."

        verify(reservationRepository).save(reservationArgumentCaptor.capture())
        val updatedReservation = reservationArgumentCaptor.value
        updatedReservation.venueId `should be equal to` mockedReservation.venueId
        updatedReservation.userId `should be equal to` mockedUser.id
        updatedReservation.numberOfGuests `should be equal to` mockedRequest.numberOfGuests
        updatedReservation.datetime `should be equal to` mockedRequest.reservationDate

        verify(reservationRepository, times(1)).findById(mockedReservation.id)
        verify(reservationRepository, times(1)).save(reservationArgumentCaptor.capture())
        verifyNoMoreInteractions(reservationRepository)
    }
}
