package fipu.diplomski.dmaglica.mobile.venue

import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest

class GetAllTest : BaseVenueServiceTest() {

    @Test
    fun `should return paginated venues filtered by search query`() {
        val cafeVenue = createVenue(id = 1, name = "Test Café", venueTypeId = 1)
        val pageable = PageRequest.of(0, 10)
        `when`(venueRepository.findFilteredVenues("cafe", null, pageable))
            .thenReturn(PageImpl(listOf(cafeVenue)))

        val result = venueService.getAll(pageable, "cafe", null)

        result.content.size `should be equal to` 1
        result.content[0].name `should be equal to` "Test Café"
        verify(venueRepository).findFilteredVenues("cafe", null, pageable)
        verifyNoMoreInteractions(venueRepository)
    }

    @Test
    fun `should return paginated venues filtered by type IDs`() {
        val restaurantVenue = createVenue(id = 2, name = "Good Restaurant", venueTypeId = 2)
        val pageable = PageRequest.of(0, 10)
        `when`(venueRepository.findFilteredVenues(null, listOf(2), pageable))
            .thenReturn(PageImpl(listOf(restaurantVenue)))

        val result = venueService.getAll(pageable, null, listOf(2))

        result.content.size `should be equal to` 1
        result.content[0].venueTypeId `should be equal to` 2
        verify(venueRepository).findFilteredVenues(null, listOf(2), pageable)
        verifyNoMoreInteractions(venueRepository)
    }

    @Test
    fun `should return venues matching both search query and type IDs`() {
        val coffeeShop = createVenue(id = 3, name = "Coffee Place", venueTypeId = 2)
        val pageable = PageRequest.of(0, 10)
        `when`(venueRepository.findFilteredVenues("coffee", listOf(2), pageable))
            .thenReturn(PageImpl(listOf(coffeeShop)))

        val result = venueService.getAll(pageable, "coffee", listOf(2))

        result.content.size `should be equal to` 1
        result.content[0].name `should be equal to` "Coffee Place"
        result.content[0].venueTypeId `should be equal to` 2
        verify(venueRepository).findFilteredVenues("coffee", listOf(2), pageable)
        verifyNoMoreInteractions(venueRepository)
    }

    @Test
    fun `should return empty page when no venues match filters`() {
        val pageable = PageRequest.of(0, 10)
        `when`(venueRepository.findFilteredVenues("nonexistent", listOf(99), pageable))
            .thenReturn(PageImpl(emptyList()))

        val result = venueService.getAll(pageable, "nonexistent", listOf(99))

        result.content.size `should be equal to` 0
        verify(venueRepository).findFilteredVenues("nonexistent", listOf(99), pageable)
        verifyNoMoreInteractions(venueRepository)
    }

    @Test
    fun `should return all venues when no filters provided`() {
        val venue1 = createVenue(id = 1, name = "Venue A")
        val venue2 = createVenue(id = 2, name = "Venue B")
        val pageable = PageRequest.of(0, 10)
        `when`(venueRepository.findFilteredVenues(null, null, pageable))
            .thenReturn(PageImpl(listOf(venue1, venue2)))

        val result = venueService.getAll(pageable, null, null)

        result.content.size `should be equal to` 2
        result.content[0].name `should be equal to` "Venue A"
        result.content[1].name `should be equal to` "Venue B"
        verify(venueRepository).findFilteredVenues(null, null, pageable)
        verifyNoMoreInteractions(venueRepository)
    }
}
