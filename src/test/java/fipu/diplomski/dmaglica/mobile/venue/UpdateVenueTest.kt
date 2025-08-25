package fipu.diplomski.dmaglica.mobile.venue

import fipu.diplomski.dmaglica.model.request.UpdateVenueRequest
import fipu.diplomski.dmaglica.repo.entity.VenueEntity
import fipu.diplomski.dmaglica.repo.entity.WorkingDaysEntity
import jakarta.persistence.EntityNotFoundException
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles
import java.util.*

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
class UpdateVenueTest : BaseVenueServiceTest() {

    private val venue = createVenue(availableCapacity = 50)
    private val workingDays = listOf(1, 2, 3, 4, 5)
    private val workingDaysEntity = createWorkingDays(venue.id, workingDays)

    @Test
    fun `should throw if venue not found`() {
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.empty())

        val exception = assertThrows<EntityNotFoundException> {
            venueService.update(venue.id, null)
        }

        exception.message?.let { it `should be equal to` "Venue with id ${venue.id} not found" }

        verify(venueRepository, times(1)).findById(venue.id)
        verifyNoMoreInteractions(venueRepository)
        verifyNoInteractions(workingDaysRepository)
    }

    @Test
    fun `should return early if request is null`() {
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(venue))

        val response = venueService.update(venue.id, null)

        response.success `should be` false
        response.message `should be equal to` "Update request cannot be null. Provide at least one field to update."

        verify(venueRepository, times(1)).findById(venue.id)
        verifyNoMoreInteractions(venueRepository)
        verifyNoInteractions(workingDaysRepository)
    }

    @Test
    fun `should return early if name in request is not valid`() {
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(venue))

        val response = venueService.update(venue.id, UpdateVenueRequest(name = ""))

        response.success `should be` false
        response.message `should be equal to` "Name is not valid."

        verify(venueRepository, times(1)).findById(venue.id)
        verifyNoMoreInteractions(venueRepository)
        verifyNoInteractions(workingDaysRepository)
    }

    @Test
    fun `should return early if location in request is not valid`() {
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(venue))

        val response = venueService.update(venue.id, UpdateVenueRequest(location = ""))

        response.success `should be` false
        response.message `should be equal to` "Location is not valid."

        verify(venueRepository, times(1)).findById(venue.id)
        verifyNoMoreInteractions(venueRepository)
        verifyNoInteractions(workingDaysRepository)
    }

    @Test
    fun `should return early if description in request is not valid`() {
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(venue))

        val response = venueService.update(venue.id, UpdateVenueRequest(description = ""))

        response.success `should be` false
        response.message `should be equal to` "Description is not valid."

        verify(venueRepository, times(1)).findById(venue.id)
        verifyNoMoreInteractions(venueRepository)
        verifyNoInteractions(workingDaysRepository)
    }

    @Test
    fun `should return early if type id in request is not valid`() {
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(venue))

        val response = venueService.update(venue.id, UpdateVenueRequest(typeId = -1))

        response.success `should be` false
        response.message `should be equal to` "Invalid venue type id."

        verify(venueRepository, times(1)).findById(venue.id)
        verifyNoMoreInteractions(venueRepository)
        verifyNoInteractions(workingDaysRepository)
    }

    @Test
    fun `should return early if working days in request are not valid`() {
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(venue))

        val response = venueService.update(venue.id, UpdateVenueRequest(workingDays = listOf(0, 8)))

        response.success `should be` false
        response.message `should be equal to` "Working days must be between Monday and Sunday."

        verify(venueRepository, times(1)).findById(venue.id)
        verifyNoMoreInteractions(venueRepository)
        verifyNoInteractions(workingDaysRepository)
    }


    @Test
    fun `should return early if working hours in request are not valid`() {
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(venue))

        val response = venueService.update(venue.id, UpdateVenueRequest(workingHours = ""))

        response.success `should be` false
        response.message `should be equal to` "Working hours are not valid."

        verify(venueRepository, times(1)).findById(venue.id)
        verifyNoMoreInteractions(venueRepository)
        verifyNoInteractions(workingDaysRepository)
    }

    @Test
    fun `should return early if maximum capacity in request is not valid`() {
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(venue))

        val response = venueService.update(
            venue.id,
            UpdateVenueRequest(maximumCapacity = -1)
        )

        response.success `should be` false
        response.message `should be equal to` "Maximum capacity is not valid."

        verify(venueRepository, times(1)).findById(venue.id)
        verifyNoMoreInteractions(venueRepository)
        verifyNoInteractions(workingDaysRepository)
    }

    @Test
    fun `should return early if request does not change anything`() {
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(venue))
        `when`(workingDaysRepository.findAllByVenueId(venue.id)).thenReturn(workingDaysEntity)

        val response = venueService.update(
            venue.id,
            UpdateVenueRequest(
                name = venue.name,
                location = venue.location,
                description = venue.description,
                typeId = venue.venueTypeId,
                workingDays = workingDays,
                workingHours = venue.workingHours
            )
        )

        response.success `should be` false
        response.message `should be` "No modifications found. Please change at least one field."

        verify(venueRepository, times(1)).findById(venue.id)
        verify(workingDaysRepository, times(1)).findAllByVenueId(venue.id)
        verifyNoMoreInteractions(venueRepository, workingDaysRepository)
    }

    @Test
    fun `should not update if new maximum availability exceed current availability`() {
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(venue))
        `when`(workingDaysRepository.findAllByVenueId(venue.id)).thenReturn(workingDaysEntity)

        val response = venueService.update(
            venue.id,
            UpdateVenueRequest(maximumCapacity = 40)
        )

        response.success `should be` false
        response.message `should be equal to` "New maximum capacity cannot exceed current available capacity."

        verify(venueRepository, times(1)).findById(venue.id)
        verify(workingDaysRepository, times(1)).findAllByVenueId(venue.id)
        verifyNoMoreInteractions(venueRepository, workingDaysRepository)
    }

    @Test
    fun `should return early if unable to save new working days`() {
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(venue))
        `when`(workingDaysRepository.saveAll(anyList())).thenReturn(workingDaysEntity)
        `when`(workingDaysRepository.saveAll(anyList())).thenThrow(RuntimeException("Save failed"))

        val response = venueService.update(
            venue.id,
            UpdateVenueRequest(workingDays = listOf(1, 2, 3, 4))
        )

        response.success `should be` false
        response.message `should be equal to` "Error while updating working days. Please try again later."

        verify(venueRepository, times(1)).findById(venue.id)
        verify(workingDaysRepository, times(1)).findAllByVenueId(venue.id)
        verify(workingDaysRepository, times(1)).saveAll(anyList())
        verifyNoMoreInteractions(venueRepository, workingDaysRepository)
    }

    @Test
    fun `should return failure response if save fails`() {
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(venue))
        `when`(venueRepository.save(any())).thenThrow(RuntimeException("Save failed"))
        `when`(workingDaysRepository.findAllByVenueId(venue.id)).thenReturn(workingDaysEntity)

        val response = venueService.update(venue.id, UpdateVenueRequest(name = "New name"))

        response.success `should be equal to` false
        response.message `should be equal to` "Error while updating venue. Please try again later."

        verify(venueRepository, times(1)).findById(venue.id)
        verify(venueRepository, times(1)).save(any())
    }

    @Test
    fun `should update only select fields`() {
        val newVenue = VenueEntity().apply {
            id = venue.id
            name = "New name"
            location = venue.location
            description = "New description"
            workingHours = venue.workingHours
            averageRating = venue.averageRating
            venueTypeId = venue.venueTypeId
        }
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(venue))
        `when`(venueRepository.save(any())).thenReturn(newVenue)
        `when`(workingDaysRepository.findAllByVenueId(venue.id)).thenReturn(workingDaysEntity)

        val response = venueService.update(
            venue.id,
            UpdateVenueRequest(name = newVenue.name, description = newVenue.description)
        )

        verify(workingDaysRepository, times(1)).findAllByVenueId(venue.id)
        verify(venueRepository).save(venueArgumentCaptor.capture())
        val savedVenue = venueArgumentCaptor.value

        response.success `should be equal to` true
        response.message `should be equal to` "Venue updated successfully."

        savedVenue.name `should be equal to` newVenue.name
        savedVenue.location `should be equal to` venue.location
        savedVenue.description `should be equal to` newVenue.description
        savedVenue.workingHours `should be equal to` venue.workingHours
        savedVenue.venueTypeId `should be equal to` venue.venueTypeId

        verifyNoMoreInteractions(venueRepository, workingDaysRepository)
    }

    @Test
    fun `should update venue and remove days`() {
        val newVenue = VenueEntity().apply {
            id = venue.id
            name = "New name"
            location = "New location"
            description = "New description"
            workingHours = "New working hours"
            maximumCapacity = 80
            availableCapacity = 80
            averageRating = venue.averageRating
            venueTypeId = 2
        }
        val newWorkingDaysEntity = createWorkingDays(venue.id, listOf(1, 2, 3, 4))
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(venue))
        `when`(workingDaysRepository.findAllByVenueId(venue.id)).thenReturn(workingDaysEntity)
        `when`(workingDaysRepository.saveAll(anyList())).thenReturn(newWorkingDaysEntity)
        `when`(venueRepository.save(any())).thenReturn(newVenue)
        val newAvailability = newVenue.maximumCapacity - venue.availableCapacity

        val response = venueService.update(
            venue.id,
            UpdateVenueRequest(
                name = newVenue.name,
                location = newVenue.location,
                description = newVenue.description,
                typeId = newVenue.venueTypeId,
                workingDays = listOf(1, 2, 3, 4),
                workingHours = newVenue.workingHours,
                maximumCapacity = newVenue.maximumCapacity,
            )
        )

        val oldDays = workingDaysEntity.map { it.dayOfWeek }.toSet()
        val newDays = listOf(1, 2, 3, 4).toSet()
        val toRemove = oldDays - newDays
        val filtered = workingDaysEntity.filter { it.dayOfWeek in toRemove }

        verify(workingDaysRepository).findAllByVenueId(venue.id)
        verify(workingDaysRepository).deleteAll(filtered)

        verify(venueRepository).save(venueArgumentCaptor.capture())
        val savedVenue = venueArgumentCaptor.value

        response.success `should be equal to` true
        response.message `should be equal to` "Venue updated successfully."

        savedVenue.name `should be equal to` newVenue.name
        savedVenue.location `should be equal to` newVenue.location
        savedVenue.description `should be equal to` newVenue.description
        savedVenue.workingHours `should be equal to` newVenue.workingHours
        savedVenue.venueTypeId `should be equal to` newVenue.venueTypeId
        savedVenue.maximumCapacity `should be equal to` newVenue.maximumCapacity
        savedVenue.availableCapacity `should be equal to` newAvailability

        verifyNoMoreInteractions(venueRepository, workingDaysRepository)
    }

    @Test
    fun `should update venue and add days`() {
        val newVenue = VenueEntity().apply {
            id = venue.id
            name = "New name"
            location = "New location"
            description = "New description"
            workingHours = "New working hours"
            maximumCapacity = 80
            availableCapacity = 80
            averageRating = venue.averageRating
            venueTypeId = 2
        }
        val newWorkingDaysEntity = createWorkingDays(venue.id, listOf(1, 2, 3, 4, 5, 6))
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(venue))
        `when`(workingDaysRepository.findAllByVenueId(venue.id)).thenReturn(workingDaysEntity)
        `when`(workingDaysRepository.saveAll(anyList())).thenReturn(newWorkingDaysEntity)
        `when`(venueRepository.save(any())).thenReturn(newVenue)
        val newAvailability = newVenue.maximumCapacity - venue.availableCapacity

        val response = venueService.update(
            venue.id,
            UpdateVenueRequest(
                name = newVenue.name,
                location = newVenue.location,
                description = newVenue.description,
                typeId = newVenue.venueTypeId,
                workingDays = listOf(1, 2, 3, 4, 5, 6),
                workingHours = newVenue.workingHours,
                maximumCapacity = newVenue.maximumCapacity,
            )
        )

        val oldDays = workingDaysEntity.map { it.dayOfWeek }.toSet()
        val newDays = listOf(1, 2, 3, 4, 5, 6).toSet()
        val toAdd = newDays - oldDays
        val newEntities = toAdd.map { day ->
            WorkingDaysEntity().apply {
                this.venueId = venueId
                this.dayOfWeek = day
            }
        }

        verify(workingDaysRepository).findAllByVenueId(venue.id)
        verify(workingDaysRepository).saveAll(workingDaysCaptor.capture())
        val savedDays = workingDaysCaptor.value
        savedDays.size `should be equal to` newEntities.size
        savedDays[0].venueId `should be equal to` venue.id
        savedDays[0].dayOfWeek `should be equal to` newEntities[0].dayOfWeek

        verify(venueRepository).save(venueArgumentCaptor.capture())
        val savedVenue = venueArgumentCaptor.value

        response.success `should be equal to` true
        response.message `should be equal to` "Venue updated successfully."

        savedVenue.name `should be equal to` newVenue.name
        savedVenue.location `should be equal to` newVenue.location
        savedVenue.description `should be equal to` newVenue.description
        savedVenue.workingHours `should be equal to` newVenue.workingHours
        savedVenue.venueTypeId `should be equal to` newVenue.venueTypeId
        savedVenue.maximumCapacity `should be equal to` newVenue.maximumCapacity
        savedVenue.availableCapacity `should be equal to` newAvailability

        verifyNoMoreInteractions(venueRepository, workingDaysRepository)
    }
}