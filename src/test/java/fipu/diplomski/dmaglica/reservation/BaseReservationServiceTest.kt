package fipu.diplomski.dmaglica.reservation

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

    protected val mockedUser: UserEntity = UserEntity().also {
        it.id = 1
        it.email = "user1@mail.com"
        it.username = "user1"
        it.password = "password"
        it.lastKnownLatitude = 0.0
        it.lastKnownLongitude = 0.0
        it.roleId = Role.USER.ordinal
    }

    protected val mockedVenue = VenueEntity().also {
        it.id = 1
        it.name = "Test Venue"
        it.location = "Test Location"
        it.description = "Test Description"
        it.workingHours = "9 AM - 5 PM"
        it.averageRating = 0.0
        it.venueTypeId = 1
    }

    protected val mockedReservation = ReservationEntity().also {
        it.id = 1
        it.userId = 1
        it.venueId = 1
        it.datetime = "02-08-2025 10:00"
        it.numberOfGuests = 2
    }
}
