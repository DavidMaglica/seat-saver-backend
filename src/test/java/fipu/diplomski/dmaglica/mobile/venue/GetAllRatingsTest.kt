package fipu.diplomski.dmaglica.mobile.venue

import fipu.diplomski.dmaglica.util.toDto
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
class GetAllRatingsTest : BaseVenueServiceTest() {

    companion object Companion {
        private const val VENUE_ID = 1
    }

    private val rating1 = createRating(1, VENUE_ID, 5.0)
    private val rating2 = createRating(2, VENUE_ID, 4.0)

    @Test
    fun `should return empty list when no ratings exist for venue`() {
        `when`(venueRatingRepository.findByVenueId(VENUE_ID)).thenReturn(emptyList())

        val result = venueService.getAllRatings(VENUE_ID)

        result `should be equal to` emptyList()

        verify(venueRatingRepository).findByVenueId(VENUE_ID)
        verifyNoMoreInteractions(venueRatingRepository)
    }

    @Test
    fun `should get all ratings for a venue sorted by descending`() {
        `when`(venueRatingRepository.findByVenueId(VENUE_ID)).thenReturn(listOf(rating1, rating2))

        val result = venueService.getAllRatings(VENUE_ID)

        result.size `should be equal to` 2
        result[0] `should be equal to` rating2.toDto()
        result[0].venueId `should be equal to` rating2.venueId
        result[0].rating `should be equal to` rating2.rating
        result[1] `should be equal to` rating1.toDto()
        result[1].venueId `should be equal to` rating1.venueId
        result[1].rating `should be equal to` rating1.rating



        verify(venueRatingRepository).findByVenueId(VENUE_ID)
        verifyNoMoreInteractions(venueRatingRepository)
    }
}