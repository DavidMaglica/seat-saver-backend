package fipu.diplomski.dmaglica.user

import fipu.diplomski.dmaglica.exception.UserNotFoundException
import fipu.diplomski.dmaglica.repo.NotificationOptionsRepository
import fipu.diplomski.dmaglica.repo.RoleRepository
import fipu.diplomski.dmaglica.repo.UserRepository
import fipu.diplomski.dmaglica.repo.entity.NotificationOptionsEntity
import fipu.diplomski.dmaglica.repo.entity.RoleEntity
import fipu.diplomski.dmaglica.repo.entity.UserEntity
import fipu.diplomski.dmaglica.service.UserService
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles
import java.sql.SQLException

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
class UserEntityServiceTest {

    companion object {
        private const val USER_ID = 1L
        private const val USER_EMAIL = "test@mail.com"
        private const val USER_USERNAME = "testUser"
        private const val USER_PASSWORD = "password"
        private const val LAST_KNOWN_LATITUDE = 0.0
        private const val LAST_KNOWN_LONGITUDE = 0.0
    }

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var roleRepository: RoleRepository

    @Mock
    private lateinit var notificationOptionsRepository: NotificationOptionsRepository

    @InjectMocks
    private lateinit var userService: UserService

    @Test
    fun `should throw UserNotFoundException when user is not found`() {
        `when`(userRepository.findByEmail(anyString())).thenReturn(null)

        assertThrows<UserNotFoundException> {
            userService.login(anyString(), anyString())
        }

        verify(userRepository, times(1)).findByEmail(anyString())
        verifyNoInteractions(roleRepository)
        verifyNoInteractions(notificationOptionsRepository)
    }

    @Test
    fun `should throw SQLException when sql call throws`() {
        `when`(userRepository.findByEmail(anyString())).thenThrow(RuntimeException())

        assertThrows<SQLException> {
            userService.login(anyString(), anyString())
        }

        verify(userRepository, times(1)).findByEmail(anyString())
        verifyNoInteractions(roleRepository)
        verifyNoInteractions(notificationOptionsRepository)
    }

    @Test
    fun `when user is found correct user should be returned`() {
        `when`(userRepository.findByEmail(USER_EMAIL)).thenReturn(mockedUserEntity())
        `when`(roleRepository.getAllByUserId(USER_ID)).thenReturn(listOf(mockedRoleEntity()))
        `when`(notificationOptionsRepository.getByUserId(USER_ID)).thenReturn(mockedNotificationOptionsEntity())

        val result = userService.login(USER_EMAIL, USER_PASSWORD)

        verify(userRepository, times(1)).findByEmail(anyString())
        verify(roleRepository, times(1)).getAllByUserId(anyLong())
        verify(notificationOptionsRepository, times(1)).getByUserId(anyLong())

        result.success `should be equal to` true
        result.message `should be equal to` "User with email $USER_EMAIL successfully logged in"
    }

    private fun mockedNotificationOptionsEntity(): NotificationOptionsEntity = NotificationOptionsEntity().also {
        it.id = 1
        it.userId = 1
        it.locationServicesTurnedOn = true
        it.emailNotificationsTurnedOn = true
        it.pushNotificationsTurnedOn = true
    }

    private fun mockedRoleEntity(): RoleEntity = RoleEntity().also {
        it.id = 1
        it.userId = 1
        it.role = "USER"
    }

    private fun mockedUserEntity(): UserEntity = UserEntity().also {
        it.id = USER_ID
        it.email = USER_EMAIL
        it.username = USER_USERNAME
        it.password = USER_PASSWORD
        it.lastKnownLatitude = LAST_KNOWN_LATITUDE
        it.lastKnownLongitude = LAST_KNOWN_LONGITUDE
    }
}