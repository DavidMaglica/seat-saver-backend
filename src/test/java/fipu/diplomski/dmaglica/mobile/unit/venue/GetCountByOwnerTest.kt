package fipu.diplomski.dmaglica.mobile.unit.venue

import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
class GetCountByOwnerTest : BaseVenueServiceTest() {

    companion object {
        private const val OWNER_ID = 1
    }

    private val venue = createVenue()

    @Test
    fun `should return 0 when no venues owned`() {
        `when`(venueRepository.countByOwnerId(OWNER_ID)).thenReturn(0)

        val result = venueService.getCountByOwner(OWNER_ID)

        result `should be equal to` 0

        verify(venueRepository).countByOwnerId(OWNER_ID)
        verifyNoMoreInteractions(venueRepository)
    }

    @Test
    fun `should return correct count when venues owned`() {
        `when`(venueRepository.countByOwnerId(OWNER_ID)).thenReturn(listOf(venue).size)

        val result = venueService.getCountByOwner(OWNER_ID)

        result `should be equal to` 1

        verify(venueRepository).countByOwnerId(OWNER_ID)
        verifyNoMoreInteractions(venueRepository)
    }
}