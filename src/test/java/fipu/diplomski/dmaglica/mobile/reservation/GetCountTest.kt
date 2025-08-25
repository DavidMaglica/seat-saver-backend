package fipu.diplomski.dmaglica.mobile.reservation

import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
class GetCountTest : BaseReservationServiceTest() {

    companion object {
        private const val OWNER_ID = 1
        private const val VENUE_ID = 1
        private val START_DATE = LocalDateTime.now()
        private val END_DATE = LocalDateTime.now().plusDays(7)
    }

    @Test
    fun `should return 0 when no reservation found for venue`() {
        `when`(reservationRepository.countByVenueId(VENUE_ID)).thenReturn(0)

        val result = reservationService.getReservationsCount(OWNER_ID, VENUE_ID)

        result `should be equal to` 0

        verify(reservationRepository).countByVenueId(VENUE_ID)
        verifyNoMoreInteractions(reservationRepository)
        verifyNoInteractions(venueRepository)
    }

    @Test
    fun `should return correct count when reservations found for venue`() {
        `when`(reservationRepository.countByVenueId(VENUE_ID)).thenReturn(listOf(mockedReservation).size)

        val result = reservationService.getReservationsCount(OWNER_ID, VENUE_ID)

        result `should be equal to` 1

        verify(reservationRepository).countByVenueId(VENUE_ID)
        verifyNoMoreInteractions(reservationRepository)
        verifyNoInteractions(venueRepository)
    }

    @Test
    fun `should return 0 when no venues owned by owner`() {
        `when`(venueRepository.findByOwnerId(OWNER_ID)).thenReturn(emptyList())

        val result = reservationService.getReservationsCount(OWNER_ID)

        result `should be equal to` 0

        verify(venueRepository).findByOwnerId(OWNER_ID)
        verifyNoMoreInteractions(venueRepository)
        verifyNoInteractions(reservationRepository)
    }

    @Test
    fun `should return 0 when no reservations found for owner's venues`() {
        `when`(venueRepository.findByOwnerId(OWNER_ID)).thenReturn(listOf(mockedVenue))
        `when`(reservationRepository.countByVenueIdIn(listOf(mockedVenue.id))).thenReturn(0)

        val result = reservationService.getReservationsCount(OWNER_ID)

        result `should be equal to` 0

        verify(venueRepository).findByOwnerId(OWNER_ID)
        verify(reservationRepository).countByVenueIdIn(listOf(mockedVenue.id))
        verifyNoMoreInteractions(venueRepository, reservationRepository)
    }

    @Test
    fun `should return correct count when reservations found for owner's venues`() {
        `when`(venueRepository.findByOwnerId(OWNER_ID)).thenReturn(listOf(mockedVenue))
        `when`(reservationRepository.countByVenueIdIn(listOf(mockedVenue.id))).thenReturn(listOf(mockedReservation).size)

        val result = reservationService.getReservationsCount(OWNER_ID)

        result `should be equal to` 1

        verify(venueRepository).findByOwnerId(OWNER_ID)
        verify(reservationRepository).countByVenueIdIn(listOf(mockedVenue.id))
        verifyNoMoreInteractions(venueRepository, reservationRepository)
    }

    @Test
    fun `should return 0 when no reservations found in time period`() {
        `when`(venueRepository.findByOwnerId(VENUE_ID)).thenReturn(listOf(mockedVenue))
        `when`(
            reservationRepository.countByVenueIdInAndDatetimeBetween(
                listOf(VENUE_ID),
                START_DATE,
                END_DATE
            )
        ).thenReturn(0)

        val result = reservationService.getReservationsCount(VENUE_ID, startDate = START_DATE, endDate = END_DATE)

        result `should be equal to` 0

        verify(venueRepository).findByOwnerId(OWNER_ID)
        verify(reservationRepository).countByVenueIdInAndDatetimeBetween(listOf(VENUE_ID), START_DATE, END_DATE)
        verifyNoMoreInteractions(reservationRepository, venueRepository)
    }

    @Test
    fun `should return correct count when reservations found in time period`() {
        `when`(venueRepository.findByOwnerId(OWNER_ID)).thenReturn(listOf(mockedVenue))
        `when`(
            reservationRepository.countByVenueIdInAndDatetimeBetween(
                listOf(VENUE_ID),
                START_DATE,
                END_DATE
            )
        ).thenReturn(listOf(mockedReservation).size)

        val result = reservationService.getReservationsCount(OWNER_ID, startDate = START_DATE, endDate = END_DATE)

        result `should be equal to` 1

        verify(venueRepository).findByOwnerId(OWNER_ID)
        verify(reservationRepository).countByVenueIdInAndDatetimeBetween(listOf(VENUE_ID), START_DATE, END_DATE)
        verifyNoMoreInteractions(reservationRepository, venueRepository)
    }
}