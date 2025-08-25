package fipu.diplomski.dmaglica.mobile.venue

import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles
import kotlin.test.Test

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
class GetNearbyTest : BaseVenueServiceTest() {

    private val pageable: PageRequest = PageRequest.of(0, 10)

    @Test
    fun `should return venues in default city when no coordinates provided`() {
        val zagrebVenues = listOf(
            createVenue(id = 1, name = "Zagreb Venue 1", location = "Zagreb"),
            createVenue(id = 2, name = "Zagreb Venue 2", location = "Zagreb")
        )
        `when`(venueRepository.findByLocation("Zagreb", pageable))
            .thenReturn(PageImpl(zagrebVenues))

        val result = venueService.getNearbyVenues(pageable, null, null)

        result.content.size `should be equal to` 2
        result.content.all { it.location == "Zagreb" } `should be equal to` true

        verify(venueRepository).findByLocation("Zagreb", pageable)
        verifyNoInteractions(geolocationService)
    }

    @Test
    fun `should return venues in current city when no nearby cities found`() {
        val currentCity = "Split"
        val splitVenues = listOf(createVenue(id = 3, location = currentCity))

        `when`(geolocationService.getGeolocation(anyDouble(), anyDouble())).thenReturn(currentCity)
        `when`(geolocationService.getNearbyCities(anyDouble(), anyDouble())).thenReturn(mutableListOf())
        `when`(venueRepository.findByLocation(currentCity, pageable))
            .thenReturn(PageImpl(splitVenues))

        val result = venueService.getNearbyVenues(pageable, 45.0, 16.0)

        result.content.size `should be equal to` 1
        result.content[0].location `should be equal to` currentCity

        verify(geolocationService).getGeolocation(45.0, 16.0)
        verify(geolocationService).getNearbyCities(45.0, 16.0)
        verify(venueRepository).findByLocation(currentCity, pageable)
        verifyNoMoreInteractions(geolocationService, venueRepository)
    }

    @Test
    fun `should return venues from current and nearby cities`() {
        val currentCity = "Rijeka"
        val nearbyCities = mutableListOf("Porec", "Crikvenica")
        val allVenues = listOf(
            createVenue(id = 4, location = currentCity),
            createVenue(id = 5, location = "Porec"),
            createVenue(id = 6, location = "Crikvenica")
        )

        `when`(geolocationService.getGeolocation(anyDouble(), anyDouble())).thenReturn(currentCity)
        `when`(geolocationService.getNearbyCities(anyDouble(), anyDouble())).thenReturn(nearbyCities)
        `when`(venueRepository.findByLocationIn(listOf("Porec", "Crikvenica", currentCity), pageable))
            .thenReturn(PageImpl(allVenues))

        val result = venueService.getNearbyVenues(pageable, 45.33, 14.44)

        result.content.size `should be equal to` 3
        result.content.map { it.location } `should be equal to` listOf(currentCity, "Porec", "Crikvenica")

        verify(geolocationService).getGeolocation(45.33, 14.44)
        verify(geolocationService).getNearbyCities(45.33, 14.44)
        verify(venueRepository).findByLocationIn(listOf("Porec", "Crikvenica", currentCity), pageable)
        verifyNoMoreInteractions(geolocationService, venueRepository)
    }


    @Test
    fun `should handle empty venue lists gracefully`() {
        `when`(geolocationService.getGeolocation(anyDouble(), anyDouble())).thenReturn("Zagreb")
        `when`(geolocationService.getNearbyCities(anyDouble(), anyDouble())).thenReturn(mutableListOf())
        `when`(venueRepository.findByLocation("Zagreb", pageable))
            .thenReturn(PageImpl(emptyList()))

        val result = venueService.getNearbyVenues(pageable, 45.81, 15.98)

        result.content.size `should be equal to` 0

        verify(geolocationService).getGeolocation(45.81, 15.98)
        verify(geolocationService).getNearbyCities(45.81, 15.98)
        verify(venueRepository).findByLocation("Zagreb", pageable)
        verifyNoMoreInteractions(geolocationService, venueRepository)
    }

    @Test
    fun `should respect pagination parameters`() {
        val venues = (1..20).map { createVenue(id = it, location = "Zagreb") }
        `when`(venueRepository.findByLocation("Zagreb", pageable))
            .thenReturn(PageImpl(venues.subList(10, 15), pageable, 20))

        val result = venueService.getNearbyVenues(pageable, null, null)

        result.content.size `should be equal to` 5
        result.page `should be equal to` 0
        result.size `should be equal to` 10
        result.totalPages `should be equal to` 2
        result.totalElements `should be equal to` 20
        verify(venueRepository).findByLocation("Zagreb", pageable)
        verifyNoInteractions(geolocationService)
    }
}
