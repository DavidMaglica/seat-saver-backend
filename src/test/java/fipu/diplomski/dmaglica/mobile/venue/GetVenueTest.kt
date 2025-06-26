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

    @Test
    fun `should return venue with calculated rating and capacity when found`() {
        val (lowerBound, upperBound) = getSurroundingHalfHours(LocalDateTime.now())
        val mockReservations = listOf(
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

        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(mockedVenue))
        `when`(venueRatingRepository.findByVenueId(anyInt())).thenReturn(listOf(mockedRating))
        `when`(
            reservationRepository.findByVenueIdAndDatetimeBetween(mockedVenue.id, lowerBound, upperBound)
        ).thenReturn(mockReservations)

        val result = venueService.get(mockedVenue.id)

        result.id `should be equal to` mockedVenue.id
        result.averageRating `should be equal to` 4.0
        result.availableCapacity `should be equal to` 50

        verify(venueRepository).findById(mockedVenue.id)
        verify(venueRatingRepository).findByVenueId(mockedVenue.id)
        verify(reservationRepository).findByVenueIdAndDatetimeBetween(mockedVenue.id, lowerBound, upperBound)
        verifyNoMoreInteractions(venueRepository, venueRatingRepository, reservationRepository)
    }

    @Test
    fun `should set full capacity when no reservations exist`() {
        val (lowerBound, upperBound) = getSurroundingHalfHours(LocalDateTime.now())

        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(mockedVenue))
        `when`(venueRatingRepository.findByVenueId(anyInt())).thenReturn(emptyList())
        `when`(
            reservationRepository.findByVenueIdAndDatetimeBetween(mockedVenue.id, lowerBound, upperBound)
        ).thenReturn(emptyList())

        val result = venueService.get(mockedVenue.id)

        result.availableCapacity `should be equal to` 100
        verify(reservationRepository).findByVenueIdAndDatetimeBetween(mockedVenue.id, lowerBound, upperBound)
    }

    @Test
    fun `should throw EntityNotFoundException when venue not found`() {
        `when`(venueRepository.findById(any())).thenReturn(Optional.empty())

        val exception = assertThrows<EntityNotFoundException> {
            venueService.get(mockedVenue.id)
        }

        exception.message `should be equal to` "Venue with id: 1 not found."
        verify(venueRepository).findById(mockedVenue.id)
        verifyNoInteractions(venueRatingRepository, reservationRepository)
    }

    @Test
    fun `should handle zero ratings by setting average to zero`() {
        val (lowerBound, upperBound) = getSurroundingHalfHours(LocalDateTime.now())

        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(mockedVenue))
        `when`(venueRatingRepository.findByVenueId(anyInt())).thenReturn(emptyList())
        `when`(
            reservationRepository.findByVenueIdAndDatetimeBetween(mockedVenue.id, lowerBound, upperBound)
        ).thenReturn(emptyList())

        val result = venueService.get(mockedVenue.id)

        result.averageRating `should be equal to` 0.0
    }
}
