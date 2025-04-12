package fipu.diplomski.dmaglica.user

import fipu.diplomski.dmaglica.exception.UserNotFoundException
import org.amshove.kluent.`should be`
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
class UpdatePasswordTest : AbstractUserServiceTest() {

    companion object {
        const val NEW_PASSWORD = "password2"
    }

    @Test
    fun `should throw if user not found`() {
        `when`(userRepository.findByEmail(anyString())).thenReturn(null)

        assertThrows<UserNotFoundException> { userService.updatePassword(USER_EMAIL, NEW_PASSWORD) }
    }

    @Test
    fun `should throw if can't update password`() {
        `when`(userRepository.findByEmail(anyString())).thenReturn(mockedUser)
        `when`(userRepository.save(any())).thenThrow(RuntimeException())

        assertThrows<SQLException> { userService.updatePassword(USER_EMAIL, NEW_PASSWORD) }

        verify(userRepository, times(1)).findByEmail(USER_EMAIL)
    }

    @Test
    fun `should update password`() {
        `when`(userRepository.findByEmail(anyString())).thenReturn(mockedUser)

        val result = userService.updatePassword(USER_EMAIL, NEW_PASSWORD)

        result.success `should be` true
        result.message `should be equal to` "Password for user with email $USER_EMAIL successfully updated"

        verify(userRepository, times(1)).findByEmail(USER_EMAIL)
        verify(userRepository, times(1)).save(any())
    }
}
