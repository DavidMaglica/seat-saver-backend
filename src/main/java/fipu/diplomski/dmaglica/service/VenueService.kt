package fipu.diplomski.dmaglica.service

import fipu.diplomski.dmaglica.model.BasicResponse
import fipu.diplomski.dmaglica.model.Venue
import fipu.diplomski.dmaglica.repo.VenueRatingRepository
import fipu.diplomski.dmaglica.repo.VenueRepository
import fipu.diplomski.dmaglica.repo.VenueTypeRepository
import fipu.diplomski.dmaglica.repo.entity.VenueEntity
import fipu.diplomski.dmaglica.repo.entity.VenueRatingEntity
import fipu.diplomski.dmaglica.util.dbActionWithTryCatch
import org.springframework.transaction.annotation.Transactional
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.sql.SQLException

@Service
class VenueService(
    private val venueRepository: VenueRepository,
    private val venueRatingRepository: VenueRatingRepository,
    private val venueTypeRepository: VenueTypeRepository,
    private val imageService: ImageService,
) {

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    fun getAll(): MutableList<VenueEntity> = venueRepository.findAll()

    @Transactional(readOnly = true)
    fun getType(typeId: Int): String =
        venueTypeRepository.findById(typeId).orElseThrow { SQLException("Venue type id: $typeId not found.") }.type

    @Transactional(readOnly = true)
    fun getAllTypes(): List<String> = venueTypeRepository.findAll().map { it.type }

    fun getVenueImages(venueId: Int, venueName: String) = imageService.getVenueImages(venueId, venueName)

    fun getMenuImage(venueId: Int, venueName: String) = imageService.getMenuImage(venueId, venueName)

    @Transactional
    fun create(
        name: String,
        location: String,
        description: String,
        typeId: Int,
        workingHours: String
    ): BasicResponse {
        val venue = VenueEntity().also {
            it.id
            it.name = name
            it.location = location
            it.description = description
            it.workingHours = workingHours
            it.venueTypeId = typeId
            it.averageRating = 0.0
        }

        dbActionWithTryCatch("Error while saving venue with name $name") {
            venueRepository.save(venue)
        }

        return BasicResponse(true, "Venue with name $name successfully created")
    }

    fun uploadVenueImage(venueId: Int, image: MultipartFile): BasicResponse =
        imageService.uploadVenueImage(venueId, image)

    fun uploadMenuImage(venueId: Int, image: MultipartFile): BasicResponse =
        imageService.uploadMenuImage(venueId, image)

    @Transactional
    fun update(
        venueId: Int,
        name: String?,
        location: String?,
        workingHours: String?,
        typeId: Int?,
        description: String?
    ): Venue {
        val venue = venueRepository.findById(venueId)
            .orElseThrow { SQLException("Venue with id $venueId not found") }

        val updatedVenue = venue.also {
            it.name = name ?: venue.name
            it.location = location ?: venue.location
            it.workingHours = workingHours ?: venue.workingHours
            it.venueTypeId = typeId ?: venue.venueTypeId
            it.description = description ?: venue.description
        }

        dbActionWithTryCatch("Error while updating venue with id $venueId") {
            venueRepository.save(updatedVenue)
        }

        return Venue(
            id = updatedVenue.id,
            name = updatedVenue.name,
            location = updatedVenue.location,
            workingHours = updatedVenue.workingHours,
            rating = updatedVenue.averageRating,
            venueTypeId = updatedVenue.venueTypeId,
            description = updatedVenue.description
        )
    }

    @Transactional
    fun rate(venueId: Int, rating: Double): BasicResponse {
        val newRatingEntity = VenueRatingEntity().also {
            it.id
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
        dbActionWithTryCatch("Error while updating rating for venue with id $venueId") {
            venueRatingRepository.save(newRatingEntity)
        }

        val updatedVenue = venue.also { it.averageRating = newRating }
        dbActionWithTryCatch("Error while updating venue with id $venueId") {
            venueRepository.save(updatedVenue)
        }

        return BasicResponse(true, "Venue with id $venueId successfully rated with rating $rating")
    }

    @Transactional
    fun delete(venueId: Int) {
        dbActionWithTryCatch("Error while deleting venue with id: $venueId") {
            venueRepository.deleteById(venueId)
        }
    }
}
