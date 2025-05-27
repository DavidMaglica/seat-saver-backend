package fipu.diplomski.dmaglica.mobile.reservation

import fipu.diplomski.dmaglica.model.data.Role
import fipu.diplomski.dmaglica.repo.ReservationRepository
import fipu.diplomski.dmaglica.repo.UserRepository
import fipu.diplomski.dmaglica.repo.VenueRepository
import fipu.diplomski.dmaglica.repo.entity.ReservationEntity
import fipu.diplomski.dmaglica.repo.entity.UserEntity
import fipu.diplomski.dmaglica.repo.entity.VenueEntity
import fipu.diplomski.dmaglica.service.ReservationService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.reset
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
abstract class BaseReservationServiceTest {

    @Mock
    protected lateinit var reservationRepository: ReservationRepository

    @Mock
    protected lateinit var userRepository: UserRepository

    @Mock
    protected lateinit var venueRepository: VenueRepository

    @InjectMocks
    protected lateinit var reservationService: ReservationService

    @AfterEach
    protected fun tearDown() {
        reset(reservationRepository, userRepository)
    }

    protected val reservationArgumentCaptor: ArgumentCaptor<ReservationEntity> =
        ArgumentCaptor.forClass(ReservationEntity::class.java)

    protected val venueArgumentCaptor: ArgumentCaptor<VenueEntity> =
        ArgumentCaptor.forClass(VenueEntity::class.java)

    protected val mockedUser: UserEntity = UserEntity().apply {
        id = 1
        email = "user1@mail.com"
        username = "user1"
        password = "password"
        lastKnownLatitude = 0.0
        lastKnownLongitude = 0.0
        roleId = Role.USER.ordinal
    }

    protected val mockedVenue = VenueEntity().apply {
        id = 1
        name = "Test Venue"
        location = "Test Location"
        description = "Test Description"
        workingHours = "9 AM - 5 PM"
        maximumCapacity = 100
        availableCapacity = 100
        averageRating = 0.0
        venueTypeId = 1
    }

    protected val mockedReservation = ReservationEntity().apply {
        id = 1
        userId = 1
        venueId = 1
        datetime = "02-08-2025 10:00"
        numberOfGuests = 2
    }
}
