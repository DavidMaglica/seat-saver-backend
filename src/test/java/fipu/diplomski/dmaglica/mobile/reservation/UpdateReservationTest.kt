package fipu.diplomski.dmaglica.mobile.reservation

import fipu.diplomski.dmaglica.exception.ReservationNotFoundException
import fipu.diplomski.dmaglica.exception.VenueNotFoundException
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
        private val mockedRequest = UpdateReservationRequest(
            userId = 1,
            reservationId = 1,
            reservationDate = LocalDateTime.now(),
            numberOfPeople = 3,
            venueId = 1
        )
        private val invalidRequest = UpdateReservationRequest(
            userId = 1,
            reservationId = 1,
            reservationDate = null,
            numberOfPeople = 0,
            venueId = 1
        )
        private val noChangeRequest = UpdateReservationRequest(
            userId = 1,
            reservationId = 1,
            reservationDate = null,
            numberOfPeople = 2,
            venueId = 1
        )
    }

    @Test
    fun `should return failure response if user does not exist`() {
        `when`(userRepository.findById(anyInt())).thenReturn(Optional.empty())

        val response = reservationService.update(mockedRequest)

        response.success `should be` false
        response.message `should be equal to` "User not found."

        verify(userRepository, times(1)).findById(mockedUser.id)
        verifyNoInteractions(reservationRepository)
        verifyNoMoreInteractions(userRepository)
    }

    @Test
    fun `should throw if reservation does not exist`() {
        `when`(userRepository.findById(anyInt())).thenReturn(Optional.of(mockedUser))
        `when`(reservationRepository.findById(anyInt())).thenReturn(Optional.empty())

        val exception = assertThrows<ReservationNotFoundException> {
            reservationService.update(mockedRequest)
        }

        exception.message `should be equal to` "Reservation not found"

        verify(userRepository, times(1)).findById(mockedUser.id)
        verify(reservationRepository, times(1)).findById(mockedRequest.reservationId)
        verifyNoMoreInteractions(userRepository, reservationRepository)
    }

    @Test
    fun `should return early if request is invalid`() {
        `when`(userRepository.findById(anyInt())).thenReturn(Optional.of(mockedUser))
        `when`(reservationRepository.findById(anyInt())).thenReturn(Optional.of(mockedReservation))

        val response =
            reservationService.update(invalidRequest)

        response.success `should be` false
        response.message `should be equal to` "Request is not valid."

        verify(userRepository, times(1)).findById(mockedUser.id)
        verify(reservationRepository, times(1)).findById(mockedReservation.id)
        verifyNoMoreInteractions(userRepository, reservationRepository)
    }

    @Test
    fun `should return early if request does not contain any changes`() {
        `when`(userRepository.findById(anyInt())).thenReturn(Optional.of(mockedUser))
        `when`(reservationRepository.findById(anyInt())).thenReturn(Optional.of(mockedReservation))

        val response = reservationService.update(noChangeRequest)

        response.success `should be` false
        response.message `should be equal to` "No modifications found. Please change at least one field."

        verify(userRepository, times(1)).findById(mockedUser.id)
        verify(reservationRepository, times(1)).findById(mockedReservation.id)
        verifyNoMoreInteractions(userRepository, reservationRepository)
    }

    @Test
    fun `should throw if venue does not exist`() {
        `when`(userRepository.findById(anyInt())).thenReturn(Optional.of(mockedUser))
        `when`(reservationRepository.findById(anyInt())).thenReturn(Optional.of(mockedReservation))
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.empty())

        val exception = assertThrows<VenueNotFoundException> {
            reservationService.update(mockedRequest)
        }

        exception.message `should be equal to` "Venue with id ${mockedRequest.venueId} not found"

        verify(userRepository, times(1)).findById(mockedUser.id)
        verify(reservationRepository, times(1)).findById(mockedReservation.id)
        verify(venueRepository, times(1)).findById(mockedRequest.venueId)
        verifyNoMoreInteractions(userRepository, reservationRepository, venueRepository)
    }

    @Test
    fun `should throw if unable to update reservation`() {
        `when`(userRepository.findById(anyInt())).thenReturn(Optional.of(mockedUser))
        `when`(reservationRepository.findById(anyInt())).thenReturn(Optional.of(mockedReservation))
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(mockedVenue))
        `when`(reservationRepository.save(any())).thenThrow(RuntimeException())

        val response = reservationService.update(mockedRequest)

        response.success `should be equal to` false
        response.message `should be equal to` "Error while updating reservation. Please try again later."

        verify(userRepository, times(1)).findById(mockedUser.id)
        verify(reservationRepository, times(1)).findById(mockedReservation.id)
        verify(venueRepository, times(1)).findById(mockedRequest.venueId)
        verify(reservationRepository, times(1)).save(any())
        verifyNoMoreInteractions(userRepository, reservationRepository, venueRepository)
    }

    @Test
    fun `should update reservation`() {
        `when`(userRepository.findById(anyInt())).thenReturn(Optional.of(mockedUser))
        `when`(reservationRepository.findById(anyInt())).thenReturn(Optional.of(mockedReservation))
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(mockedVenue))

        val response = reservationService.update(mockedRequest)

        response.success `should be` true
        response.message `should be equal to` "Reservation updated successfully."

        verify(reservationRepository).save(reservationArgumentCaptor.capture())
        val updatedReservation = reservationArgumentCaptor.value
        updatedReservation.venueId `should be equal to` mockedReservation.venueId
        updatedReservation.userId `should be equal to` mockedUser.id
        updatedReservation.numberOfGuests `should be equal to` mockedRequest.numberOfPeople
        updatedReservation.datetime `should be equal to` mockedRequest.reservationDate

        verify(userRepository, times(1)).findById(mockedUser.id)
        verify(reservationRepository, times(1)).findById(mockedReservation.id)
        verify(venueRepository, times(1)).findById(mockedRequest.venueId)
        verify(reservationRepository, times(1)).save(reservationArgumentCaptor.capture())
        verifyNoMoreInteractions(userRepository, reservationRepository, venueRepository)
    }
}
