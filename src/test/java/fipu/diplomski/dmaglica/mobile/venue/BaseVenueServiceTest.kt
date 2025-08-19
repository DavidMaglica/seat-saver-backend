package fipu.diplomski.dmaglica.mobile.venue

import fipu.diplomski.dmaglica.model.data.Role
import fipu.diplomski.dmaglica.repo.*
import fipu.diplomski.dmaglica.repo.entity.ReservationEntity
import fipu.diplomski.dmaglica.repo.entity.UserEntity
import fipu.diplomski.dmaglica.repo.entity.VenueEntity
import fipu.diplomski.dmaglica.repo.entity.VenueRatingEntity
import fipu.diplomski.dmaglica.service.GeolocationService
import fipu.diplomski.dmaglica.service.ImageService
import fipu.diplomski.dmaglica.service.VenueService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito.reset
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
abstract class BaseVenueServiceTest {

    @Mock
    protected lateinit var venueRepository: VenueRepository

    @Mock
    protected lateinit var venueRatingRepository: VenueRatingRepository

    @Mock
    protected lateinit var venueTypeRepository: VenueTypeRepository

    @Mock
    protected lateinit var reservationRepository: ReservationRepository

    @Mock
    protected lateinit var userRepository: UserRepository

    @Mock
    protected lateinit var geolocationService: GeolocationService

    @Mock
    protected lateinit var imageService: ImageService

    protected lateinit var venueService: VenueService

    @BeforeEach
    fun setUp() {
        venueService = VenueService(
            venueRepository,
            venueRatingRepository,
            venueTypeRepository,
            reservationRepository,
            imageService,
            geolocationService,
            userRepository,
        )
    }

    @AfterEach
    fun tearDown() {
        reset(
            venueRepository,
            venueRatingRepository,
            venueTypeRepository,
            reservationRepository,
            imageService,
            geolocationService,
            userRepository,
        )
    }

    protected val venueArgumentCaptor: ArgumentCaptor<VenueEntity> = ArgumentCaptor.forClass(VenueEntity::class.java)
    protected val venueRatingArgumentCaptor: ArgumentCaptor<VenueRatingEntity> =
        ArgumentCaptor.forClass(VenueRatingEntity::class.java)

    protected fun createVenue(
        id: Int = 1,
        name: String = "Test Venue",
        venueTypeId: Int = 1,
        location: String = "Test Location",
        workingHours: String = "9AM-5PM",
        description: String = "Test Description",
        maximumCapacity: Int = 100,
        availableCapacity: Int = 50,
        averageRating: Double = 0.0
    ): VenueEntity = VenueEntity().apply {
        this.id = id
        this.name = name
        this.venueTypeId = venueTypeId
        this.location = location
        this.workingHours = workingHours
        this.maximumCapacity = maximumCapacity
        this.availableCapacity = availableCapacity
        this.averageRating = averageRating
        this.description = description
    }

    protected fun createRating(id: Int = 1, venueId: Int, rating: Double): VenueRatingEntity =
        VenueRatingEntity().apply {
            this.id = id
            this.venueId = venueId
            this.rating = rating
        }

    protected fun createUser(id: Int = 1, name: String = "Test user", email: String = "test@email.com"): UserEntity =
        UserEntity().apply {
            this.id = id
            this.username = name
            this.email = email
            this.password = "password"
            this.lastKnownLatitude = 0.0
            this.lastKnownLongitude = 0.0
            this.roleId = Role.CUSTOMER.ordinal
        }

    protected fun createReservation(
        id: Int = 1,
        userId: Int = 1,
        venueId: Int = 1,
        datetime: LocalDateTime = LocalDateTime.now(),
        numberOfGuests: Int = 2
    ) = ReservationEntity().apply {
        this.id = id
        this.userId = userId
        this.venueId = venueId
        this.datetime = datetime
        this.numberOfGuests = numberOfGuests
    }

    protected fun getSurroundingHalfHours(time: LocalDateTime): Pair<LocalDateTime, LocalDateTime> {
        val minute = time.minute
        val second = time.second
        val nano = time.nano
        val truncated = time.minusSeconds(second.toLong()).minusNanos(nano.toLong())

        val previous = when {
            minute < 30 -> truncated.withMinute(0)
            else -> truncated.withMinute(30)
        }

        val next = previous.plusHours(1)

        return previous to next
    }

}