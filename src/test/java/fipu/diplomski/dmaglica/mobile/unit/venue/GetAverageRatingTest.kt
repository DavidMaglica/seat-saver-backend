package fipu.diplomski.dmaglica.mobile.unit.venue

import jakarta.persistence.EntityNotFoundException
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
class GetAverageRatingTest : BaseVenueServiceTest() {

    private val venue = createVenue(averageRating = 3.5)

    @Test
    fun `should throw exception when venue does not exist`() {
        `when`(venueRepository.findById(venue.id)).thenReturn(Optional.empty())

        val exception = assertThrows<EntityNotFoundException> {
            venueService.getVenueAverageRating(venue.id)
        }

        exception.message `should be equal to` "Venue with id: ${venue.id} not found."

        verify(venueRepository).findById(venue.id)
        verifyNoMoreInteractions(venueRepository)
    }

    @Test
    fun `should return average rating when venue has ratings`() {
        `when`(venueRepository.findById(venue.id)).thenReturn(Optional.of(venue))

        val result = venueService.getVenueAverageRating(venue.id)

        result `should be equal to` 3.5

        verify(venueRepository).findById(venue.id)
        verifyNoMoreInteractions(venueRepository)
    }

}