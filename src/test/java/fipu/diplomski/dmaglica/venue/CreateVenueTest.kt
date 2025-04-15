package fipu.diplomski.dmaglica.venue

import fipu.diplomski.dmaglica.model.request.CreateVenueRequest
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles
import java.sql.SQLException

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
class CreateVenueTest : BaseVenueServiceTest() {

    @Test
    fun `should throw if unable to save venue`() {
        `when`(venueRepository.save(any())).thenThrow(RuntimeException("Unable to save venue"))
        val request = CreateVenueRequest(
            mockedVenue.name,
            mockedVenue.location,
            mockedVenue.description,
            mockedVenue.venueTypeId,
            mockedVenue.workingHours,
        )

        val exception = assertThrows<SQLException> { venueService.create(request) }

        exception.message `should be equal to` "Error while saving venue: ${mockedVenue.name}"
    }

    @Test
    fun `should save venue`() {
        `when`(venueRepository.save(any())).thenReturn(mockedVenue)
        val request = CreateVenueRequest(
            mockedVenue.name,
            mockedVenue.location,
            mockedVenue.description,
            mockedVenue.venueTypeId,
            mockedVenue.workingHours,
        )

        val result = venueService.create(request)

        result.success `should be equal to` true
        result.message `should be equal to` "Venue ${mockedVenue.name} created successfully"

        verify(venueRepository).save(venueArgumentCaptor.capture())
        val savedVenue = venueArgumentCaptor.value
        savedVenue.name `should be equal to` mockedVenue.name
        savedVenue.location `should be equal to` mockedVenue.location
        savedVenue.description `should be equal to` mockedVenue.description
        savedVenue.venueTypeId `should be equal to` mockedVenue.venueTypeId
        savedVenue.workingHours `should be equal to` mockedVenue.workingHours

        verify(venueRepository, times(1)).save(any())
    }
}