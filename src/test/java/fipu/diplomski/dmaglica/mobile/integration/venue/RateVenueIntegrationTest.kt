package fipu.diplomski.dmaglica.mobile.integration.venue

import fipu.diplomski.dmaglica.model.response.BasicResponse
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


@Transactional
class RateVenueIntegrationTest : AbstractVenueIntegrationTest() {

    @Test
    fun `should insert new rating and update venue average`() {
        val response: BasicResponse = venueService.rate(1, 5.0, 1, "Great test venue!")

        response.success `should be equal to` true
        response.message `should be equal to` "Venue with id 1 successfully rated with rating 5.0."

        val ratings = venueRatingRepository.findByVenueId(1)
        ratings.size `should be equal to` 1
        ratings.first().venueId `should be equal to` 1
        ratings.first().rating `should be equal to` 5.0
        ratings.first().comment `should be equal to` "Great test venue!"

        val updatedVenue = venueRepository.findById(1).get()
        updatedVenue.averageRating `should be equal to` 5.0
    }

    @Test
    fun `should fail when rating is below allowed`() {
        val response: BasicResponse = venueService.rate(1, 0.0, 1, "Too low")

        response.success `should be equal to` false
        response.message `should be equal to` "Rating must be between 1.0 and 5.0."
    }

    @Test
    fun `should fail when rating is above allowed`() {
        val response: BasicResponse = venueService.rate(1, 6.0, 1, "Too high")

        response.success `should be equal to` false
        response.message `should be equal to` "Rating must be between 1.0 and 5.0."
    }

    @Test
    fun `should fail when user does not exist`() {
        val response: BasicResponse = venueService.rate(1, 4.0, 999, "Unknown user")

        response.success `should be equal to` false
        response.message `should be equal to` "User with id 999 not found."
    }

    @Test
    fun `should throw when venue does not exist`() {
        val exception = assertThrows<EntityNotFoundException> {
            venueService.rate(999, 4.0, 1, "Unknown venue")
        }

        exception.message `should be equal to` "Venue with id 999 not found"
    }

    @Test
    fun `should update average rating correctly with multiple ratings`() {
        venueService.rate(1, 5.0, 1, "Excellent")

        val secondUser = createCustomer("Second User", "second@email.com")
        userRepository.saveAndFlush(secondUser)
        venueService.rate(1, 3.0, secondUser.id, "Average")

        val updatedVenue = venueRepository.findById(1).get()
        updatedVenue.averageRating `should be equal to` 4.0

        val ratings = venueRatingRepository.findByVenueId(1)
        ratings.size `should be equal to` 2
    }

    @Test
    fun `should allow null comment`() {
        val response: BasicResponse = venueService.rate(1, 4.0, 1, null)

        response.success `should be equal to` true
        response.message `should be equal to` "Venue with id 1 successfully rated with rating 4.0."

        val rating = venueRatingRepository.findByVenueId(1).first()
        rating.comment `should be equal to` null
    }
}