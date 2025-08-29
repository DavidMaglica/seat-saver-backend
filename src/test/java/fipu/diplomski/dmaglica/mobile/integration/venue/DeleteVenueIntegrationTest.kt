package fipu.diplomski.dmaglica.mobile.integration.venue

import fipu.diplomski.dmaglica.model.response.BasicResponse
import fipu.diplomski.dmaglica.repo.entity.MenuImageEntity
import fipu.diplomski.dmaglica.repo.entity.VenueImageEntity
import jakarta.transaction.Transactional
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test

@Transactional
class DeleteVenueIntegrationTest : AbstractVenueIntegrationTest() {

    @Test
    fun `should delete venue and cascade to working days, reservations, venue images, menu images, and venue ratings`() {
        workingDaysRepository.saveAll(createWorkingDays(venue.id, listOf(1, 2, 3)))
        reservationRepository.save(createReservation(userId = customer.id, venueId = venue.id))

        venueImageRepository.save(VenueImageEntity().apply {
            this.venueId = venue.id
            this.name = "venue.jpg"
            this.imageData = ByteArray(0)
        })
        menuImageRepository.save(MenuImageEntity().apply {
            this.venueId = venue.id
            this.name = "menu.jpg"
            this.imageData = ByteArray(0)
        })

        venueRatingRepository.save(createRating(venue.id, 5.0))

        venueRepository.findById(venue.id).isPresent `should be equal to` true
        workingDaysRepository.findAllByVenueId(venue.id).isNotEmpty() `should be equal to` true
        reservationRepository.findAll().any { it.venueId == venue.id } `should be equal to` true
        venueImageRepository.findAll().any { it.venueId == venue.id } `should be equal to` true
        menuImageRepository.findAll().any { it.venueId == venue.id } `should be equal to` true
        venueRatingRepository.findByVenueId(venue.id).isNotEmpty() `should be equal to` true

        val response = venueService.delete(venue.id)

        response.success `should be equal to` true
        response.message `should be equal to` "Venue successfully deleted."


        venueRepository.findById(venue.id).isPresent `should be equal to` false
        workingDaysRepository.findAllByVenueId(venue.id).isEmpty() `should be equal to` true
        reservationRepository.findAll().any { it.venueId == venue.id } `should be equal to` false
        venueImageRepository.findAll().any { it.venueId == venue.id } `should be equal to` false
        menuImageRepository.findAll().any { it.venueId == venue.id } `should be equal to` false
        venueRatingRepository.findByVenueId(venue.id).isEmpty() `should be equal to` true
    }

    @Test
    fun `should handle deletion of non-existent venue gracefully`() {
        val nonExistentVenueId = 9999

        val response: BasicResponse = venueService.delete(nonExistentVenueId)

        response.success `should be equal to` true
        response.message `should be equal to` "Venue successfully deleted."
    }
}