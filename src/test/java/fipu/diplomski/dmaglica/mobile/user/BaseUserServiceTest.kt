package fipu.diplomski.dmaglica.mobile.user

import fipu.diplomski.dmaglica.model.data.Role
import fipu.diplomski.dmaglica.repo.NotificationOptionsRepository
import fipu.diplomski.dmaglica.repo.UserRepository
import fipu.diplomski.dmaglica.repo.entity.NotificationOptionsEntity
import fipu.diplomski.dmaglica.repo.entity.UserEntity
import fipu.diplomski.dmaglica.service.UserService
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
abstract class BaseUserServiceTest {

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

    protected val userEntityArgumentCaptor: ArgumentCaptor<UserEntity> = ArgumentCaptor.forClass(UserEntity::class.java)
    protected val notificationOptionsArgumentCaptor: ArgumentCaptor<NotificationOptionsEntity> =
        ArgumentCaptor.forClass(NotificationOptionsEntity::class.java)

    protected val mockedUser: UserEntity = UserEntity().also {
        it.id = 0
        it.email = "user1@mail.com"
        it.username = "user1"
        it.password = "password"
        it.lastKnownLatitude = 0.0
        it.lastKnownLongitude = 0.0
        it.roleId = Role.USER.ordinal
    }

    protected val mockedNotificationOptions: NotificationOptionsEntity = NotificationOptionsEntity().also {
        it.userId = 0
        it.locationServicesEnabled = false
        it.pushNotificationsEnabled = false
        it.emailNotificationsEnabled = false
    }
}
