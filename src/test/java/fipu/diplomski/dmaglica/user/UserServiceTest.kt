package fipu.diplomski.dmaglica.user

import fipu.diplomski.dmaglica.model.Role
import fipu.diplomski.dmaglica.repo.NotificationOptionsRepository
import fipu.diplomski.dmaglica.repo.UserRepository
import fipu.diplomski.dmaglica.repo.entity.NotificationOptionsEntity
import fipu.diplomski.dmaglica.repo.entity.UserEntity
import fipu.diplomski.dmaglica.service.UserService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.reset
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
abstract class UserServiceTest {

    @Mock
    protected lateinit var userRepository: UserRepository

    @Mock
    protected lateinit var notificationOptionsRepository: NotificationOptionsRepository

    @InjectMocks
    protected lateinit var userService: UserService

    @AfterEach
    protected fun tearDown() {
        reset(userRepository, notificationOptionsRepository)
    }

    protected val mockedUser: UserEntity = UserEntity().also {
        it.id = 1
        it.email = "user1@mail.com"
        it.username = "user1"
        it.password = "password"
        it.lastKnownLatitude = 0.0
        it.lastKnownLongitude = 0.0
        it.roleId = Role.USER.ordinal
    }

    protected val mockedNotificationOptions: NotificationOptionsEntity = NotificationOptionsEntity().also {
        it.userId = 1
        it.locationServicesTurnedOn = false
        it.pushNotificationsTurnedOn = false
        it.emailNotificationsTurnedOn = false
    }
}
