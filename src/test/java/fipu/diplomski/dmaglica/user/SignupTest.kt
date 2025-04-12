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
class SignupTest : AbstractUserServiceTest() {

    @Test
    fun `should return early if user already exists`() {
        `when`(userRepository.getByEmail(anyString())).thenReturn(mockedUser)

        val response = userService.signup(USER_EMAIL, USER_USERNAME, USER_PASSWORD)

        response.success `should be equal to` false
        response.message `should be equal to` "User with email $USER_EMAIL already exists"

        verify(userRepository, times(1)).getByEmail(USER_EMAIL)
        verifyNoInteractions(notificationOptionsRepository)
    }

    @Test
    fun `should throw if user not saved`() {
        `when`(userRepository.getByEmail(anyString())).thenReturn(null)
        `when`(userRepository.save(any())).thenThrow(RuntimeException("Error while saving user"))

        assertThrows<SQLException> { userService.signup(USER_EMAIL, USER_USERNAME, USER_PASSWORD) }

        verify(userRepository, times(1)).getByEmail(USER_EMAIL)
        verify(userRepository, times(1)).save(any())
        verifyNoInteractions(notificationOptionsRepository)
    }

    @Test
    fun `should throw if notification options not saved`() {
        `when`(userRepository.getByEmail(anyString())).thenReturn(null)
        `when`(userRepository.save(any())).thenReturn(mockedUser)
        `when`(notificationOptionsRepository.save(any())).thenThrow(RuntimeException("Error while saving notification options"))

        assertThrows<SQLException> { userService.signup(USER_EMAIL, USER_USERNAME, USER_PASSWORD) }

        verify(userRepository, times(1)).getByEmail(USER_EMAIL)
        verify(userRepository, times(1)).save(any())
        verify(notificationOptionsRepository, times(1)).save(any())
    }

    @Test
    fun `should save user and notification options`() {
        `when`(userRepository.getByEmail(anyString())).thenReturn(null)
        `when`(userRepository.save(any())).thenReturn(mockedUser)
        `when`(notificationOptionsRepository.save(any())).thenReturn(mockedNotificationOptions)

        val response = userService.signup(USER_EMAIL, USER_USERNAME, USER_PASSWORD)

        response.success `should be equal to` true
        response.message `should be equal to` "User with email $USER_EMAIL successfully created"

        verify(userRepository, times(1)).getByEmail(USER_EMAIL)
        verify(userRepository, times(1)).save(any())
        verify(notificationOptionsRepository, times(1)).save(any())
    }
}
