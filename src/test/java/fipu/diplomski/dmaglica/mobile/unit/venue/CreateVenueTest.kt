package fipu.diplomski.dmaglica.mobile.unit.venue

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
    private val workingDays = listOf(0, 1, 2, 5, 6)
    private val workingDaysEntity = createWorkingDays(venue.id, workingDays)
    private val user = createUser()
    private val owner = createUser().also { it.roleId = Role.OWNER.ordinal }
    private val request = CreateVenueRequest(
        venue.ownerId,
        venue.name,
        venue.location,
        venue.description,
        venue.venueTypeId,
        workingDays,
        venue.workingHours,
        venue.maximumCapacity,
    )

    @Test
    fun `should return failing response if venue name is empty`() {
        val invalidRequest = request.copy(name = "")

        val response = venueService.create(invalidRequest)

        response.success `should be equal to` false
        response.message `should be equal to` "Name cannot be empty."

        verifyNoInteractions(venueRepository, userRepository, workingDaysRepository)
    }

    @Test
    fun `should return failing response if venue location is empty`() {
        val invalidRequest = request.copy(location = "")

        val response = venueService.create(invalidRequest)

        response.success `should be equal to` false
        response.message `should be equal to` "Location cannot be empty."

        verifyNoInteractions(venueRepository, userRepository, workingDaysRepository)
    }

    @Test
    fun `should return failing response if venue working days are empty`() {
        val invalidRequest = request.copy(workingDays = emptyList())

        val response = venueService.create(invalidRequest)

        response.success `should be equal to` false
        response.message `should be equal to` "Working days cannot be empty."

        verifyNoInteractions(venueRepository, userRepository, workingDaysRepository)
    }

    @Test
    fun `should return failing response if venue working days are invalid`() {
        val invalidRequest = request.copy(workingDays = listOf(-1))

        val response = venueService.create(invalidRequest)

        response.success `should be equal to` false
        response.message `should be equal to` "Working days must be between Monday and Sunday."

        verifyNoInteractions(venueRepository, userRepository, workingDaysRepository)
    }

    @Test
    fun `should return failing response if venue working hours are empty`() {
        val invalidRequest = request.copy(workingHours = "")

        val response = venueService.create(invalidRequest)

        response.success `should be equal to` false
        response.message `should be equal to` "Working hours cannot be empty."

        verifyNoInteractions(venueRepository, userRepository, workingDaysRepository)
    }

    @Test
    fun `should return failing response if venue maximum capacity is not positive`() {
        val invalidRequest = request.copy(maximumCapacity = 0)

        val response = venueService.create(invalidRequest)

        response.success `should be equal to` false
        response.message `should be equal to` "Maximum capacity must be positive."

        verifyNoInteractions(venueRepository, userRepository, workingDaysRepository)
    }

    @Test
    fun `should return failing response if venue type ID is invalid`() {
        val invalidRequest = request.copy(typeId = -1)

        val response = venueService.create(invalidRequest)

        response.success `should be equal to` false
        response.message `should be equal to` "Invalid venue type id."

        verifyNoInteractions(venueRepository, userRepository, workingDaysRepository)
    }

    @Test
    fun `should return failing response if user doesn't exist`() {
        `when`(userRepository.findById(venue.ownerId)).thenReturn(Optional.empty())

        val response = venueService.create(request)

        response.success `should be equal to` false
        response.message `should be equal to` "User does not exist."

        verify(userRepository).findById(venue.ownerId)
        verifyNoMoreInteractions(userRepository)
        verifyNoInteractions(venueRepository, workingDaysRepository)
    }

    @Test
    fun `should return failing response if user is not owner`() {
        `when`(userRepository.findById(venue.ownerId)).thenReturn(Optional.of(user))

        val response = venueService.create(request)

        response.success `should be equal to` false
        response.message `should be equal to` "User is not a valid owner."

        verify(userRepository).findById(venue.ownerId)
        verifyNoMoreInteractions(userRepository)
        verifyNoInteractions(venueRepository, workingDaysRepository)
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
        verifyNoMoreInteractions(userRepository, venueRepository, workingDaysRepository)
    }

    @Test
    fun `should return failing response if unable to save venue`() {
        `when`(userRepository.findById(venue.ownerId)).thenReturn(Optional.of(owner))
        `when`(venueRepository.existsByOwnerIdAndNameIgnoreCase(venue.ownerId, venue.name)).thenReturn(false)
        `when`(venueRepository.saveAndFlush(any())).thenThrow(RuntimeException("Unable to save venue"))

        val response = venueService.create(request)

        response.success `should be equal to` false
        response.message `should be equal to` "Error while creating venue. Please try again later."

        verify(userRepository).findById(venue.ownerId)
        verify(venueRepository).existsByOwnerIdAndNameIgnoreCase(venue.ownerId, venue.name)
        verify(venueRepository).saveAndFlush(venueArgumentCaptor.capture())
        verifyNoMoreInteractions(userRepository, venueRepository)
        verifyNoInteractions(workingDaysRepository)
    }

    @Test
    fun `should return failing response if unable to save working hours`() {
        `when`(userRepository.findById(venue.ownerId)).thenReturn(Optional.of(owner))
        `when`(venueRepository.existsByOwnerIdAndNameIgnoreCase(venue.ownerId, venue.name)).thenReturn(false)
        `when`(venueRepository.save(any())).thenReturn(venue)
        `when`(workingDaysRepository.saveAll(listOf())).thenThrow(RuntimeException("Unable to save venue"))

        val response = venueService.create(request)

        response.success `should be equal to` false
        response.message `should be equal to` "Error while creating venue. Please try again later."

        verify(userRepository).findById(venue.ownerId)
        verify(venueRepository).existsByOwnerIdAndNameIgnoreCase(venue.ownerId, venue.name)
        verify(venueRepository).saveAndFlush(any())
        verify(workingDaysRepository).saveAll(anyList())
        verifyNoMoreInteractions(userRepository, venueRepository, workingDaysRepository)
    }

    @Test
    fun `should save venue`() {
        `when`(userRepository.findById(venue.ownerId)).thenReturn(Optional.of(owner))
        `when`(venueRepository.existsByOwnerIdAndNameIgnoreCase(venue.ownerId, venue.name)).thenReturn(false)
        `when`(venueRepository.save(any())).thenReturn(venue)
        `when`(workingDaysRepository.saveAll(anyList())).thenReturn(workingDaysEntity)

        val response = venueService.create(request)

        response.success `should be equal to` true
        response.message `should be equal to` "Venue ${venue.name} created successfully."
        response.data `should be equal to` venue.id

        verify(userRepository).findById(venue.ownerId)
        verify(venueRepository).existsByOwnerIdAndNameIgnoreCase(venue.ownerId, venue.name)
        verify(venueRepository).saveAndFlush(venueArgumentCaptor.capture())
        val savedVenue = venueArgumentCaptor.value
        savedVenue.name `should be equal to` venue.name
        savedVenue.location `should be equal to` venue.location
        savedVenue.description `should be equal to` venue.description
        savedVenue.venueTypeId `should be equal to` venue.venueTypeId
        savedVenue.workingHours `should be equal to` venue.workingHours
        savedVenue.maximumCapacity `should be equal to` venue.maximumCapacity
        savedVenue.availableCapacity `should be equal to` venue.maximumCapacity

        verify(workingDaysRepository).saveAll(workingDaysCaptor.capture())
        val savedWorkingDays = workingDaysCaptor.value
        savedWorkingDays.size `should be equal to` workingDays.size
        for (i in workingDays.indices) {
            savedWorkingDays[i].venueId `should be equal to` venue.id
            savedWorkingDays[i].dayOfWeek `should be equal to` workingDays[i]
        }

        verifyNoMoreInteractions(userRepository, venueRepository, workingDaysRepository)
    }
}