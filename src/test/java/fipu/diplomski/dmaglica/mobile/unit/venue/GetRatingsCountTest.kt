package fipu.diplomski.dmaglica.mobile.unit.venue

import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
class GetRatingsCountTest : BaseVenueServiceTest() {

    companion object Companion {
        private const val OWNER_ID = 1
    }

    private val venue = createVenue(1, OWNER_ID)
    private val reservation = createReservation()

    @Test
    fun `should return 0 when no venues are owned`() {
        `when`(venueRepository.findByOwnerId(OWNER_ID)).thenReturn(emptyList())

        val result = venueService.getRatingsCount(OWNER_ID)

        result `should be equal to` 0

        verify(venueRepository).findByOwnerId(OWNER_ID)
        verifyNoMoreInteractions(venueRepository)
    }

    @Test
    fun `fun should return 0 when no reservations are found for owned venues`() {
        `when`(venueRepository.findByOwnerId(OWNER_ID)).thenReturn(listOf(venue))
        `when`(venueRatingRepository.countByVenueIdIn(listOf(venue.id))).thenReturn(0)

        val result = venueService.getRatingsCount(OWNER_ID)

        result `should be equal to` 0

        verify(venueRepository).findByOwnerId(OWNER_ID)
        verify(venueRatingRepository).countByVenueIdIn(listOf(venue.id))
        verifyNoMoreInteractions(venueRepository)
    }

    @Test
    fun `fun should return correct count when reservations are found for owned venues`() {
        `when`(venueRepository.findByOwnerId(OWNER_ID)).thenReturn(listOf(venue))
        `when`(venueRatingRepository.countByVenueIdIn(listOf(venue.id))).thenReturn(listOf(reservation).size)

        val result = venueService.getRatingsCount(OWNER_ID)

        result `should be equal to` 1

        verify(venueRepository).findByOwnerId(OWNER_ID)
        verify(venueRatingRepository).countByVenueIdIn(listOf(venue.id))
        verifyNoMoreInteractions(venueRepository, venueRatingRepository)
    }
}