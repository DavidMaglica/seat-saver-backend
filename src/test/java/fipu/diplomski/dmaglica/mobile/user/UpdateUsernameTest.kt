package fipu.diplomski.dmaglica.mobile.user

import fipu.diplomski.dmaglica.exception.UserNotFoundException
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles
import java.sql.SQLException

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
class UpdateUsernameTest : BaseUserServiceTest() {
    companion object {
        const val NEW_USERNAME = "newTestUsername"
    }

    @Test
    fun `should throw if user not found`() {
        `when`(userRepository.findByEmail(anyString())).thenReturn(null)

        val exception =
            assertThrows<UserNotFoundException> { userService.updateUsername(mockedUser.email, NEW_USERNAME) }

        exception.message `should be equal to` "User with email ${mockedUser.email} does not exist"
    }

    @Test
    fun `should throw if can't update username`() {
        `when`(userRepository.findByEmail(anyString())).thenReturn(mockedUser)
        `when`(userRepository.save(any())).thenThrow(RuntimeException())

        val exception = assertThrows<SQLException> { userService.updateUsername(mockedUser.email, NEW_USERNAME) }

        exception.message `should be equal to` "Error while updating username for user with email ${mockedUser.email}"

        verify(userRepository, times(1)).findByEmail(mockedUser.email)
    }

    @Test
    fun `should update username`() {
        `when`(userRepository.findByEmail(anyString())).thenReturn(mockedUser)

        val result = userService.updateUsername(mockedUser.email, NEW_USERNAME)

        result.success `should be` true
        result.message `should be equal to` "Username for user with email ${mockedUser.email} successfully updated"

        verify(userRepository).save(userEntityArgumentCaptor.capture())
        val updatedUser = userEntityArgumentCaptor.value
        updatedUser.username `should be equal to` NEW_USERNAME

        verify(userRepository, times(1)).findByEmail(mockedUser.email)
        verify(userRepository, times(1)).save(any())
    }
}
