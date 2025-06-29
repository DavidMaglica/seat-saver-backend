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

    private val venue = createVenue()
    private val request = CreateVenueRequest(
        venue.name,
        venue.location,
        venue.description,
        venue.venueTypeId,
        venue.workingHours,
        venue.maximumCapacity,
    )

    @Test
    fun `should return failing response if venue name is empty`() {
        val invalidRequest = request.copy(name = "")

        val response = venueService.create(invalidRequest)

        response.success `should be equal to` false
        response.message `should be equal to` "Name cannot be empty."

        verifyNoInteractions(venueRepository)
    }

    @Test
    fun `should return failing response if venue location is empty`() {
        val invalidRequest = request.copy(location = "")

        val response = venueService.create(invalidRequest)

        response.success `should be equal to` false
        response.message `should be equal to` "Location cannot be empty."

        verifyNoInteractions(venueRepository)
    }

    @Test
    fun `should return failing response if venue description is empty`() {
        val invalidRequest = request.copy(description = "")

        val response = venueService.create(invalidRequest)

        response.success `should be equal to` false
        response.message `should be equal to` "Description cannot be empty."

        verifyNoInteractions(venueRepository)
    }

    @Test
    fun `should return failing response if venue working hours are empty`() {
        val invalidRequest = request.copy(workingHours = "")

        val response = venueService.create(invalidRequest)

        response.success `should be equal to` false
        response.message `should be equal to` "Working hours cannot be empty."

        verifyNoInteractions(venueRepository)
    }

    @Test
    fun `should return failing response if venue maximum capacity is not positive`() {
        val invalidRequest = request.copy(maximumCapacity = 0)

        val response = venueService.create(invalidRequest)

        response.success `should be equal to` false
        response.message `should be equal to` "Maximum capacity must be positive."

        verifyNoInteractions(venueRepository)
    }

    @Test
    fun `should return failing response if venue type ID is invalid`() {
        val invalidRequest = request.copy(typeId = -1)

        val response = venueService.create(invalidRequest)

        response.success `should be equal to` false
        response.message `should be equal to` "Invalid venue type id."

        verifyNoInteractions(venueRepository)
    }

    @Test
    fun `should throw if unable to save venue`() {
        `when`(venueRepository.save(any())).thenThrow(RuntimeException("Unable to save venue"))

        val response = venueService.create(request)

        response.success `should be equal to` false
        response.message `should be equal to` "Error while creating venue. Please try again later."

        verify(venueRepository).save(venueArgumentCaptor.capture())
        verifyNoMoreInteractions(venueRepository)
    }

    @Test
    fun `should save venue`() {
        `when`(venueRepository.save(any())).thenReturn(venue)

        val response = venueService.create(request)

        response.success `should be equal to` true
        response.message `should be equal to` "Venue ${venue.name} created successfully."

        verify(venueRepository).save(venueArgumentCaptor.capture())
        val savedVenue = venueArgumentCaptor.value
        savedVenue.name `should be equal to` venue.name
        savedVenue.location `should be equal to` venue.location
        savedVenue.description `should be equal to` venue.description
        savedVenue.venueTypeId `should be equal to` venue.venueTypeId
        savedVenue.workingHours `should be equal to` venue.workingHours
        savedVenue.maximumCapacity `should be equal to` venue.maximumCapacity
        savedVenue.availableCapacity `should be equal to` venue.maximumCapacity

        verify(venueRepository, times(1)).save(any())
        verifyNoMoreInteractions(venueRepository)
    }
}