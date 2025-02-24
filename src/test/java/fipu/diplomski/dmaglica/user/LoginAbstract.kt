package fipu.diplomski.dmaglica.user

import fipu.diplomski.dmaglica.repo.entity.UserEntity
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles
import java.sql.SQLException

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
class LoginAbstract : AbstractUserServiceTest() {

    @Test
    fun `should throw UserNotFoundException when user is not found`() {
        `when`(userRepository.getByEmail(anyString())).thenReturn(null)

        val response = userService.login("wrongEmail@email.com", "wrongPassword")

        verify(userRepository, times(1)).getByEmail(anyString())

        response.success `should be equal to` false
        response.message `should be equal to` "User with email wrongEmail@email.com does not exist"
    }

    @Disabled
    @Test
    fun `should throw SQLException when sql call throws`() {
        `when`(userRepository.getByEmail(anyString())).thenThrow(RuntimeException())

        assertThrows<SQLException> {
            userService.login(anyString(), anyString())
        }

        verify(userRepository, times(1)).getByEmail(anyString())
    }

    @Test
    fun `when user is found correct user should be returned`() {
        `when`(userRepository.getByEmail(USER_EMAIL)).thenReturn(mockedUserEntity())

        val result = userService.login(USER_EMAIL, USER_PASSWORD)

        verify(userRepository, times(1)).getByEmail(anyString())

        result.success `should be equal to` true
        result.message `should be equal to` "User with email $USER_EMAIL successfully logged in"
    }

    private fun mockedUserEntity(): UserEntity = UserEntity().also {
        it.id = USER_ID
        it.email = USER_EMAIL
        it.username = USER_USERNAME
        it.password = USER_PASSWORD
        it.lastKnownLatitude = LAST_KNOWN_LATITUDE
        it.lastKnownLongitude = LAST_KNOWN_LONGITUDE
        it.roleId = USER_ROLE.ordinal
    }
}