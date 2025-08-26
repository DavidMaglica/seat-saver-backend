package fipu.diplomski.dmaglica.mobile.venue

import fipu.diplomski.dmaglica.model.data.TrendingVenueProjection
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles
import kotlin.test.Test

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
class GetTrendingVenuesTest : BaseVenueServiceTest() {

    private val pageable = PageRequest.of(0, 10)

    private fun createStats(venueId: Int, reservationCount: Int) = object : TrendingVenueProjection {
        override fun getVenueId(): Int = venueId
        override fun getReservationCount(): Int = reservationCount
    }

    @Test
    fun `should return venues ordered by reservation count`() {
        val stats = listOf(
            createStats(venueId = 1, reservationCount = 30),
            createStats(venueId = 2, reservationCount = 40),
            createStats(venueId = 3, reservationCount = 50),
        )
        val venues = listOf(
            createVenue(id = 3),
            createVenue(id = 1),
            createVenue(id = 2)
        )

        `when`(reservationRepository.findTopVenuesByReservationCount(pageable)).thenReturn(PageImpl(stats, pageable, 3))
        `when`(venueRepository.findAllById(listOf(1, 2, 3))).thenReturn(venues)

        val response = venueService.getTrendingVenues(pageable)

        response.content[0].id `should be equal to` 1
        response.content[1].id `should be equal to` 2
        response.content[2].id `should be equal to` 3
        response.totalElements `should be equal to` 3

        verify(reservationRepository).findTopVenuesByReservationCount(pageable)
        verify(venueRepository).findAllById(listOf(1, 2, 3))
    }

    @Test
    fun `should maintain pagination when venues exceed page size`() {
        val stats = (1..20).map { createStats(venueId = it, reservationCount = it * 2) }
        val venues = (1..20).map { createVenue(id = it) }

        `when`(reservationRepository.findTopVenuesByReservationCount(pageable))
            .thenReturn(PageImpl(stats.take(10), pageable, 20))
        `when`(venueRepository.findAllById((1..10).toList())).thenReturn(venues.take(10))

        val response = venueService.getTrendingVenues(pageable)

        response.content.size `should be equal to` 10
        response.totalElements `should be equal to` 20
        response.totalPages `should be equal to` 2

        verify(reservationRepository).findTopVenuesByReservationCount(pageable)
        verify(venueRepository).findAllById((1..10).toList())
    }

    @Test
    fun `should handle empty trending list`() {
        `when`(reservationRepository.findTopVenuesByReservationCount(pageable))
            .thenReturn(PageImpl(emptyList(), pageable, 0))

        val response = venueService.getTrendingVenues(pageable)

        response.content `should be` emptyList()
        response.totalElements `should be equal to` 0

        verify(reservationRepository).findTopVenuesByReservationCount(pageable)
    }

    @Test
    fun `should preserve order when fetching venue details`() {
        val stats = listOf(
            createStats(venueId = 1, reservationCount = 100),
            createStats(venueId = 2, reservationCount = 90)
        )
        val venues = listOf(createVenue(id = 2), createVenue(id = 1))

        `when`(reservationRepository.findTopVenuesByReservationCount(pageable))
            .thenReturn(PageImpl(stats, pageable, 2))
        `when`(venueRepository.findAllById(listOf(1, 2))).thenReturn(venues)

        val response = venueService.getTrendingVenues(pageable)

        response.content[0].id `should be equal to` 1
        response.content[1].id `should be equal to` 2

        verify(reservationRepository).findTopVenuesByReservationCount(pageable)
        verify(venueRepository).findAllById(listOf(1, 2))
    }

    @Test
    fun `should handle missing venues gracefully`() {
        val stats = listOf(
            createStats(venueId = 1, reservationCount = 50),
            createStats(venueId = 99, reservationCount = 40)
        )
        val venues = listOf(createVenue(id = 1))

        `when`(reservationRepository.findTopVenuesByReservationCount(pageable))
            .thenReturn(PageImpl(stats, pageable, 2))
        `when`(venueRepository.findAllById(listOf(1, 99))).thenReturn(venues)

        val response = venueService.getTrendingVenues(pageable)

        response.content.size `should be equal to` 1
        response.content[0].id `should be equal to` 1

        verify(reservationRepository).findTopVenuesByReservationCount(pageable)
        verify(venueRepository).findAllById(listOf(1, 99))
    }
}
