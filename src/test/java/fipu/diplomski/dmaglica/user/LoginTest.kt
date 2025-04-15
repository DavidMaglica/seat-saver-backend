package fipu.diplomski.dmaglica.user

import org.amshove.kluent.`should be`
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
class LoginTest : BaseUserServiceTest() {

    companion object {
        const val WRONG_PASSWORD = "wrongPassword"
        const val WRONG_EMAIL = "wrongEmail@email.com"
    }

    @Test
    fun `should return early when user not found`() {
        `when`(userRepository.findByEmail(anyString())).thenReturn(null)

        val result = userService.login(WRONG_EMAIL, mockedUser.password)

        result.success `should be` false
        result.message `should be equal to` "User with email $WRONG_EMAIL does not exist"

        verify(userRepository, times(1)).findByEmail(anyString())
    }

    @Test
    fun `should return early if wrong password`() {
        `when`(userRepository.findByEmail(anyString())).thenReturn(mockedUser)

        val result = userService.login(mockedUser.email, WRONG_PASSWORD)

        result.success `should be` false
        result.message `should be equal to` "Incorrect password"

        verify(userRepository, times(1)).findByEmail(anyString())
    }

    @Test
    fun `when user is found and password is correct user should be returned`() {
        `when`(userRepository.findByEmail(anyString())).thenReturn(mockedUser)

        val result = userService.login(mockedUser.email, mockedUser.password)

        result.success `should be` true
        result.message `should be equal to` "User with email ${mockedUser.email} successfully logged in"

        verify(userRepository, times(1)).findByEmail(anyString())
    }
}