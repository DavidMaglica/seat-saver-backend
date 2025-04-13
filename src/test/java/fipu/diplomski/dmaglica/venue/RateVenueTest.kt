package fipu.diplomski.dmaglica.venue

import fipu.diplomski.dmaglica.repo.entity.VenueEntity
import fipu.diplomski.dmaglica.repo.entity.VenueRatingEntity
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles
import java.sql.SQLException
import java.util.*

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
class RateVenueTest : VenueServiceTest() {

    companion object {
        private val updatedRatingCaptor = ArgumentCaptor.forClass(VenueRatingEntity::class.java)
        private val updatedVenueCaptor = ArgumentCaptor.forClass(VenueEntity::class.java)
    }

    @Test
    fun `should return early if rating is not valid`() {
        val response = venueService.rate(mockedVenue.id, 6.0)

        response.success `should be` false
        response.message `should be equal to` "Rating must be between 1 and 5"

        verifyNoInteractions(venueRatingRepository, venueRepository)
    }

    @Test
    fun `should throw if venue not found`() {
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.empty())

        val exception = assertThrows<SQLException> {
            venueService.rate(mockedVenue.id, 3.0)
        }

        exception.message?.let { it `should be equal to` "Venue with id ${mockedVenue.id} not found" }

        verify(venueRepository, times(1)).findById(mockedVenue.id)
        verifyNoInteractions(venueRatingRepository)
    }

    @Test
    fun `should throw if saving rating entity fails`() {
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(mockedVenue))
        `when`(venueRatingRepository.save(any())).thenThrow(RuntimeException())

        val exception = assertThrows<SQLException> {
            venueService.rate(mockedVenue.id, 3.0)
        }

        exception.message?.let { it `should be equal to` "Error while updating rating for venue with id ${mockedVenue.id}" }

        verify(venueRepository, times(1)).findById(mockedVenue.id)
        verify(venueRatingRepository, times(1)).save(any())
    }

    @Test
    fun `should throw if saving updated rating fails`() {
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(mockedVenue))
        `when`(venueRatingRepository.findByVenueId(anyInt())).thenReturn(listOf(mockedRating))
        `when`(venueRatingRepository.save(updatedRatingCaptor.capture())).thenThrow(RuntimeException())

        val exception = assertThrows<SQLException> {
            venueService.rate(mockedVenue.id, 3.0)
        }

        exception.message?.let { it `should be equal to` "Error while updating rating for venue with id ${mockedVenue.id}" }

        verify(venueRepository, times(1)).findById(mockedVenue.id)
        verify(venueRatingRepository, times(1)).findByVenueId(mockedVenue.id)
        verify(venueRatingRepository, times(1)).save(updatedRatingCaptor.capture())
    }

    @Test
    fun `should insert new rating and update if does not exist earlier`() {
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(mockedVenue))
        `when`(venueRatingRepository.findByVenueId(anyInt())).thenReturn(emptyList())

        val response = venueService.rate(mockedVenue.id, 3.0)

        response.success `should be` true
        response.message `should be equal to` "Venue with id ${mockedVenue.id} successfully rated with rating 3.0"

        verify(venueRatingRepository).save(updatedRatingCaptor.capture())
        val updatedRating = updatedRatingCaptor.value
        updatedRating.venueId `should be equal to` mockedVenue.id
        updatedRating.rating `should be equal to` 3.0

        verify(venueRepository).save(updatedVenueCaptor.capture())
        val updatedVenue = updatedVenueCaptor.value
        updatedVenue.id `should be equal to` mockedVenue.id
        updatedVenue.averageRating `should be equal to` 3.0

        verify(venueRepository, times(1)).findById(mockedVenue.id)
        verify(venueRatingRepository, times(1)).findByVenueId(mockedVenue.id)
        verify(venueRatingRepository, times(1)).save(updatedRatingCaptor.capture())
        verify(venueRepository, times(1)).save(updatedVenueCaptor.capture())
    }

    @Test
    fun `should insert new rating and calculate new average correctly`() {
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(mockedVenue))
        `when`(venueRatingRepository.findByVenueId(anyInt())).thenReturn(listOf(mockedRating))

        val response = venueService.rate(mockedVenue.id, 5.0)

        response.success `should be` true
        response.message `should be equal to` "Venue with id ${mockedVenue.id} successfully rated with rating 5.0"

        verify(venueRatingRepository).save(updatedRatingCaptor.capture())
        val updatedRating = updatedRatingCaptor.value
        updatedRating.venueId `should be equal to` mockedVenue.id
        updatedRating.rating `should be equal to` 5.0

        verify(venueRepository).save(updatedVenueCaptor.capture())
        val updatedVenue = updatedVenueCaptor.value
        updatedVenue.id `should be equal to` mockedVenue.id
        updatedVenue.averageRating `should be equal to` 4.5

        verify(venueRepository, times(1)).findById(mockedVenue.id)
        verify(venueRatingRepository, times(1)).findByVenueId(mockedVenue.id)
        verify(venueRatingRepository, times(1)).save(updatedRatingCaptor.capture())
        verify(venueRepository, times(1)).save(updatedVenueCaptor.capture())
    }

}