package fipu.diplomski.dmaglica.mobile.venue

import fipu.diplomski.dmaglica.model.request.CreateVenueRequest
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles

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
            mockedVenue.maximumCapacity,
            mockedVenue.availableCapacity,
        )

        val response = venueService.create(request)

        response.success `should be equal to` false
        response.message `should be equal to` "Error while creating venue. Please try again later."
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
            mockedVenue.maximumCapacity,
            mockedVenue.availableCapacity,
        )

        val response = venueService.create(request)

        response.success `should be equal to` true
        response.message `should be equal to` "Venue ${mockedVenue.name} created successfully."

        verify(venueRepository).save(venueArgumentCaptor.capture())
        val savedVenue = venueArgumentCaptor.value
        savedVenue.name `should be equal to` mockedVenue.name
        savedVenue.location `should be equal to` mockedVenue.location
        savedVenue.description `should be equal to` mockedVenue.description
        savedVenue.venueTypeId `should be equal to` mockedVenue.venueTypeId
        savedVenue.workingHours `should be equal to` mockedVenue.workingHours
        savedVenue.maximumCapacity `should be equal to` mockedVenue.maximumCapacity
        savedVenue.availableCapacity `should be equal to` mockedVenue.availableCapacity

        verify(venueRepository, times(1)).save(any())
    }
}