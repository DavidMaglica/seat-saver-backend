package fipu.diplomski.dmaglica.user

import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles
import java.sql.SQLException

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
class SignupTest : BaseUserServiceTest() {

    @Test
    fun `should return early if user already exists`() {
        `when`(userRepository.findByEmail(anyString())).thenReturn(mockedUser)

        val response = userService.signup(mockedUser.email, mockedUser.username, mockedUser.password)

        response.success `should be equal to` false
        response.message `should be equal to` "User with email ${mockedUser.email} already exists"

        verify(userRepository, times(1)).findByEmail(mockedUser.email)
        verifyNoInteractions(notificationOptionsRepository)
    }

    @Test
    fun `should throw if user not saved`() {
        `when`(userRepository.findByEmail(anyString())).thenReturn(null)
        `when`(userRepository.save(any())).thenThrow(RuntimeException("Error while saving user"))

        val exception = assertThrows<SQLException> {
            userService.signup(
                mockedUser.email,
                mockedUser.username,
                mockedUser.password
            )
        }

        exception.message `should be equal to` "Error while saving user with email ${mockedUser.email}"

        verify(userRepository, times(1)).findByEmail(mockedUser.email)
        verify(userRepository, times(1)).save(any())
        verifyNoInteractions(notificationOptionsRepository)
    }

    @Test
    fun `should throw if notification options not saved`() {
        `when`(userRepository.findByEmail(anyString())).thenReturn(null)
        `when`(userRepository.save(any())).thenReturn(mockedUser)
        `when`(notificationOptionsRepository.save(any())).thenThrow(RuntimeException("Error while saving notification options"))

        val exception = assertThrows<SQLException> {
            userService.signup(
                mockedUser.email,
                mockedUser.username,
                mockedUser.password
            )
        }

        exception.message `should be equal to` "Error while saving user with email ${mockedUser.email}"

        verify(userRepository, times(1)).findByEmail(mockedUser.email)
        verify(userRepository, times(1)).save(any())
        verify(notificationOptionsRepository, times(1)).save(any())
    }

    @Test
    fun `should save user and notification options`() {
        `when`(userRepository.findByEmail(anyString())).thenReturn(null)
        `when`(userRepository.save(any())).thenReturn(mockedUser)
        `when`(notificationOptionsRepository.save(any())).thenReturn(mockedNotificationOptions)

        val response = userService.signup(mockedUser.email, mockedUser.username, mockedUser.password)

        response.success `should be equal to` true
        response.message `should be equal to` "User with email ${mockedUser.email} successfully created"

        verify(userRepository).save(userEntityArgumentCaptor.capture())
        verify(notificationOptionsRepository).save(notificationOptionsArgumentCaptor.capture())
        val newUser = userEntityArgumentCaptor.value
        val newNotificationOptions = notificationOptionsArgumentCaptor.value

        newUser.email `should be equal to` mockedUser.email
        newUser.username `should be equal to` mockedUser.username
        newUser.password `should be equal to` mockedUser.password
        newUser.roleId `should be equal to` mockedUser.roleId
        newNotificationOptions.userId `should be equal to` mockedUser.id
        newNotificationOptions.emailNotificationsEnabled `should be equal to` mockedNotificationOptions.emailNotificationsEnabled
        newNotificationOptions.pushNotificationsEnabled `should be equal to` mockedNotificationOptions.pushNotificationsEnabled
        newNotificationOptions.locationServicesEnabled `should be equal to` mockedNotificationOptions.locationServicesEnabled

        verify(userRepository, times(1)).findByEmail(mockedUser.email)
        verify(userRepository, times(1)).save(any())
        verify(notificationOptionsRepository, times(1)).save(any())
    }
}
