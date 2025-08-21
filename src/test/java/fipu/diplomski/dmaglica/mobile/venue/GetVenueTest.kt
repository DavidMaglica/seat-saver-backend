package fipu.diplomski.dmaglica.mobile.venue

import fipu.diplomski.dmaglica.repo.entity.ReservationEntity
import jakarta.persistence.EntityNotFoundException
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.*
import java.time.LocalDateTime
import java.util.*
import kotlin.test.Test

class GetVenueTest : BaseVenueServiceTest() {

    private val venue = createVenue()

    @Test
    fun `should return venue with calculated rating and capacity when found`() {
        val (lowerBound, upperBound) = getSurroundingHalfHours(LocalDateTime.now())
        val rating = createRating(1, 1, 4.0)
        val reservations = listOf(
            ReservationEntity(
                id = 1,
                userId = 1,
                venueId = 1,
                datetime = LocalDateTime.now(),
                numberOfGuests = 20
            ),
            ReservationEntity(
                id = 2,
                userId = 2,
                venueId = 1,
                datetime = LocalDateTime.now().plusDays(1),
                numberOfGuests = 30
            )
        )

        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(venue))
        `when`(venueRatingRepository.findByVenueId(anyInt())).thenReturn(listOf(rating))
        `when`(reservationRepository.findByVenueIdAndDatetimeBetween(venue.id, lowerBound, upperBound)).thenReturn(
            reservations
        )

        val result = venueService.get(venue.id)

        result.id `should be equal to` venue.id
        result.averageRating `should be equal to` 4.0
        result.availableCapacity `should be equal to` 50

        verify(venueRepository).findById(venue.id)
        verify(venueRatingRepository).findByVenueId(venue.id)
        verify(reservationRepository).findByVenueIdAndDatetimeBetween(venue.id, lowerBound, upperBound)
        verifyNoMoreInteractions(venueRepository, venueRatingRepository, reservationRepository)
    }

    @Test
    fun `should set full capacity when no reservations exist`() {
        val (lowerBound, upperBound) = getSurroundingHalfHours(LocalDateTime.now())

        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(venue))
        `when`(venueRatingRepository.findByVenueId(anyInt())).thenReturn(emptyList())
        `when`(reservationRepository.findByVenueIdAndDatetimeBetween(venue.id, lowerBound, upperBound)).thenReturn(
            emptyList()
        )

        val result = venueService.get(venue.id)

        result.availableCapacity `should be equal to` 100
        verify(reservationRepository).findByVenueIdAndDatetimeBetween(venue.id, lowerBound, upperBound)
    }

    @Test
    fun `should throw EntityNotFoundException when venue not found`() {
        val venue = createVenue()

        `when`(venueRepository.findById(any())).thenReturn(Optional.empty())

        val exception = assertThrows<EntityNotFoundException> {
            venueService.get(venue.id)
        }

        exception.message `should be equal to` "Venue with id: 0 not found."
        verify(venueRepository).findById(venue.id)
        verifyNoInteractions(venueRatingRepository, reservationRepository)
    }

    @Test
    fun `should handle zero ratings by setting average to zero`() {
        val (lowerBound, upperBound) = getSurroundingHalfHours(LocalDateTime.now())
        val venue = createVenue()

        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(venue))
        `when`(venueRatingRepository.findByVenueId(anyInt())).thenReturn(emptyList())
        `when`(reservationRepository.findByVenueIdAndDatetimeBetween(venue.id, lowerBound, upperBound)).thenReturn(
            emptyList()
        )

        val result = venueService.get(venue.id)

        result.averageRating `should be equal to` 0.0
    }
}
