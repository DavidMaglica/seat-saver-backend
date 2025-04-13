package fipu.diplomski.dmaglica.user

import fipu.diplomski.dmaglica.exception.UserNotFoundException
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
class DeleteUserTest : UserServiceTest() {

    @Test
    fun `should throw if user not found`() {
        `when`(userRepository.findByEmail(mockedUser.email)).thenReturn(null)

        assertThrows<UserNotFoundException> { userService.delete(mockedUser.email) }
    }

    @Test
    fun `should throw if user not deleted`() {
        `when`(userRepository.findByEmail(anyString())).thenReturn(mockedUser)
        `when`(userRepository.deleteById(mockedUser.id)).thenThrow(RuntimeException("Error while deleting user"))

        val exception = assertThrows<SQLException> { userService.delete(mockedUser.email) }

        exception.message `should be equal to` "Error while deleting user with email ${mockedUser.email}"
    }

    @Test
    fun `should delete user`() {
        `when`(userRepository.findByEmail(anyString())).thenReturn(mockedUser)

        val result = userService.delete(mockedUser.email)

        result.success `should be equal to` true
        result.message `should be equal to` "User with email ${mockedUser.email} successfully deleted"

        verify(userRepository, times(1)).findByEmail(mockedUser.email)
        verify(userRepository, times(1)).deleteById(mockedUser.id)
    }
}
