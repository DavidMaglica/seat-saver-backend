package fipu.diplomski.dmaglica.mobile.venue

import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
class GetByOwnerTest : BaseVenueServiceTest() {

    companion object {
        private const val OWNER_ID = 1
    }

    private val venue = createVenue()
    private val pageable = PageRequest.of(0, 10)

    @Test
    fun `should return empty list when no venues owned`() {
        `when`(venueRepository.findByOwnerId(OWNER_ID, pageable)).thenReturn(PageImpl(emptyList()))

        val result = venueService.getByOwner(OWNER_ID, pageable)

        result.content `should be equal to` emptyList()

        verify(venueRepository).findByOwnerId(OWNER_ID, pageable)
        verifyNoMoreInteractions(venueRepository)
    }

    @Test
    fun `should return list of venues when venues owned`() {
        `when`(venueRepository.findByOwnerId(OWNER_ID, pageable)).thenReturn(PageImpl(listOf(venue)))

        val result = venueService.getByOwner(OWNER_ID, pageable)

        result.content.size `should be equal to` 1
        result.content[0].id `should be equal to` venue.id
        result.content[0].name `should be equal to` venue.name
        result.content[0].venueTypeId `should be equal to` venue.venueTypeId
        result.content[0].location `should be equal to` venue.location
        result.content[0].workingHours `should be equal to` venue.workingHours
        result.content[0].description `should be equal to` venue.description

        verify(venueRepository).findByOwnerId(OWNER_ID, pageable)
        verifyNoMoreInteractions(venueRepository)
    }
}
