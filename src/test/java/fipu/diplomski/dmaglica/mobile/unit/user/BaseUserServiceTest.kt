package fipu.diplomski.dmaglica.mobile.unit.user

import fipu.diplomski.dmaglica.model.data.Role
import fipu.diplomski.dmaglica.repo.NotificationOptionsRepository
import fipu.diplomski.dmaglica.repo.UserRepository
import fipu.diplomski.dmaglica.repo.entity.NotificationOptionsEntity
import fipu.diplomski.dmaglica.repo.entity.UserEntity
import fipu.diplomski.dmaglica.service.UserService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito.reset
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.ActiveProfiles

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
abstract class BaseUserServiceTest {

    @Mock
    protected lateinit var userRepository: UserRepository

    @Mock
    protected lateinit var notificationOptionsRepository: NotificationOptionsRepository

    protected lateinit var userService: UserService

    @BeforeEach
    protected fun setUp() {
        userService = UserService(
            userRepository,
            notificationOptionsRepository,
        )
    }

    @AfterEach
    protected fun tearDown() {
        reset(userRepository, notificationOptionsRepository)
    }

    protected val userEntityArgumentCaptor: ArgumentCaptor<UserEntity> = ArgumentCaptor.forClass(UserEntity::class.java)
    protected val notificationOptionsArgumentCaptor: ArgumentCaptor<NotificationOptionsEntity> =
        ArgumentCaptor.forClass(NotificationOptionsEntity::class.java)

    protected val passwordEncoder = BCryptPasswordEncoder()

    protected val mockedUser: UserEntity = UserEntity().apply {
        id = 0
        email = "user1@mail.com"
        username = "user1"
        password = "password"
        lastKnownLatitude = 0.0
        lastKnownLongitude = 0.0
        roleId = Role.CUSTOMER.ordinal
    }

    protected val mockedUserWithEncryptedPassword = UserEntity().apply {
        id = mockedUser.id
        email = mockedUser.email
        username = mockedUser.username
        password = passwordEncoder.encode(mockedUser.password)
        lastKnownLatitude = mockedUser.lastKnownLatitude
        lastKnownLongitude = mockedUser.lastKnownLongitude
        roleId = mockedUser.roleId
    }

    protected val mockedNotificationOptions: NotificationOptionsEntity = NotificationOptionsEntity().apply {
        userId = 0
        locationServicesEnabled = false
        pushNotificationsEnabled = false
        emailNotificationsEnabled = false
    }
}
