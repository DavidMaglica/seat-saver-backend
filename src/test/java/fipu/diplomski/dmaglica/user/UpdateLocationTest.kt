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
class UpdateLocationTest : BaseUserServiceTest() {

    companion object {
        const val NEW_LATITUDE = 0.0
        const val NEW_LONGITUDE = 0.0
    }

    @Test
    fun `should throw if user not found`() {
        `when`(userRepository.findByEmail(anyString())).thenReturn(null)

        val exception = assertThrows<UserNotFoundException> {
            userService.updateLocation(
                mockedUser.email,
                NEW_LATITUDE,
                NEW_LONGITUDE
            )
        }

        exception.message `should be equal to` "User with email ${mockedUser.email} does not exist"
    }

    @Test
    fun `should throw if can't update location`() {
        `when`(userRepository.findByEmail(anyString())).thenReturn(mockedUser)
        `when`(userRepository.save(any())).thenThrow(RuntimeException())

        val exception =
            assertThrows<SQLException> { userService.updateLocation(mockedUser.email, NEW_LATITUDE, NEW_LONGITUDE) }

        exception.message `should be equal to` "Error while updating location for user with email ${mockedUser.email}"

        verify(userRepository, times(1)).findByEmail(mockedUser.email)
    }

    @Test
    fun `should update location`() {
        `when`(userRepository.findByEmail(anyString())).thenReturn(mockedUser)

        val result = userService.updateLocation(mockedUser.email, NEW_LATITUDE, NEW_LONGITUDE)

        result.success `should be` true
        result.message `should be equal to` "Location for user with email ${mockedUser.email} successfully updated"

        verify(userRepository).save(userEntityArgumentCaptor.capture())
        val updatedUser = userEntityArgumentCaptor.value
        updatedUser.lastKnownLatitude `should be equal to` NEW_LATITUDE
        updatedUser.lastKnownLongitude `should be equal to` NEW_LONGITUDE

        verify(userRepository, times(1)).findByEmail(mockedUser.email)
        verify(userRepository, times(1)).save(any())
    }
}
