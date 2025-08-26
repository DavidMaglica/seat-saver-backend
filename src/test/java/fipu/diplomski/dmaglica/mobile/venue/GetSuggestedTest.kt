package fipu.diplomski.dmaglica.mobile.venue

import fipu.diplomski.dmaglica.repo.entity.VenueEntity
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime
import kotlin.test.Test

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
class GetSuggestedTest : BaseVenueServiceTest() {

    private val pageable = PageRequest.of(0, 10)

    @Test
    fun `should return highly rated venues with available capacity`() {
        val suggestedVenues = listOf(
            createVenue(id = 1, averageRating = 4.5, availableCapacity = 10),
            createVenue(id = 2, averageRating = 4.2, availableCapacity = 5)
        )

        `when`(venueRepository.findSuggestedVenues(pageable)).thenReturn(PageImpl(suggestedVenues, pageable, 2))
        `when`(venueRatingRepository.findByVenueIdIn(suggestedVenues.map { it.id })).thenReturn(
            listOf(
                createRating(1, 1, 4.5),
                createRating(2, 2, 4.2)
            )
        )

        val response = venueService.getSuggestedVenues(pageable)

        response.content.size `should be equal to` 2
        response.content.all { it.averageRating > 4.0 } `should be equal to` true
        response.content.all { it.availableCapacity > 0 } `should be equal to` true

        verify(venueRepository).findSuggestedVenues(pageable)
        verifyNoMoreInteractions(venueRepository)
    }

    @Test
    fun `should return venues in correct sorting order`() {
        val (lowerBound, upperBound) = getSurroundingHalfHours(LocalDateTime.now())
        val venues = listOf(
            createVenue(id = 1, averageRating = 5.0, availableCapacity = 5),
            createVenue(id = 2, averageRating = 4.9, availableCapacity = 10),
            createVenue(id = 3, averageRating = 4.8, availableCapacity = 15),
        ).sortedWith(
            compareByDescending<VenueEntity> { it.id }
                .thenByDescending { it.averageRating }
                .thenByDescending { it.availableCapacity }
        )

        val reservation = createReservation(venueId = 1, numberOfGuests = 5)

        `when`(venueRepository.findSuggestedVenues(pageable)).thenReturn(PageImpl(venues, pageable, 3))
        `when`(venueRatingRepository.findByVenueIdIn(venues.map { it.id })).thenReturn(
            listOf(
                createRating(1, 1, 5.0),
                createRating(2, 2, 4.9),
                createRating(3, 3, 4.8)
            )
        )
        `when`(reservationRepository.findByDatetimeBetween(lowerBound, upperBound)).thenReturn(listOf(reservation))

        val response = venueService.getSuggestedVenues(pageable)

        response.content[0].id `should be equal to` 3
        response.content[1].id `should be equal to` 2
        response.content[2].id `should be equal to` 1
        response.content[0].averageRating `should be equal to` 4.8
        response.content[1].averageRating `should be equal to` 4.9

        verify(venueRepository).findSuggestedVenues(pageable)
        verifyNoMoreInteractions(venueRepository)
    }

    @Test
    fun `should exclude low-rated venues`() {
        val mixedVenues = listOf(
            createVenue(id = 1, averageRating = 4.1, availableCapacity = 5),
            createVenue(id = 2, averageRating = 3.9, availableCapacity = 10) // Should be excluded
        ).filter { it.averageRating > 4.0 && it.availableCapacity > 0 }

        `when`(venueRepository.findSuggestedVenues(pageable)).thenReturn(PageImpl(mixedVenues, pageable, 1))
        `when`(venueRatingRepository.findByVenueIdIn(mixedVenues.map { it.id })).thenReturn(
            listOf(
                createRating(1, 1, 4.1),
                createRating(2, 2, 3.9)
            )
        )

        val response = venueService.getSuggestedVenues(pageable)

        response.content.size `should be equal to` 1
        response.content[0].averageRating `should be equal to` 4.1

        verify(venueRepository).findSuggestedVenues(pageable)
        verifyNoMoreInteractions(venueRepository)
    }

    @Test
    fun `should exclude fully booked venues`() {
        val (lowerBound, upperBound) = getSurroundingHalfHours(LocalDateTime.now())
        val mixedVenues = listOf(
            createVenue(id = 1, averageRating = 4.5, availableCapacity = 5, maximumCapacity = 15),
            createVenue(id = 2, averageRating = 4.2, availableCapacity = 0)
        ).filter { it.averageRating > 4.0 && it.availableCapacity > 0 }
        val reservation = createReservation(venueId = 1, numberOfGuests = 10)

        `when`(venueRepository.findSuggestedVenues(pageable)).thenReturn(PageImpl(mixedVenues, pageable, 1))
        `when`(reservationRepository.findByDatetimeBetween(lowerBound, upperBound)).thenReturn(listOf(reservation))

        val response = venueService.getSuggestedVenues(pageable)

        response.content.size `should be equal to` 1
        response.content[0].availableCapacity `should be equal to` 5

        verify(venueRepository).findSuggestedVenues(pageable)
        verify(reservationRepository).findByDatetimeBetween(lowerBound, upperBound)
        verifyNoMoreInteractions(venueRepository, reservationRepository)
    }

    @Test
    fun `should maintain pagination metadata`() {
        val customPageable = PageRequest.of(1, 3)
        val mockVenues = listOf(
            createVenue(id = 4, averageRating = 4.3, availableCapacity = 2),
            createVenue(id = 5, averageRating = 4.4, availableCapacity = 3)
        )

        `when`(venueRepository.findSuggestedVenues(customPageable))
            .thenReturn(PageImpl(mockVenues, customPageable, 5))

        val response = venueService.getSuggestedVenues(customPageable)

        response.page `should be equal to` 1
        response.size `should be equal to` 3
        response.totalElements `should be equal to` 5
        response.totalPages `should be equal to` 2

        verify(venueRepository).findSuggestedVenues(customPageable)
        verifyNoMoreInteractions(venueRepository)
    }
}