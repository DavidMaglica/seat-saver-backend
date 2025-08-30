package fipu.diplomski.dmaglica.mobile.unit.reservation

import org.amshove.kluent.`should be`
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles
import java.util.*
import kotlin.test.Test

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
class GetByIdTest : BaseReservationServiceTest() {

    companion object {
        private const val RESERVATION_ID = 1
    }

    @Test
    fun `should return null when no reservation found`() {
        `when`(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.empty())

        val result = reservationService.getById(RESERVATION_ID)

        result `should be` null

        verify(reservationRepository).findById(RESERVATION_ID)
        verifyNoMoreInteractions(reservationRepository)
    }

    @Test
    fun `should return reservation DTO when found`() {
        `when`(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(mockedReservation))
        val result = reservationService.getById(RESERVATION_ID)

        result!!.id `should be` mockedReservation.id
        result.userId `should be` mockedReservation.userId
        result.venueId `should be` mockedReservation.venueId
        result.datetime `should be` mockedReservation.datetime
        result.numberOfGuests `should be` mockedReservation.numberOfGuests

        verify(reservationRepository).findById(RESERVATION_ID)
        verifyNoMoreInteractions(reservationRepository)
    }
}
