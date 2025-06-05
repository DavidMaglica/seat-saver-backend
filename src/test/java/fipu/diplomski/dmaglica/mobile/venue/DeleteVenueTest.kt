package fipu.diplomski.dmaglica.mobile.venue

import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
class DeleteVenueTest : BaseVenueServiceTest() {

    @Test
    fun `should return failure response when venue does not exist`() {
        `when`(venueRepository.deleteById(anyInt())).thenThrow(IllegalArgumentException("Venue not found"))

        val response = venueService.delete(mockedVenue.id)

        response.success `should be equal to` false
        response.message `should be equal to` "Error while deleting venue. Please try again later."

        verify(venueRepository, times(1)).deleteById(anyInt())
        verifyNoMoreInteractions(venueRepository)
    }

    @Test
    fun `should delete venue successfully`() {
        val response = venueService.delete(mockedVenue.id)

        response.success `should be equal to` true
        response.message `should be equal to` "Venue with id: ${mockedVenue.id} successfully deleted."

        verify(venueRepository, times(1)).deleteById(anyInt())
    }
}