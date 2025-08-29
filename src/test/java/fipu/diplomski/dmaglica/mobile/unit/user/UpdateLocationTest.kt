package fipu.diplomski.dmaglica.mobile.unit.user

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
class UpdateLocationTest : BaseUserServiceTest() {

    companion object {
        const val NEW_LATITUDE = 0.0
        const val NEW_LONGITUDE = 0.0
    }

    @Test
    fun `should return failure response when user not found`() {
        `when`(userRepository.findById(anyInt())).thenReturn(Optional.empty())

        val response = userService.updateLocation(mockedUser.id, NEW_LATITUDE, NEW_LONGITUDE)

        response.success `should be` false
        response.message `should be equal to` "User not found."

        verify(userRepository, times(1)).findById(mockedUser.id)
        verifyNoMoreInteractions(userRepository)
    }

    @Test
    fun `should return failure response when updating user location throws exception`() {
        `when`(userRepository.findById(anyInt())).thenReturn(Optional.of(mockedUser))
        `when`(userRepository.save(any())).thenThrow(RuntimeException())

        val response = userService.updateLocation(mockedUser.id, NEW_LATITUDE, NEW_LONGITUDE)

        response.success `should be` false
        response.message `should be equal to` "Error while updating location. Please try again later."

        verify(userRepository, times(1)).findById(mockedUser.id)
    }

    @Test
    fun `should update location`() {
        `when`(userRepository.findById(anyInt())).thenReturn(Optional.of(mockedUser))

        val response = userService.updateLocation(mockedUser.id, NEW_LATITUDE, NEW_LONGITUDE)

        response.success `should be` true
        response.message `should be equal to` "Location successfully updated."

        verify(userRepository).save(userEntityArgumentCaptor.capture())
        val updatedUser = userEntityArgumentCaptor.value
        updatedUser.lastKnownLatitude `should be equal to` NEW_LATITUDE
        updatedUser.lastKnownLongitude `should be equal to` NEW_LONGITUDE

        verify(userRepository, times(1)).findById(mockedUser.id)
        verify(userRepository, times(1)).save(any())
    }
}
