package fipu.diplomski.dmaglica.mobile.venue

import fipu.diplomski.dmaglica.model.data.Role
import fipu.diplomski.dmaglica.model.request.CreateVenueRequest
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles
import java.util.*

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
class CreateVenueTest : BaseVenueServiceTest() {

    private val venue = createVenue()
    private val user = createUser()
    private val owner = createUser().also { it.roleId = Role.OWNER.ordinal }
    private val request = CreateVenueRequest(
        venue.ownerId,
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
    fun `should return failing response if user doesn't exist`() {
        `when`(userRepository.findById(venue.ownerId)).thenReturn(Optional.empty())

        val response = venueService.create(request)

        response.success `should be equal to` false
        response.message `should be equal to` "User does not exist."

        verify(userRepository).findById(venue.ownerId)
        verifyNoMoreInteractions(userRepository)
        verifyNoInteractions(venueRepository)
    }

    @Test
    fun `should return failing response if user is not owner`() {
        `when`(userRepository.findById(venue.ownerId)).thenReturn(Optional.of(user))

        val response = venueService.create(request)

        response.success `should be equal to` false
        response.message `should be equal to` "User is not a valid owner."

        verify(userRepository).findById(venue.ownerId)
        verifyNoMoreInteractions(userRepository)
        verifyNoInteractions(venueRepository)
    }

    @Test
    fun `should return failing response if venue with same name already exists for owner`() {
        `when`(userRepository.findById(venue.ownerId)).thenReturn(Optional.of(owner))
        `when`(venueRepository.existsByOwnerIdAndNameIgnoreCase(venue.ownerId, venue.name)).thenReturn(true)

        val response = venueService.create(request)

        response.success `should be equal to` false
        response.message `should be equal to` "Venue with name '${request.name}' already exists for this owner."

        verify(userRepository).findById(venue.ownerId)
        verify(venueRepository).existsByOwnerIdAndNameIgnoreCase(venue.ownerId, venue.name)
        verifyNoMoreInteractions(userRepository, venueRepository)
    }

    @Test
    fun `should throw if unable to save venue`() {
        `when`(userRepository.findById(venue.ownerId)).thenReturn(Optional.of(owner))
        `when`(venueRepository.existsByOwnerIdAndNameIgnoreCase(venue.ownerId, venue.name)).thenReturn(false)
        `when`(venueRepository.save(any())).thenThrow(RuntimeException("Unable to save venue"))

        val response = venueService.create(request)

        response.success `should be equal to` false
        response.message `should be equal to` "Error while creating venue. Please try again later."

        verify(userRepository).findById(venue.ownerId)
        verify(venueRepository).existsByOwnerIdAndNameIgnoreCase(venue.ownerId, venue.name)
        verify(venueRepository).save(venueArgumentCaptor.capture())
        verifyNoMoreInteractions(userRepository, venueRepository)
    }

    @Test
    fun `should save venue`() {
        `when`(userRepository.findById(venue.ownerId)).thenReturn(Optional.of(owner))
        `when`(venueRepository.existsByOwnerIdAndNameIgnoreCase(venue.ownerId, venue.name)).thenReturn(false)
        `when`(venueRepository.save(any())).thenReturn(venue)

        val response = venueService.create(request)

        response.success `should be equal to` true
        response.message `should be equal to` "Venue ${venue.name} created successfully."
        response.data `should be equal to` venue.id

        verify(userRepository).findById(venue.ownerId)
        verify(venueRepository).existsByOwnerIdAndNameIgnoreCase(venue.ownerId, venue.name)
        verify(venueRepository).save(venueArgumentCaptor.capture())
        val savedVenue = venueArgumentCaptor.value
        savedVenue.name `should be equal to` venue.name
        savedVenue.location `should be equal to` venue.location
        savedVenue.description `should be equal to` venue.description
        savedVenue.venueTypeId `should be equal to` venue.venueTypeId
        savedVenue.workingHours `should be equal to` venue.workingHours
        savedVenue.maximumCapacity `should be equal to` venue.maximumCapacity
        savedVenue.availableCapacity `should be equal to` venue.maximumCapacity

        verifyNoMoreInteractions(userRepository, venueRepository)
    }
}