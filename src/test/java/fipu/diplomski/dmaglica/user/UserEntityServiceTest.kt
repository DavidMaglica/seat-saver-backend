package fipu.diplomski.dmaglica.user

import fipu.diplomski.dmaglica.repo.NotificationOptionsRepository
import fipu.diplomski.dmaglica.repo.RoleRepository
import fipu.diplomski.dmaglica.repo.UserRepository
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
abstract class UserEntityServiceTest {
    companion object {
        const val USER_ID = 1L
        const val USER_EMAIL = "user1@mail.com"
        const val USER_USERNAME = "user1"
        const val USER_PASSWORD = "password"
        const val LAST_KNOWN_LATITUDE = 0.0
        const val LAST_KNOWN_LONGITUDE = 0.0
    }

    @Mock
    lateinit var userRepository: UserRepository

    @Mock
    lateinit var roleRepository: RoleRepository

    @Mock
    lateinit var notificationOptionsRepository: NotificationOptionsRepository

    @InjectMocks
    lateinit var userService: UserService

    @AfterEach
    fun tearDown() {
        reset(userRepository, roleRepository, notificationOptionsRepository)
    }
}