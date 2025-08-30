package fipu.diplomski.dmaglica.mobile.unit.venue

import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
class GetOverallRatingTest : BaseVenueServiceTest() {

    private val venue1 = createVenue(1, averageRating = 5.0)
    private val venue2 = createVenue(2, averageRating = 3.0)

    @Test
    fun `should return 0 when no venues owned`() {
        `when`(venueRepository.findByOwnerId(venue1.ownerId)).thenReturn(emptyList())

        val result = venueService.getOverallRating(venue1.ownerId)

        result `should be equal to` 0.0

        verify(venueRepository).findByOwnerId(venue1.ownerId)
        verifyNoMoreInteractions(venueRepository)
    }

    @Test
    fun `should return correct overall rating when venues owned`() {
        `when`(venueRepository.findByOwnerId(venue1.ownerId)).thenReturn(listOf(venue1, venue2))

        val result = venueService.getOverallRating(venue1.ownerId)

        val averageRating = (venue1.averageRating + venue2.averageRating) / 2
        result `should be equal to` averageRating

        verify(venueRepository).findByOwnerId(venue1.ownerId)
        verifyNoMoreInteractions(venueRepository)
    }
}