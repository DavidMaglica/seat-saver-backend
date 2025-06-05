package fipu.diplomski.dmaglica.mobile.reservation

import fipu.diplomski.dmaglica.exception.VenueNotFoundException
import fipu.diplomski.dmaglica.model.request.CreateReservationRequest
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles
import java.sql.SQLException
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
class CreateReservationTest : BaseReservationServiceTest() {

    companion object {
        private val mockedRequest = CreateReservationRequest(
            userId = 1,
            venueId = 1,
            reservationDate = LocalDateTime.now(),
            numberOfPeople = 2,
        )
    }

    @Test
    fun `should return failure response if user does not exist`() {
        `when`(userRepository.findById(anyInt())).thenReturn(Optional.empty())

        val result = reservationService.create(mockedRequest)

        result.success `should be equal to` false
        result.message `should be equal to` "User not found."

        verify(userRepository, times(1)).findById(mockedUser.id)
        verifyNoInteractions(venueRepository)
        verifyNoInteractions(reservationRepository)
        verifyNoMoreInteractions(userRepository)
    }

    @Test
    fun `should throw if venue does not exist`() {
        `when`(userRepository.findById(anyInt())).thenReturn(Optional.of(mockedUser))
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.empty())

        val exception = assertThrows<VenueNotFoundException> {
            reservationService.create(mockedRequest)
        }

        exception.message `should be equal to` "Venue with id ${mockedRequest.venueId} not found"

        verify(userRepository, times(1)).findById(mockedUser.id)
        verify(venueRepository, times(1)).findById(mockedVenue.id)
        verifyNoInteractions(reservationRepository)
        verifyNoMoreInteractions(userRepository, venueRepository)
    }

    @Test
    fun `should throw if unable to save reservation`() {
        `when`(userRepository.findById(anyInt())).thenReturn(Optional.of(mockedUser))
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(mockedVenue))
        `when`(reservationRepository.save(any())).thenThrow(RuntimeException())

        val exception = assertThrows<SQLException> {
            reservationService.create(mockedRequest)
        }

        exception.message `should be equal to` "Error while creating reservation. Please try again later."

        verify(userRepository, times(1)).findById(mockedUser.id)
        verify(venueRepository, times(1)).findById(mockedVenue.id)
        verify(reservationRepository, times(1)).save(any())
        verifyNoMoreInteractions(userRepository, venueRepository, reservationRepository)
    }

    @Test
    fun `should create reservation`() {
        `when`(userRepository.findById(anyInt())).thenReturn(Optional.of(mockedUser))
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(mockedVenue))
        `when`(reservationRepository.save(any())).thenReturn(mockedReservation)

        val response = reservationService.create(mockedRequest)

        response.success `should be equal to` true
        response.message `should be equal to` "Reservation created successfully"

        verify(reservationRepository).save(reservationArgumentCaptor.capture())
        val reservation = reservationArgumentCaptor.value
        reservation.userId `should be equal to` mockedUser.id
        reservation.venueId `should be equal to` mockedRequest.venueId
        reservation.datetime `should be equal to` mockedRequest.reservationDate
        reservation.numberOfGuests `should be equal to` mockedRequest.numberOfPeople

        verify(userRepository, times(1)).findById(mockedUser.id)
        verify(venueRepository, times(1)).findById(mockedVenue.id)
        verify(reservationRepository, times(1)).save(reservationArgumentCaptor.capture())
        verifyNoMoreInteractions(userRepository, venueRepository, reservationRepository)
    }
}
