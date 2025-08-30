package fipu.diplomski.dmaglica.mobile.integration.venue

import fipu.diplomski.dmaglica.model.request.CreateVenueRequest
import fipu.diplomski.dmaglica.model.response.DataResponse
import jakarta.transaction.Transactional
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should not be`
import org.junit.jupiter.api.Test


@Transactional
class CreateVenueIntegrationTest : AbstractVenueIntegrationTest() {

    @Test
    fun `should create venue successfully with valid owner`() {
        val request = CreateVenueRequest(
            ownerId = owner.id,
            name = "Owner Venue",
            location = "Main Street",
            description = "A test venue",
            workingHours = "09:00 - 17:00",
            maximumCapacity = 50,
            typeId = 1,
            workingDays = listOf(1, 2, 3)
        )

        val response: DataResponse<Int> = venueService.create(request)

        response.success `should be equal to` true
        response.message `should be equal to` "Venue ${request.name} created successfully."
        response.data `should not be` null

        val createdVenue = venueRepository.findById(response.data!!).get()
        createdVenue.name `should be equal to` "Owner Venue"
        createdVenue.ownerId `should be equal to` owner.id

        val workingDays = workingDaysRepository.findAllByVenueId(createdVenue.id)
        workingDays.map { it.dayOfWeek } `should be equal to` listOf(1, 2, 3)
    }

    @Test
    fun `should fail when user does not exist`() {
        val request = CreateVenueRequest(
            ownerId = 999,
            name = "Ghost Venue",
            location = "Nowhere",
            description = "Should fail",
            workingHours = "10:00 - 18:00",
            maximumCapacity = 20,
            typeId = 1,
            workingDays = listOf(1)
        )

        val response: DataResponse<Int> = venueService.create(request)

        response.success `should be equal to` false
        response.message `should be equal to` "User does not exist."
    }

    @Test
    fun `should fail when user is not an owner`() {
        val request = CreateVenueRequest(
            ownerId = customer.id,
            name = "Customer Venue",
            location = "Fail Street",
            description = "Should fail",
            workingHours = "09:00 - 17:00",
            maximumCapacity = 30,
            typeId = 1,
            workingDays = listOf(1)
        )

        val response: DataResponse<Int> = venueService.create(request)

        response.success `should be equal to` false
        response.message `should be equal to` "User is not a valid owner."
    }

    @Test
    fun `should fail when venue name already exists for the owner`() {
        val existingVenue = createVenue(ownerId = owner.id, name = "Duplicate Venue")
        venueRepository.saveAndFlush(existingVenue)

        val request = CreateVenueRequest(
            ownerId = owner.id,
            name = "Duplicate Venue",
            location = "Somewhere",
            description = "Duplicate test",
            workingHours = "10:00 - 20:00",
            maximumCapacity = 40,
            typeId = 1,
            workingDays = listOf(2, 3)
        )

        val response: DataResponse<Int> = venueService.create(request)

        response.success `should be equal to` false
        response.message `should be equal to` "Venue with name 'Duplicate Venue' already exists for this owner."
    }
}