package fipu.diplomski.dmaglica.mobile.reservation

import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles
import java.util.Collections.emptyList

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
class GetByOwnerTest : BaseReservationServiceTest() {

    companion object {
        private const val OWNER_ID = 1
    }

    @Test
    fun `should return empty list when no venues owned`() {
        `when`(venueRepository.findByOwnerId(OWNER_ID)).thenReturn(emptyList())

        val result = reservationService.getByOwnerId(OWNER_ID)

        result `should be equal to` emptyList()

        verify(venueRepository).findByOwnerId(OWNER_ID)
        verifyNoMoreInteractions(venueRepository)
        verifyNoInteractions(reservationRepository)
    }

    @Test
    fun `should return empty list when no reservations found`() {
        `when`(venueRepository.findByOwnerId(OWNER_ID)).thenReturn(listOf(mockedVenue))
        `when`(reservationRepository.findByVenueIdIn(listOf(mockedVenue.id))).thenReturn(emptyList())

        val result = reservationService.getByOwnerId(OWNER_ID)

        result `should be equal to` emptyList()

        verify(venueRepository).findByOwnerId(OWNER_ID)
        verify(reservationRepository).findByVenueIdIn(listOf(mockedVenue.id))
        verifyNoMoreInteractions(venueRepository, reservationRepository)
    }

    @Test
    fun `should return list of reservations when found`() {
        `when`(venueRepository.findByOwnerId(OWNER_ID)).thenReturn(listOf(mockedVenue))
        `when`(reservationRepository.findByVenueIdIn(listOf(mockedVenue.id))).thenReturn(listOf(mockedReservation))

        val result = reservationService.getByOwnerId(OWNER_ID)

        result.size `should be equal to` 1
        result[0].id `should be equal to` mockedReservation.id
        result[0].userId `should be equal to` mockedReservation.userId
        result[0].venueId `should be equal to` mockedReservation.venueId
        result[0].datetime `should be equal to` mockedReservation.datetime
        result[0].numberOfGuests `should be equal to` mockedReservation.numberOfGuests

        verify(venueRepository).findByOwnerId(OWNER_ID)
        verify(reservationRepository).findByVenueIdIn(listOf(mockedVenue.id))
        verifyNoMoreInteractions(venueRepository, reservationRepository)
    }

}
