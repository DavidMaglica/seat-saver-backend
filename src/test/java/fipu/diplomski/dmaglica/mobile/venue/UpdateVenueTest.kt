package fipu.diplomski.dmaglica.mobile.venue

import fipu.diplomski.dmaglica.model.request.UpdateVenueRequest
import fipu.diplomski.dmaglica.repo.entity.VenueEntity
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

    private val venue = createVenue()

    @Test
    fun `should throw if venue not found`() {
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.empty())

        val exception = assertThrows<EntityNotFoundException> {
            venueService.update(venue.id, null)
        }

        exception.message?.let { it `should be equal to` "Venue with id ${venue.id} not found" }

        verify(venueRepository, times(1)).findById(venue.id)
    }

    @Test
    fun `should return early if request is null`() {
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(venue))

        val response = venueService.update(venue.id, null)

        response.success `should be` false
        response.message `should be equal to` "Update request cannot be null. Provide at least one field to update."

        verify(venueRepository, times(1)).findById(venue.id)
    }

    @Test
    fun `should return early if name in request is not valid`() {
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(venue))

        val response = venueService.update(venue.id, UpdateVenueRequest(name = ""))

        response.success `should be` false
        response.message `should be equal to` "Name is not valid."

        verify(venueRepository, times(1)).findById(venue.id)
        verifyNoMoreInteractions(venueRepository)
    }

    @Test
    fun `should return early if location in request is not valid`() {
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(venue))

        val response = venueService.update(venue.id, UpdateVenueRequest(location = ""))

        response.success `should be` false
        response.message `should be equal to` "Location is not valid."

        verify(venueRepository, times(1)).findById(venue.id)
        verifyNoMoreInteractions(venueRepository)
    }

    @Test
    fun `should return early if description in request is not valid`() {
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(venue))

        val response = venueService.update(venue.id, UpdateVenueRequest(description = ""))

        response.success `should be` false
        response.message `should be equal to` "Description is not valid."

        verify(venueRepository, times(1)).findById(venue.id)
        verifyNoMoreInteractions(venueRepository)
    }

    @Test
    fun `should return early if type id in request is not valid`() {
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(venue))

        val response = venueService.update(venue.id, UpdateVenueRequest(typeId = -1))

        response.success `should be` false
        response.message `should be equal to` "Invalid venue type id."

        verify(venueRepository, times(1)).findById(venue.id)
        verifyNoMoreInteractions(venueRepository)
    }

    @Test
    fun `should return early if working hours in request are not valid`() {
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(venue))

        val response = venueService.update(venue.id, UpdateVenueRequest(workingHours = ""))

        response.success `should be` false
        response.message `should be equal to` "Working hours are not valid."

        verify(venueRepository, times(1)).findById(venue.id)
        verifyNoMoreInteractions(venueRepository)
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
    }

    @Test
    fun `should return early if request does not change anything`() {
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(venue))

        val response = venueService.update(
            venue.id,
            UpdateVenueRequest(
                name = venue.name,
                location = venue.location,
                description = venue.description,
                typeId = venue.venueTypeId,
                workingHours = venue.workingHours
            )
        )

        response.success `should be` false
        response.message `should be` "No modifications found. Please change at least one field."

        verify(venueRepository, times(1)).findById(venue.id)
    }

    @Test
    fun `should not update if new maximum availability exceed current availability`() {
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(venue))

        val response = venueService.update(
            venue.id,
            UpdateVenueRequest(maximumCapacity = 40)
        )

        response.success `should be` false
        response.message `should be equal to` "New maximum capacity cannot exceed current available capacity."

        verify(venueRepository, times(1)).findById(venue.id)
        verifyNoMoreInteractions(venueRepository)
    }

    @Test
    fun `should return failure response if save fails`() {
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(venue))
        `when`(venueRepository.save(any())).thenThrow(RuntimeException("Save failed"))

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

        val response = venueService.update(
            venue.id,
            UpdateVenueRequest(name = newVenue.name, description = newVenue.description)
        )

        verify(venueRepository).save(venueArgumentCaptor.capture())
        val savedVenue = venueArgumentCaptor.value

        response.success `should be equal to` true
        response.message `should be equal to` "Venue updated successfully."

        savedVenue.name `should be equal to` newVenue.name
        savedVenue.location `should be equal to` venue.location
        savedVenue.description `should be equal to` newVenue.description
        savedVenue.workingHours `should be equal to` venue.workingHours
        savedVenue.venueTypeId `should be equal to` venue.venueTypeId
    }

    @Test
    fun `should update venue`() {
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
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(venue))
        `when`(venueRepository.save(any())).thenReturn(newVenue)
        val newAvailability = newVenue.maximumCapacity - venue.availableCapacity

        val response = venueService.update(
            venue.id,
            UpdateVenueRequest(
                name = newVenue.name,
                location = newVenue.location,
                description = newVenue.description,
                typeId = newVenue.venueTypeId,
                workingHours = newVenue.workingHours,
                maximumCapacity = newVenue.maximumCapacity,
            )
        )

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
    }
}