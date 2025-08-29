package fipu.diplomski.dmaglica.mobile.integration.venue

import fipu.diplomski.dmaglica.model.data.Role
import fipu.diplomski.dmaglica.repo.*
import fipu.diplomski.dmaglica.repo.entity.*
import fipu.diplomski.dmaglica.service.ReservationService
import fipu.diplomski.dmaglica.service.UserService
import fipu.diplomski.dmaglica.service.VenueService
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import java.time.LocalDateTime

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = ["rapidapi.key=test_key"])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AbstractVenueIntegrationTest {

    @Autowired
    protected lateinit var reservationService: ReservationService

    @Autowired
    protected lateinit var userService: UserService

    @Autowired
    protected lateinit var venueService: VenueService

    @Autowired
    protected lateinit var menuImageRepository: MenuImageRepository

    @Autowired
    protected lateinit var reservationRepository: ReservationRepository

    @Autowired
    protected lateinit var userRepository: UserRepository

    @Autowired
    protected lateinit var venueImageRepository: VenueImageRepository

    @Autowired
    protected lateinit var venueRatingRepository: VenueRatingRepository

    @Autowired
    protected lateinit var venueRepository: VenueRepository

    @Autowired
    protected lateinit var workingDaysRepository: WorkingDaysRepository

    protected val owner = createOwner()
    protected val customer = createCustomer()
    protected val venue = createVenue(ownerId = owner.id)

    @BeforeAll
    fun setupData() {
        userRepository.saveAllAndFlush(listOf(customer, owner))
        venueRepository.saveAndFlush(venue)
    }

    protected fun createVenue(
        ownerId: Int = 1,
        name: String = "Test Venue",
        venueTypeId: Int = 1,
        location: String = "Test Location",
        workingHours: String = "9:00 - 17:00",
        description: String = "Test Description",
        maximumCapacity: Int = 100,
        availableCapacity: Int = 100,
        averageRating: Double = 0.0
    ): VenueEntity = VenueEntity().apply {
        this.ownerId = ownerId
        this.name = name
        this.venueTypeId = venueTypeId
        this.location = location
        this.workingHours = workingHours
        this.maximumCapacity = maximumCapacity
        this.availableCapacity = availableCapacity
        this.averageRating = averageRating
        this.description = description
    }

    protected fun createWorkingDays(venueId: Int, daysOfTheWeek: List<Int>): List<WorkingDaysEntity> =
        daysOfTheWeek.map { dayOfTheWeek ->
            WorkingDaysEntity().apply {
                this.venueId = venueId
                this.dayOfWeek = dayOfTheWeek
            }
        }

    protected fun createRating(venueId: Int, rating: Double): VenueRatingEntity =
        VenueRatingEntity().apply {
            this.venueId = venueId
            this.rating = rating
        }

    protected fun createCustomer(name: String = "Test user", email: String = "user@email.com"): UserEntity =
        UserEntity().apply {
            this.username = name
            this.email = email
            this.password = "password"
            this.lastKnownLatitude = 0.0
            this.lastKnownLongitude = 0.0
            this.roleId = Role.CUSTOMER.ordinal
        }

    protected fun createOwner(name: String = "Test owner", email: String = "owner@email.com"): UserEntity =
        UserEntity().apply {
            this.username = name
            this.email = email
            this.password = "password"
            this.lastKnownLatitude = 0.0
            this.lastKnownLongitude = 0.0
            this.roleId = Role.OWNER.ordinal
        }

    protected fun createReservation(
        userId: Int = 1,
        venueId: Int = 1,
        datetime: LocalDateTime = LocalDateTime.now(),
        numberOfGuests: Int = 2
    ) = ReservationEntity().apply {
        this.userId = userId
        this.venueId = venueId
        this.datetime = datetime
        this.numberOfGuests = numberOfGuests
    }
}