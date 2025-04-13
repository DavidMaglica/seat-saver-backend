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
class UpdateEmailTest : UserServiceTest() {

    companion object {
        const val NEW_EMAIL = "test@test.com"
        const val OLD_EMAIL = "user1@mail.com"
    }

    @Test
    fun `should throw if user not found`() {
        `when`(userRepository.findByEmail(anyString())).thenReturn(null)

        assertThrows<UserNotFoundException> { userService.updateEmail(OLD_EMAIL, NEW_EMAIL) }
    }

    @Test
    fun `should throw if can't update email`() {
        `when`(userRepository.findByEmail(anyString())).thenReturn(mockedUser)
        `when`(userRepository.save(any())).thenThrow(RuntimeException())

        assertThrows<SQLException> { userService.updateEmail(OLD_EMAIL, NEW_EMAIL) }

        verify(userRepository, times(1)).findByEmail(anyString())
    }

    @Test
    fun `should update email`() {
        `when`(userRepository.findByEmail(anyString())).thenReturn(mockedUser)

        val result = userService.updateEmail(OLD_EMAIL, NEW_EMAIL)

        result.success `should be` true
        result.message `should be equal to` "Email for user with email $OLD_EMAIL updated to $NEW_EMAIL"

        verify(userRepository, times(1)).findByEmail(OLD_EMAIL)
        verify(userRepository, times(1)).save(any())
    }
}
