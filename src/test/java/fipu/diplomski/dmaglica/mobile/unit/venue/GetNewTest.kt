package fipu.diplomski.dmaglica.mobile.unit.venue

import org.amshove.kluent.`should be`
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.test.context.ActiveProfiles
import kotlin.test.Test

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
class GetNewTest : BaseVenueServiceTest() {

    private val pageable: PageRequest = PageRequest.of(0, 10)

    @Test
    fun `should return venues sorted by descending ID`() {
        val venues = listOf(
            createVenue(id = 3),
            createVenue(id = 1),
            createVenue(id = 2)
        )
        val sortedPageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("id")))

        `when`(venueRepository.findAll(sortedPageable))
            .thenReturn(PageImpl(venues.sortedByDescending { it.id }, sortedPageable, 3))

        val response = venueService.getNewVenues(pageable)

        response.content.size `should be equal to` 3
        response.content[0].id `should be equal to` 3
        response.content[1].id `should be equal to` 2
        response.content[2].id `should be equal to` 1
        verify(venueRepository).findAll(sortedPageable)
        verifyNoMoreInteractions(venueRepository)
    }

    @Test
    fun `should maintain pagination parameters`() {
        val customPageable = PageRequest.of(2, 5)
        val expectedPageable = PageRequest.of(2, 5, Sort.by(Sort.Order.desc("id")))
        val mockVenues = (6..10).map { createVenue(id = it) }.reversed()

        `when`(venueRepository.findAll(expectedPageable))
            .thenReturn(PageImpl(mockVenues, expectedPageable, 25))

        val response = venueService.getNewVenues(customPageable)

        response.page `should be equal to` 2
        response.size `should be equal to` 5
        response.totalElements `should be equal to` 25
        response.totalPages `should be equal to` 5
    }

    @Test
    fun `should handle empty results`() {
        val sortedPageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("id")))
        `when`(venueRepository.findAll(sortedPageable))
            .thenReturn(PageImpl(emptyList(), sortedPageable, 0))

        val response = venueService.getNewVenues(pageable)

        response.content `should be` emptyList()
        response.totalElements `should be equal to` 0
    }


}