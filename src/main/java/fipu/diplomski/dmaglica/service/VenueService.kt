package fipu.diplomski.dmaglica.service

import fipu.diplomski.dmaglica.model.request.CreateVenueRequest
import fipu.diplomski.dmaglica.model.request.UpdateVenueRequest
import fipu.diplomski.dmaglica.model.response.BasicResponse
import fipu.diplomski.dmaglica.repo.ReservationRepository
import fipu.diplomski.dmaglica.repo.VenueRatingRepository
import fipu.diplomski.dmaglica.repo.VenueRepository
import fipu.diplomski.dmaglica.repo.VenueTypeRepository
import fipu.diplomski.dmaglica.repo.entity.*
import fipu.diplomski.dmaglica.util.dbActionWithTryCatch
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

@Service
class VenueService(
    private val venueRepository: VenueRepository,
    private val venueRatingRepository: VenueRatingRepository,
    private val venueTypeRepository: VenueTypeRepository,
    private val imageService: ImageService,
    private val reservationRepository: ReservationRepository,
) {

    @Transactional(readOnly = true)
    fun get(venueId: Int): VenueEntity {
        val venue: VenueEntity =
            venueRepository.findById(venueId)
                .orElseThrow { EntityNotFoundException("Venue with id: $venueId not found.") }
        val venueRating: List<VenueRatingEntity> = venueRatingRepository.findByVenueId(venueId)
        venue.averageRating = venueRating.map { it.rating }.average()

        val currentTimestamp: LocalDateTime = LocalDateTime.now()
        val (lowerBound, upperBound) = getSurroundingHalfHours(currentTimestamp)

        val reservations = reservationRepository.findByVenueIdAndDatetimeIn(
            venueId, listOf(lowerBound, upperBound)
        )

        if (reservations.isNotEmpty()) {
            calculateCurrentAvailableCapacity(venue, reservations)
        } else {
            venue.availableCapacity = venue.maximumCapacity
        }

        return venue
    }

    @Transactional(readOnly = true)
    fun getAll(): List<VenueEntity> {
        val venues = venueRepository.findAll()
        val ratings = venueRatingRepository.findAll()
        val currentTimestamp: LocalDateTime = LocalDateTime.now()
        val (lowerBound, upperBound) = getSurroundingHalfHours(currentTimestamp)
        val reservationsByVenueId =
            reservationRepository.findByDatetimeIn(listOf(lowerBound, upperBound)).groupBy { it.venueId }

        val averageRatingByVenueId = ratings.groupBy { it.venueId }
            .mapValues { (_, venueRatings) -> venueRatings.map { it.rating }.average() }

        for (venue in venues) {
            venue.averageRating = averageRatingByVenueId[venue.id] ?: 0.0

            val venueReservations = reservationsByVenueId[venue.id].orEmpty()
            if (venueReservations.isNotEmpty()) {
                calculateCurrentAvailableCapacity(venue, venueReservations)
            } else {
                venue.availableCapacity = venue.maximumCapacity
            }
        }

        return venues
    }

    @Transactional(readOnly = true)
    fun getType(typeId: Int): String =
        venueTypeRepository.getReferenceById(typeId).type

    @Transactional(readOnly = true)
    fun getVenueRating(venueId: Int): Double = venueRepository.findById(venueId)
        .orElseThrow { EntityNotFoundException("Venue with id: $venueId not found.") }.averageRating

    @Transactional(readOnly = true)
    fun getAllTypes(): List<VenueTypeEntity> = venueTypeRepository.findAll()

    fun getVenueImages(venueId: Int, venueName: String): List<ByteArray> =
        imageService.getVenueImages(venueId, venueName)

    fun getMenuImage(venueId: Int, venueName: String): MenuImageEntity = imageService.getMenuImage(venueId, venueName)

    @Transactional
    fun create(request: CreateVenueRequest): BasicResponse {
        val venue = VenueEntity().apply {
            name = request.name
            location = request.location
            description = request.description
            workingHours = request.workingHours
            maximumCapacity = request.maximumCapacity
            availableCapacity = request.availableCapacity
            venueTypeId = request.typeId
            averageRating = 0.0
        }

        dbActionWithTryCatch("Error while saving venue: ${request.name}") {
            venueRepository.save(venue)
        }

        return BasicResponse(true, "Venue ${request.name} created successfully.")
    }

    fun uploadVenueImage(venueId: Int, image: MultipartFile): BasicResponse =
        imageService.uploadVenueImage(venueId, image)

    fun uploadMenuImage(venueId: Int, image: MultipartFile): BasicResponse =
        imageService.uploadMenuImage(venueId, image)

    @Transactional
    fun update(venueId: Int, request: UpdateVenueRequest?): BasicResponse {
        val venue = venueRepository.findById(venueId)
            .orElseThrow { EntityNotFoundException("Venue with id $venueId not found") }

        if (!isRequestValid(request)) return BasicResponse(false, "Request is not valid.")

        if (!containsVenueChanges(request, venue)) return BasicResponse(
            false,
            "No modifications found. Please change at least one field."
        )

        venue.apply {
            name = request?.name ?: venue.name
            location = request?.location ?: venue.location
            workingHours = request?.workingHours ?: venue.workingHours
            maximumCapacity = request?.maximumCapacity ?: venue.maximumCapacity
            availableCapacity = request?.availableCapacity ?: venue.availableCapacity
            venueTypeId = request?.typeId ?: venue.venueTypeId
            description = request?.description ?: venue.description
        }

        dbActionWithTryCatch("Error while updating venue with id $venueId") {
            venueRepository.save(venue)
        }

        return BasicResponse(true, "Venue updated successfully.")
    }

    @Transactional
    fun rate(venueId: Int, userRating: Double): BasicResponse {
        if (userRating < 0.5 || userRating > 5.0) return BasicResponse(false, "Rating must be between 0.5 and 5.")

        val venue = venueRepository.findById(venueId)
            .orElseThrow { EntityNotFoundException("Venue with id $venueId not found") }
        val venueRating =
            venueRatingRepository.findByVenueId(venueId)

        val newRatingEntity = VenueRatingEntity().also {
            it.id
            it.venueId = venueId
            it.rating = userRating
        }
        dbActionWithTryCatch("Error while updating rating for venue with id $venueId") {
            venueRatingRepository.save(newRatingEntity)
        }

        val newAverageRating = calculateNewAverageRating(venueRating, userRating)
        val updatedVenue = venue.also { it.averageRating = newAverageRating }
        dbActionWithTryCatch("Error while updating venue with id $venueId") {
            venueRepository.save(updatedVenue)
        }

        return BasicResponse(true, "Venue with id $venueId successfully rated with rating $userRating.")
    }

    private fun calculateNewAverageRating(
        venueRating: List<VenueRatingEntity>,
        userRating: Double
    ): Double {
        val cumulativeRating = venueRating.sumOf { it.rating } + userRating
        val cumulativeRatingCount = venueRating.size + 1

        return cumulativeRating / cumulativeRatingCount
    }

    @Transactional
    fun delete(venueId: Int): BasicResponse {
        dbActionWithTryCatch("Error while deleting venue with id: $venueId") {
            venueRepository.deleteById(venueId)
        }

        return BasicResponse(true, "Venue with id: $venueId successfully deleted.")
    }

    private fun isRequestValid(request: UpdateVenueRequest?): Boolean = request?.let {
        it.name != null || it.location != null || it.workingHours != null ||
                it.typeId != null || it.description != null
    } ?: false

    private fun containsVenueChanges(request: UpdateVenueRequest?, venue: VenueEntity): Boolean {
        if (request == null) return false

        return listOf(
            request.name?.takeIf { it != venue.name },
            request.location?.takeIf { it != venue.location },
            request.workingHours?.takeIf { it != venue.workingHours },
            request.maximumCapacity?.takeIf { it != venue.maximumCapacity },
            request.availableCapacity?.takeIf { it != venue.availableCapacity },
            request.typeId?.takeIf { it != venue.venueTypeId },
            request.description?.takeIf { it != venue.description }
        ).any { it != null }
    }

    private fun calculateCurrentAvailableCapacity(venue: VenueEntity, reservations: List<ReservationEntity>) {
        val totalGuests = reservations.sumOf { it.numberOfGuests }

        venue.availableCapacity = venue.maximumCapacity - totalGuests
    }

    private fun getSurroundingHalfHours(time: LocalDateTime): Pair<LocalDateTime, LocalDateTime> {
        val minute = time.minute
        val second = time.second
        val nano = time.nano
        val truncated = time.minusSeconds(second.toLong()).minusNanos(nano.toLong())

        val previous = when {
            minute < 30 -> truncated.withMinute(0)
            else -> truncated.withMinute(30)
        }

        val next = when {
            minute < 30 -> truncated.withMinute(30)
            else -> truncated.plusHours(1).withMinute(0)
        }

        return previous to next
    }
}
