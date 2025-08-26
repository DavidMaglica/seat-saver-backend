package fipu.diplomski.dmaglica.mobile.reservation

import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles
import java.util.*
import kotlin.test.Test

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
class GetByVenueTest : BaseReservationServiceTest() {

    companion object {
        private const val VENUE_ID = 1
    }

    @Test
    fun `should return empty list when venue not found`() {
        `when`(venueRepository.findById(VENUE_ID)).thenReturn(Optional.empty())

        val result = reservationService.getByVenueId(VENUE_ID)

        result `should be equal to` emptyList()

        verify(venueRepository).findById(VENUE_ID)
        verifyNoMoreInteractions(venueRepository)
        verifyNoInteractions(reservationRepository)
    }

    @Test
    fun `should return empty list when no reservations found for venue`() {
        `when`(venueRepository.findById(VENUE_ID)).thenReturn(Optional.of(mockedVenue))
        `when`(reservationRepository.findByVenueId(VENUE_ID)).thenReturn(emptyList())

        val result = reservationService.getByVenueId(VENUE_ID)

        result `should be equal to` emptyList()

        verify(venueRepository).findById(VENUE_ID)
        verify(reservationRepository).findByVenueId(VENUE_ID)
        verifyNoMoreInteractions(venueRepository, reservationRepository)
    }

    @Test
    fun `should return list of reservations when reservations found for venue`() {
        `when`(venueRepository.findById(VENUE_ID)).thenReturn(Optional.of(mockedVenue))
        `when`(reservationRepository.findByVenueId(VENUE_ID)).thenReturn(listOf(mockedReservation))

        val result = reservationService.getByVenueId(VENUE_ID)

        result.size `should be equal to` 1
        result[0].id `should be equal to` mockedReservation.id
        result[0].userId `should be equal to` mockedReservation.userId
        result[0].venueId `should be equal to` mockedReservation.venueId
        result[0].datetime `should be equal to` mockedReservation.datetime
        result[0].numberOfGuests `should be equal to` mockedReservation.numberOfGuests

        verify(venueRepository).findById(VENUE_ID)
        verify(reservationRepository).findByVenueId(VENUE_ID)
        verifyNoMoreInteractions(venueRepository, reservationRepository)
    }
}