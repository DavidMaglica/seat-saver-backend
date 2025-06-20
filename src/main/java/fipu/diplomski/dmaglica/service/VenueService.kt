package fipu.diplomski.dmaglica.service

import fipu.diplomski.dmaglica.model.request.CreateVenueRequest
import fipu.diplomski.dmaglica.model.request.UpdateVenueRequest
import fipu.diplomski.dmaglica.model.response.BasicResponse
import fipu.diplomski.dmaglica.repo.*
import fipu.diplomski.dmaglica.repo.entity.*
import fipu.diplomski.dmaglica.util.getSurroundingHalfHours
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime
import kotlin.jvm.optionals.getOrElse

@Service
class VenueService(
    private val venueRepository: VenueRepository,
    private val venueRatingRepository: VenueRatingRepository,
    private val venueTypeRepository: VenueTypeRepository,
    private val reservationRepository: ReservationRepository,
    private val imageService: ImageService,
    private val geolocationService: GeolocationService,
    private val userRepository: UserRepository,
) {

    companion object {
        private val logger = KotlinLogging.logger(VenueService::class.java.name)
    }

    @Transactional(readOnly = true)
    fun get(venueId: Int): VenueEntity {
        val venue: VenueEntity =
            venueRepository.findById(venueId)
                .orElseThrow { EntityNotFoundException("Venue with id: $venueId not found.") }
        val venueRating: List<VenueRatingEntity> = venueRatingRepository.findByVenueId(venueId)
        venue.averageRating = venueRating.map { it.rating }.average()

        val currentTimestamp: LocalDateTime = LocalDateTime.now()
        val (lowerBound, upperBound) = getSurroundingHalfHours(currentTimestamp)

        val reservations = reservationRepository.findByVenueIdAndDatetimeBetween(
            venueId, lowerBound, upperBound
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
        if (venues.isEmpty()) return emptyList()

        venues.sortBy { it.name }
        val venueIds = venues.map { it.id }
        val currentTimestamp: LocalDateTime = LocalDateTime.now()
        val (lowerBound, upperBound) = getSurroundingHalfHours(currentTimestamp)

        return buildVenueStats(venueIds, lowerBound, upperBound, venues)
    }

    @Transactional(readOnly = true)
    fun getNearbyVenues(latitude: Double?, longitude: Double?): List<VenueEntity> {
        val currentTimestamp: LocalDateTime = LocalDateTime.now()
        val (lowerBound, upperBound) = getSurroundingHalfHours(currentTimestamp)
        if (latitude == null || longitude == null) {
            val venues = venueRepository.findByLocation("Zagreb")
            val venueIds = venues.map { it.id }
            return buildVenueStats(
                venueIds,
                lowerBound,
                upperBound,
                venues
            )
        }

        val currentCity = geolocationService.getGeolocation(latitude, longitude)
        val nearbyCities = geolocationService.getNearbyCities(latitude, longitude)

        if (nearbyCities.isNullOrEmpty()) {
            val venues = venueRepository.findByLocation(currentCity)
            val venueIds = venues.map { it.id }
            return buildVenueStats(
                venueIds,
                lowerBound,
                upperBound,
                venues
            )
        }

        nearbyCities.add(currentCity)

        val venues = venueRepository.findByLocationIn(nearbyCities)
        val venueIds = venues.map { it.id }

        return buildVenueStats(venueIds, lowerBound, upperBound, venues)
    }

    @Transactional(readOnly = true)
    fun getNewVenues(): List<VenueEntity> {
        val currentTimestamp: LocalDateTime = LocalDateTime.now()
        val (lowerBound, upperBound) = getSurroundingHalfHours(currentTimestamp)

        val venues = venueRepository.findAll(PageRequest.of(0, 20, Sort.by(Sort.Order.desc("id")))).content
        if (venues.isEmpty()) return emptyList()
        val venueIds = venues.map { it.id }

        return buildVenueStats(venueIds, lowerBound, upperBound, venues)
    }

    @Transactional(readOnly = true)
    fun getTrendingVenues(): List<VenueEntity> {
        val currentTimestamp: LocalDateTime = LocalDateTime.now()
        val (lowerBound, upperBound) = getSurroundingHalfHours(currentTimestamp)

        val venues = venueRepository.findAll(PageRequest.of(0, 20, Sort.by(Sort.Order.desc("averageRating")))).content
        if (venues.isEmpty()) return emptyList()
        val venueIds = venues.map { it.id }

        return buildVenueStats(venueIds, lowerBound, upperBound, venues)
    }

    @Transactional(readOnly = true)
    fun getSuggestedVenues(): List<VenueEntity> {
        val currentTimestamp: LocalDateTime = LocalDateTime.now()
        val (lowerBound, upperBound) = getSurroundingHalfHours(currentTimestamp)

        val venues = venueRepository.findSuggestedVenues()
        if (venues.isEmpty()) return emptyList()
        val venueIds = venues.map { it.id }

        return buildVenueStats(venueIds, lowerBound, upperBound, venues)
    }

    @Transactional(readOnly = true)
    fun getType(typeId: Int): String =
        venueTypeRepository.getReferenceById(typeId).type

    @Transactional(readOnly = true)
    fun getVenueRating(venueId: Int): Double = venueRepository.findById(venueId)
        .orElseThrow { EntityNotFoundException("Venue with id: $venueId not found.") }.averageRating

    @Transactional(readOnly = true)
    fun getAllRatings(venueId: Int): List<VenueRatingEntity> =
        venueRatingRepository.findByVenueId(venueId).sortedByDescending { it.id }

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

        try {
            venueRepository.save(venue)
        } catch (e: Exception) {
            logger.error(e) { "Error while creating venue: ${e.message}" }
            return BasicResponse(false, "Error while creating venue. Please try again later.")
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

        try {
            venueRepository.save(venue)
        } catch (e: Exception) {
            logger.error { "Error while updating venue with id $venueId: ${e.message}" }
            return BasicResponse(false, "Error while updating venue. Please try again later.")
        }

        return BasicResponse(true, "Venue updated successfully.")
    }

    @Transactional
    fun rate(venueId: Int, userRating: Double, userId: Int, comment: String?): BasicResponse {
        if (userRating < 0.5 || userRating > 5.0) return BasicResponse(false, "Rating must be between 0.5 and 5.")

        val username = userRepository.findById(userId).getOrElse {
            logger.error { "User with id $userId not found." }
            return BasicResponse(false, "User with id $userId not found.")
        }.username

        val venue = venueRepository.findById(venueId)
            .orElseThrow { EntityNotFoundException("Venue with id $venueId not found") }
        val venueRating = venueRatingRepository.findByVenueId(venueId)

        val newRatingEntity = VenueRatingEntity().apply {
            this.venueId = venueId
            this.rating = userRating
            this.username = username
            this.comment = comment
        }

        try {
            venueRatingRepository.save(newRatingEntity)
        } catch (e: Exception) {
            logger.error { "Error while updating rating for venue with id $venueId: ${e.message}" }
            return BasicResponse(false, "Error while updating rating. Please try again later.")
        }

        val newAverageRating = calculateNewAverageRating(venueRating, userRating)
        val updatedVenue = venue.apply { averageRating = newAverageRating }

        try {
            venueRepository.save(updatedVenue)
        } catch (e: Exception) {
            logger.error { "Error while updating venue with id $venueId after rating: ${e.message}" }
            return BasicResponse(false, "Error while updating venue after rating. Please try again later.")
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
        try {
            venueRepository.deleteById(venueId)
        } catch (e: Exception) {
            logger.error { "Error while deleting venue with id $venueId: ${e.message}" }
            return BasicResponse(false, "Error while deleting venue. Please try again later.")
        }

        return BasicResponse(true, "Venue successfully deleted.")
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

    private fun buildVenueStats(
        venueIds: List<Int>,
        lowerBound: LocalDateTime,
        upperBound: LocalDateTime,
        venues: List<VenueEntity>
    ): List<VenueEntity> {
        val ratings = venueRatingRepository.findByVenueIdIn(venueIds)
        val reservationsByVenueId =
            reservationRepository.findByDatetimeBetween(lowerBound, upperBound).groupBy { it.venueId }
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

}
