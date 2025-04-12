package fipu.diplomski.dmaglica.user

import fipu.diplomski.dmaglica.exception.UserNotFoundException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
class DeleteUserTest : AbstractUserServiceTest() {

    @Test
    fun `should throw if user not found`() {
        `when`(userRepository.getByEmail(USER_EMAIL)).thenThrow(RuntimeException("Error while getting user"))

        assertThrows<UserNotFoundException> { userService.delete(USER_EMAIL) }
    }

    @Test
    fun `should throw if user not deleted`() {
        `when`(userRepository.getByEmail(USER_EMAIL)).thenReturn(mockedUser)
        `when`(userRepository.deleteById(mockedUser.id)).thenThrow(RuntimeException("Error while deleting user"))
    }

    @Test
    fun `should delete user`() {

    }
}
