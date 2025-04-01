package fipu.diplomski.dmaglica.service

import fipu.diplomski.dmaglica.model.BasicResponse
import fipu.diplomski.dmaglica.model.Venue
import fipu.diplomski.dmaglica.repo.VenueRatingRepository
import fipu.diplomski.dmaglica.repo.VenueRepository
import fipu.diplomski.dmaglica.repo.VenueTypeRepository
import fipu.diplomski.dmaglica.repo.entity.VenueEntity
import fipu.diplomski.dmaglica.repo.entity.VenueRatingEntity
import fipu.diplomski.dmaglica.util.dbActionWithTryCatch
import org.springframework.stereotype.Service
import java.sql.SQLException

@Service
class VenueService(
    private val venueRepository: VenueRepository,
    private val venueRatingRepository: VenueRatingRepository,
    private val venueTypeRepository: VenueTypeRepository,
) {

    fun create(name: String, location: String, description: String, typeId: Int, workingHours: String): BasicResponse {
        val venueEntity = VenueEntity().also {
            it.id
            it.name = name
            it.location = location
            it.description = description
            it.workingHours = workingHours
            it.venueTypeId = typeId
            it.averageRating = 0.0
        }

        dbActionWithTryCatch("Error while saving venue with name $name") {
            venueRepository.saveAndFlush(venueEntity)
        }
        return BasicResponse(true, "Venue with name $name successfully created")
    }

    fun get(venueId: Int): Venue {
        val venue = venueRepository.findById(venueId).orElseThrow { SQLException("Venue wit id: $venueId not found.") }
        return Venue(
            id = venue.id,
            name = venue.name,
            location = venue.location,
            workingHours = venue.workingHours,
            rating = venue.averageRating,
            venueTypeId = venue.venueTypeId,
            description = venue.description
        )
    }

    fun getVenueType(typeId: Int): String =
        venueTypeRepository.findById(typeId).orElseThrow { SQLException("Venue type id: $typeId not found.") }.type

    fun update() {
    }

    fun rate(venueId: Int, rating: Double) {
        val newRatingEntity = VenueRatingEntity().also {
            it.venueId = venueId
            it.rating = rating
        }

        val venue = venueRepository.findById(venueId)
            .orElseThrow { SQLException("Venue with id $venueId not found") }
        val venueRating =
            venueRatingRepository.findByVenueId(venueId)

        val cumulativeRating = venueRating.sumOf { it.rating } + rating
        val cumulativeRatingCount = venueRating.size + 1

        val newRating = cumulativeRating / cumulativeRatingCount
        val updatedVenue = venue.also {
            it.averageRating = newRating
        }
        dbActionWithTryCatch("Error while updating rating for venue with id $venueId") {
            venueRatingRepository.saveAndFlush(newRatingEntity)
            venueRepository.saveAndFlush(updatedVenue)
        }
    }

    fun delete() {
    }
}