package fipu.diplomski.dmaglica.mobile.venue

import fipu.diplomski.dmaglica.model.data.Role
import fipu.diplomski.dmaglica.repo.*
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
        maximumCapacity: Int = 100
    ): VenueEntity = VenueEntity().apply {
        this.id = id
        this.name = name
        this.venueTypeId = venueTypeId
        this.location = location
        this.workingHours = workingHours
        this.maximumCapacity = maximumCapacity
    }

    protected val mockedVenue = VenueEntity().apply {
        id = 1
        name = "Test Venue"
        location = "Test Location"
        description = "Test Description"
        workingHours = "9 AM - 5 PM"
        maximumCapacity = 100
        availableCapacity = 50
        averageRating = 0.0
        venueTypeId = 1
    }

    protected val mockedRating = VenueRatingEntity().apply {
        id = 1
        venueId = mockedVenue.id
        rating = 4.0
    }

    protected val mockedUser: UserEntity = UserEntity().apply {
        id = 1
        email = "user1@mail.com"
        username = "user1"
        password = "password"
        lastKnownLatitude = 0.0
        lastKnownLongitude = 0.0
        roleId = Role.USER.ordinal
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

        val next = when {
            minute < 30 -> truncated.withMinute(30)
            else -> truncated.plusHours(1).withMinute(0)
        }

        return previous to next
    }

}