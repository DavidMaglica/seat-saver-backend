package fipu.diplomski.dmaglica.mobile.integration.venue

import fipu.diplomski.dmaglica.model.data.Venue
import fipu.diplomski.dmaglica.model.response.PagedResponse
import fipu.diplomski.dmaglica.repo.entity.VenueEntity
import jakarta.transaction.Transactional
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should contain all`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

@Transactional
class GetVenueIntegrationTest : AbstractVenueIntegrationTest() {

    lateinit var venue1: VenueEntity
    lateinit var venue2: VenueEntity
    lateinit var venue3: VenueEntity

    @BeforeEach
    fun setupVenues() {
        venue1 = createVenue(name = "Alpha Venue", location = "Porec", averageRating = 4.5)
        venue2 = createVenue(name = "Beta Venue", location = "Porec", averageRating = 5.0)
        venue3 = createVenue(name = "Gamma Venue", location = "Split", venueTypeId = 2, averageRating = 3.5)

        venueRepository.saveAll(listOf(venue1, venue2, venue3))
    }

    @Test
    fun `should return all venues with pagination`() {
        val pageable = PageRequest.of(0, 10, Sort.by("name"))
        val response: PagedResponse<Venue> = venueService.getAll(pageable, null, null)

        response.content.size `should be equal to` 4
        response.content.map { it.name } `should be equal to` listOf(
            "Alpha Venue",
            "Beta Venue",
            "Gamma Venue",
            "Test Venue"
        )
        response.page `should be equal to` 0
        response.size `should be equal to` 10
        response.totalElements `should be equal to` 4
    }

    @Test
    fun `should filter venues by search query`() {
        val pageable = PageRequest.of(0, 10)
        val response: PagedResponse<Venue> = venueService.getAll(pageable, "Alpha", null)

        response.content.size `should be equal to` 1
        response.content.first().name `should be equal to` "Alpha Venue"
    }

    @Test
    fun `should filter venues by typeIds`() {
        val pageable = PageRequest.of(0, 10)
        val response: PagedResponse<Venue> = venueService.getAll(pageable, null, listOf(1))

        response.content.size `should be equal to` 3
        response.content.map { it.id } `should contain all` listOf(venue1.id, venue2.id, venue.id)
    }

    @Test
    fun `should return nearby venues using fixed coordinates`() {
        val pageable = PageRequest.of(0, 10)
        val latitude = 45.229682953209725
        val longitude = 13.602388713617216

        val response: PagedResponse<Venue> = venueService.getNearbyVenues(pageable, latitude, longitude)

        response.content.isNotEmpty() `should be equal to` true
        response.content.all { it.location.contains("Porec") } `should be equal to` true
    }

    @Test
    fun `should return new venues sorted by id descending`() {
        val pageable = PageRequest.of(0, 10)
        val response = venueService.getNewVenues(pageable)

        response.content.first().id `should be equal to` venue3.id
        response.content.last().id `should be equal to` venue.id
    }

    @Test
    fun `should return suggested venues based on criteria`() {
        val pageable = PageRequest.of(0, 10)
        val response = venueService.getSuggestedVenues(pageable)

        response.content.size `should be equal to` 2
    }

    @Test
    fun `should return trending venues based on reservation stats`() {
        reservationRepository.save(createReservation(venueId = venue1.id))
        reservationRepository.save(createReservation(venueId = venue1.id))
        reservationRepository.save(createReservation(venueId = venue2.id))

        val pageable = PageRequest.of(0, 10)
        val response = venueService.getTrendingVenues(pageable)

        response.content.first().id `should be equal to` venue1.id
        response.content.last().id `should be equal to` venue2.id
    }
}