package fipu.diplomski.dmaglica.mobile.integration.venue

import fipu.diplomski.dmaglica.model.request.UpdateVenueRequest
import fipu.diplomski.dmaglica.model.response.BasicResponse
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import org.springframework.transaction.annotation.Transactional

@Transactional
class UpdateVenueIntegrationTest : AbstractVenueIntegrationTest() {

    @Test
    fun `should fail if no modifications are provided`() {
        val request = UpdateVenueRequest(
            name = null,
            location = null,
            workingHours = null,
            maximumCapacity = null,
            typeId = null,
            description = null,
            workingDays = emptyList()
        )

        val response: BasicResponse = venueService.update(venue.id, request)

        response.success `should be equal to` false
        response.message `should be equal to` "No modifications found. Please change at least one field."
    }

    @Test
    fun `should prevent shrinking capacity below current reservations`() {
        venue.availableCapacity = 50
        venueRepository.saveAndFlush(venue)

        val request = UpdateVenueRequest(maximumCapacity = 40)

        val response: BasicResponse = venueService.update(venue.id, request)

        response.success `should be equal to` false
        response.message `should be equal to` "New maximum capacity cannot exceed current available capacity."
    }

    @Test
    fun `should update working days by adding and removing`() {
        val venue = venueRepository.findAll().first()
        val venueId = venue.id

        workingDaysRepository.saveAll(
            listOf(
                createWorkingDays(venueId, listOf(1, 2))
            ).flatten()
        )

        val request = UpdateVenueRequest(
            workingDays = listOf(2, 3)
        )

        val response: BasicResponse = venueService.update(venueId, request)

        response.success `should be equal to` true
        response.message `should be equal to` "Venue updated successfully."

        val updatedDays = workingDaysRepository.findAllByVenueId(venueId).map { it.dayOfWeek }.toSet()
        updatedDays `should be equal to` setOf(2, 3)
    }

    @Test
    fun `should persist venue property changes`() {
        val request = UpdateVenueRequest(
            name = "Updated Venue",
            location = "New Location",
            workingHours = "10:00 - 20:00",
            maximumCapacity = venue.maximumCapacity + 50,
            typeId = venue.venueTypeId,
            description = "Updated Description",
            workingDays = emptyList()
        )

        val response: BasicResponse = venueService.update(venue.id, request)

        response.success `should be equal to` true
        response.message `should be equal to` "Venue updated successfully."

        val updatedVenue = venueRepository.findById(venue.id).get()
        updatedVenue.name `should be equal to` "Updated Venue"
        updatedVenue.location `should be equal to` "New Location"
        updatedVenue.workingHours `should be equal to` "10:00 - 20:00"
        updatedVenue.maximumCapacity `should be equal to` venue.maximumCapacity + 50
        updatedVenue.description `should be equal to` "Updated Description"
    }
}