package fipu.diplomski.dmaglica.mobile.user

import org.amshove.kluent.`should be`
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles
import java.util.*

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
class DeleteUserTest : BaseUserServiceTest() {

    @Test
    fun `should return failure response if user not found`() {
        `when`(userRepository.findById(anyInt())).thenReturn(Optional.empty())

        val result = userService.delete(mockedUser.id)

        result.success `should be` false
        result.message `should be equal to` "User not found"

        verify(userRepository, times(1)).findById(mockedUser.id)
    }

    @Test
    fun `should return failure response if user not deleted`() {
        `when`(userRepository.findById(anyInt())).thenReturn(Optional.of(mockedUser))
        `when`(userRepository.deleteById(mockedUser.id)).thenThrow(RuntimeException("Error while deleting user"))

        val result = userService.delete(mockedUser.id)

        result.success `should be` false
        result.message `should be equal to` "Error while deleting user. Please try again later."

        verify(userRepository, times(1)).findById(mockedUser.id)
    }

    @Test
    fun `should delete user`() {
        `when`(userRepository.findById(anyInt())).thenReturn(Optional.of(mockedUser))

        val result = userService.delete(mockedUser.id)

        result.success `should be equal to` true
        result.message `should be equal to` "User successfully deleted."

        verify(userRepository, times(1)).findById(mockedUser.id)
        verify(userRepository, times(1)).deleteById(mockedUser.id)
    }
}
