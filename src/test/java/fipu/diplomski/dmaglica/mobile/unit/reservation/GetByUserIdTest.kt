package fipu.diplomski.dmaglica.mobile.unit.reservation

import org.amshove.kluent.`should be`
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles
import java.util.*
import kotlin.test.Test

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
class GetByUserIdTest : BaseReservationServiceTest() {

    companion object {
        private const val USER_ID = 1
    }

    @Test
    fun `should return empty list when user not found`() {
        `when`(userRepository.findById(USER_ID)).thenReturn(Optional.empty())

        val result = reservationService.getByUserId(USER_ID)

        result `should be` emptyList()

        verify(userRepository).findById(USER_ID)
        verifyNoMoreInteractions(userRepository)
        verifyNoInteractions(reservationRepository)
    }

    @Test
    fun `should return empty list when no reservations found for user`() {
        `when`(userRepository.findById(USER_ID)).thenReturn(Optional.of(mockedUser))
        `when`(reservationRepository.findByUserId(USER_ID)).thenReturn(emptyList())

        val result = reservationService.getByUserId(USER_ID)

        result `should be equal to` emptyList()

        verify(userRepository).findById(USER_ID)
        verify(reservationRepository).findByUserId(USER_ID)
        verifyNoMoreInteractions(userRepository, reservationRepository)
    }

    @Test
    fun `should return list of reservations when reservations found for user`() {
        `when`(userRepository.findById(USER_ID)).thenReturn(Optional.of(mockedUser))
        `when`(reservationRepository.findByUserId(USER_ID)).thenReturn(listOf(mockedReservation))

        val result = reservationService.getByUserId(USER_ID)

        result.size `should be` 1
        result[0].id `should be equal to` mockedReservation.id
        result[0].userId `should be equal to` mockedReservation.userId
        result[0].venueId `should be equal to` mockedReservation.venueId
        result[0].datetime `should be equal to` mockedReservation.datetime
        result[0].numberOfGuests `should be equal to` mockedReservation.numberOfGuests

        verify(userRepository).findById(USER_ID)
        verify(reservationRepository).findByUserId(USER_ID)
        verifyNoMoreInteractions(userRepository, reservationRepository)
    }
}