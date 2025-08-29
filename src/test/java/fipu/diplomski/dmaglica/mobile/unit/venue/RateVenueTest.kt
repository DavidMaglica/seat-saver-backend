package fipu.diplomski.dmaglica.mobile.unit.venue

import jakarta.persistence.EntityNotFoundException
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles
import java.util.*

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
class RateVenueTest : BaseVenueServiceTest() {

    private val venue = createVenue()
    private val user = createUser()
    private val rating = createRating(venueId = venue.id, rating = 4.0)

    @Test
    fun `should return early if rating is not valid`() {
        val response = venueService.rate(venue.id, 6.0, 1, null)

        response.success `should be` false
        response.message `should be equal to` "Rating must be between 1.0 and 5.0."

        verifyNoInteractions(userRepository, venueRatingRepository, venueRepository)
    }

    @Test
    fun `should throw if venue not found`() {
        `when`(userRepository.findById(anyInt())).thenReturn(Optional.of(user))
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.empty())

        val exception = assertThrows<EntityNotFoundException> {
            venueService.rate(venue.id, 3.0, 1, null)
        }

        exception.message?.let { it `should be equal to` "Venue with id ${venue.id} not found" }

        verify(userRepository, times(1)).findById(user.id)
        verify(venueRepository, times(1)).findById(venue.id)
        verifyNoMoreInteractions(userRepository, venueRepository)
        verifyNoInteractions(venueRatingRepository)
    }

    @Test
    fun `should return failure response if saving rating fails`() {
        `when`(userRepository.findById(anyInt())).thenReturn(Optional.of(user))
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(venue))
        `when`(venueRatingRepository.findByVenueId(anyInt())).thenReturn(listOf(rating))
        `when`(venueRatingRepository.save(any())).thenThrow(RuntimeException())

        val response = venueService.rate(venue.id, 3.0, 1, null)

        response.success `should be equal to` false
        response.message `should be equal to` "Error while updating rating. Please try again later."

        verify(userRepository, times(1)).findById(user.id)
        verify(venueRepository, times(1)).findById(venue.id)
        verify(venueRatingRepository, times(1)).findByVenueId(venue.id)
        verify(venueRatingRepository, times(1)).save(venueRatingArgumentCaptor.capture())
        verifyNoMoreInteractions(userRepository, venueRatingRepository, venueRepository)
    }

    @Test
    fun `should return failure response if saving venue fails`() {
        `when`(userRepository.findById(anyInt())).thenReturn(Optional.of(user))
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(venue))
        `when`(venueRatingRepository.findByVenueId(anyInt())).thenReturn(listOf(rating))
        `when`(venueRepository.save(any())).thenThrow(RuntimeException())

        val response = venueService.rate(venue.id, 3.0, 1, null)

        response.success `should be equal to` false
        response.message `should be equal to` "Error while updating venue after rating. Please try again later."

        verify(userRepository, times(1)).findById(user.id)
        verify(venueRepository, times(1)).findById(venue.id)
        verify(venueRatingRepository, times(1)).findByVenueId(venue.id)
        verify(venueRatingRepository, times(1)).save(venueRatingArgumentCaptor.capture())
        verify(venueRepository, times(1)).save(venueArgumentCaptor.capture())
        verifyNoMoreInteractions(userRepository, venueRatingRepository, venueRepository)
    }

    @Test
    fun `should insert new rating and update if does not exist earlier`() {
        `when`(userRepository.findById(anyInt())).thenReturn(Optional.of(user))
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(venue))
        `when`(venueRatingRepository.findByVenueId(anyInt())).thenReturn(emptyList())

        val response = venueService.rate(venue.id, 3.0, 1, null)

        response.success `should be` true
        response.message `should be equal to` "Venue with id ${venue.id} successfully rated with rating 3.0."

        verify(venueRatingRepository).save(venueRatingArgumentCaptor.capture())
        val venueRating = venueRatingArgumentCaptor.value
        venueRating.venueId `should be equal to` venue.id
        venueRating.rating `should be equal to` 3.0
        venueRating.comment `should be equal to` null

        verify(venueRepository).save(venueArgumentCaptor.capture())
        val updatedVenue = venueArgumentCaptor.value
        updatedVenue.id `should be equal to` venue.id
        updatedVenue.averageRating `should be equal to` 3.0

        verify(venueRepository, times(1)).findById(venue.id)
        verify(venueRatingRepository, times(1)).findByVenueId(venue.id)
        verify(venueRatingRepository, times(1)).save(venueRatingArgumentCaptor.capture())
        verify(venueRepository, times(1)).save(venueArgumentCaptor.capture())
        verifyNoMoreInteractions(userRepository, venueRatingRepository, venueRepository)
    }

    @Test
    fun `should insert new rating and calculate new average correctly`() {
        `when`(userRepository.findById(anyInt())).thenReturn(Optional.of(user))
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(venue))
        `when`(venueRatingRepository.findByVenueId(anyInt())).thenReturn(listOf(rating))

        val response = venueService.rate(venue.id, 5.0, 1, null)

        response.success `should be` true
        response.message `should be equal to` "Venue with id ${venue.id} successfully rated with rating 5.0."

        verify(venueRatingRepository).save(venueRatingArgumentCaptor.capture())
        val venueRating = venueRatingArgumentCaptor.value
        venueRating.venueId `should be equal to` venue.id
        venueRating.rating `should be equal to` 5.0
        venueRating.comment `should be equal to` null

        verify(venueRepository).save(venueArgumentCaptor.capture())
        val updatedVenue = venueArgumentCaptor.value
        updatedVenue.id `should be equal to` venue.id
        updatedVenue.averageRating `should be equal to` 4.5

        verify(venueRepository, times(1)).findById(venue.id)
        verify(venueRatingRepository, times(1)).findByVenueId(venue.id)
        verify(venueRatingRepository, times(1)).save(venueRatingArgumentCaptor.capture())
        verify(venueRepository, times(1)).save(venueArgumentCaptor.capture())
        verifyNoMoreInteractions(userRepository, venueRatingRepository, venueRepository)
    }

    @Test
    fun `should insert new rating with comment and update average rating correctly`() {
        `when`(userRepository.findById(anyInt())).thenReturn(Optional.of(user))
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(venue))
        `when`(venueRatingRepository.findByVenueId(anyInt())).thenReturn(listOf(rating))

        val response = venueService.rate(venue.id, 5.0, 1, "Great venue!")

        response.success `should be` true
        response.message `should be equal to` "Venue with id ${venue.id} successfully rated with rating 5.0."

        verify(venueRatingRepository).save(venueRatingArgumentCaptor.capture())
        val venueRating = venueRatingArgumentCaptor.value
        venueRating.venueId `should be equal to` venue.id
        venueRating.rating `should be equal to` 5.0
        venueRating.comment `should be equal to` "Great venue!"

        verify(venueRepository).save(venueArgumentCaptor.capture())
        val updatedVenue = venueArgumentCaptor.value
        updatedVenue.id `should be equal to` venue.id
        updatedVenue.averageRating `should be equal to` 4.5

        verify(venueRepository, times(1)).findById(venue.id)
        verify(venueRatingRepository, times(1)).findByVenueId(venue.id)
        verify(venueRatingRepository, times(1)).save(venueRatingArgumentCaptor.capture())
        verify(venueRepository, times(1)).save(venueArgumentCaptor.capture())
        verifyNoMoreInteractions(userRepository, venueRatingRepository, venueRepository)
    }
}
