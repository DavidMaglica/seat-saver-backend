package fipu.diplomski.dmaglica.mobile.reservation

import fipu.diplomski.dmaglica.exception.ReservationNotFoundException
import fipu.diplomski.dmaglica.exception.VenueNotFoundException
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles
import java.util.*

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
class DeleteReservationTest : BaseReservationServiceTest() {

    @Test
    fun `should return failure response if user does not exist`() {
        `when`(userRepository.findById(anyInt())).thenReturn(Optional.empty())

        val response = reservationService.delete(mockedUser.id, mockedReservation.id, mockedReservation.venueId)

        response.success `should be equal to` false
        response.message `should be equal to` "User not found."

        verify(userRepository, times(1)).findById(mockedUser.id)
        verifyNoMoreInteractions(userRepository)
        verifyNoInteractions(venueRepository)
        verifyNoInteractions(reservationRepository)
    }

    @Test
    fun `should throw if venue does not exist`() {
        `when`(userRepository.findById(anyInt())).thenReturn(Optional.of(mockedUser))
        `when`(reservationRepository.findById(anyInt())).thenReturn(Optional.of(mockedReservation))
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.empty())

        val exception = assertThrows<VenueNotFoundException> {
            reservationService.delete(mockedUser.id, mockedReservation.id, mockedReservation.venueId)
        }

        exception.message `should be equal to` "Venue with id ${mockedReservation.venueId} not found"

        verify(userRepository, times(1)).findById(mockedUser.id)
        verify(venueRepository, times(1)).findById(mockedReservation.venueId)
        verify(reservationRepository, times(1)).findById(mockedReservation.id)
        verifyNoMoreInteractions(userRepository, venueRepository, reservationRepository)
    }

    @Test
    fun `should throw if reservation does not exist`() {
        `when`(userRepository.findById(anyInt())).thenReturn(Optional.of(mockedUser))
        `when`(reservationRepository.findById(anyInt())).thenReturn(Optional.empty())

        val exception = assertThrows<ReservationNotFoundException> {
            reservationService.delete(mockedUser.id, mockedReservation.id, mockedReservation.venueId)
        }

        exception.message `should be equal to` "Reservation with id ${mockedReservation.id} not found"

        verify(userRepository, times(1)).findById(mockedUser.id)
        verify(reservationRepository, times(1)).findById(mockedReservation.id)
        verifyNoInteractions(venueRepository)
        verifyNoMoreInteractions(userRepository, reservationRepository)
    }

    @Test
    fun `should throw if unable to delete`() {
        `when`(userRepository.findById(anyInt())).thenReturn(Optional.of(mockedUser))
        `when`(reservationRepository.findById(anyInt())).thenReturn(Optional.of(mockedReservation))
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(mockedVenue))
        `when`(reservationRepository.deleteById(anyInt())).thenThrow(RuntimeException())

        val response = reservationService.delete(mockedUser.id, mockedReservation.id, mockedReservation.venueId)

        response.success `should be equal to` false
        response.message `should be equal to` "Error while deleting reservation. Please try again later."

        verify(userRepository, times(1)).findById(mockedUser.id)
        verify(venueRepository, times(1)).findById(mockedReservation.venueId)
        verify(reservationRepository, times(1)).deleteById(mockedReservation.id)
        verifyNoMoreInteractions(userRepository, venueRepository, reservationRepository)
    }

    @Test
    fun `should delete reservation`() {
        `when`(userRepository.findById(anyInt())).thenReturn(Optional.of(mockedUser))
        `when`(reservationRepository.findById(anyInt())).thenReturn(Optional.of(mockedReservation))
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(mockedVenue))

        val response = reservationService.delete(mockedUser.id, mockedReservation.id, mockedReservation.venueId)

        response.success `should be equal to` true
        response.message `should be equal to` "Reservation deleted successfully."

        verify(userRepository, times(1)).findById(mockedUser.id)
        verify(venueRepository, times(1)).findById(mockedReservation.venueId)
        verify(reservationRepository, times(1)).findById(mockedReservation.id)
        verify(reservationRepository, times(1)).deleteById(mockedReservation.id)
        verifyNoMoreInteractions(userRepository, venueRepository, reservationRepository)
    }
}
