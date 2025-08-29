package fipu.diplomski.dmaglica.mobile.unit.reservation

import fipu.diplomski.dmaglica.exception.ReservationNotFoundException
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
    fun `should throw if reservation does not exist`() {
        `when`(reservationRepository.findById(anyInt())).thenReturn(Optional.empty())

        val exception = assertThrows<ReservationNotFoundException> {
            reservationService.delete(mockedReservation.id)
        }

        exception.message `should be equal to` "Reservation with id ${mockedReservation.id} not found"

        verify(reservationRepository, times(1)).findById(mockedReservation.id)
        verifyNoMoreInteractions(reservationRepository)
    }

    @Test
    fun `should throw if unable to delete`() {
        `when`(reservationRepository.findById(anyInt())).thenReturn(Optional.of(mockedReservation))
        `when`(reservationRepository.deleteById(anyInt())).thenThrow(RuntimeException())

        val response = reservationService.delete(mockedReservation.id)

        response.success `should be equal to` false
        response.message `should be equal to` "Error while deleting reservation. Please try again later."

        verify(reservationRepository, times(1)).deleteById(mockedReservation.id)
        verifyNoMoreInteractions(reservationRepository)
    }

    @Test
    fun `should delete reservation`() {
        `when`(reservationRepository.findById(anyInt())).thenReturn(Optional.of(mockedReservation))

        val response = reservationService.delete(mockedReservation.id)

        response.success `should be equal to` true
        response.message `should be equal to` "Reservation deleted successfully."

        verify(reservationRepository, times(1)).findById(mockedReservation.id)
        verify(reservationRepository, times(1)).deleteById(mockedReservation.id)
        verifyNoMoreInteractions(reservationRepository)
    }
}
