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
class UpdatePasswordTest : BaseUserServiceTest() {

    companion object {
        const val NEW_PASSWORD = "password2"
    }

    @Test
    fun `should return failure response if new password is empty`() {
        val result = userService.updatePassword(mockedUser.id, "")

        result.success `should be` false
        result.message `should be equal to` "Password cannot be empty."

        verifyNoInteractions(userRepository)
    }

    @Test
    fun `should return failure response if user not found`() {
        `when`(userRepository.findById(anyInt())).thenReturn(Optional.empty())

        val result = userService.updatePassword(mockedUser.id, NEW_PASSWORD)

        result.success `should be` false
        result.message `should be equal to` "User not found."

        verify(userRepository, times(1)).findById(mockedUser.id)
        verifyNoMoreInteractions(userRepository)
    }

    @Test
    fun `should return failure response if can't update password`() {
        `when`(userRepository.findById(anyInt())).thenReturn(Optional.of(mockedUser))
        `when`(userRepository.save(any())).thenThrow(RuntimeException())

        val result = userService.updatePassword(mockedUser.id, NEW_PASSWORD)

        result.success `should be` false
        result.message `should be equal to` "Error while updating password. Please try again later."

        verify(userRepository, times(1)).findById(mockedUser.id)
        verify(userRepository, times(1)).save(any())
        verifyNoMoreInteractions(userRepository)
    }

    @Test
    fun `should update password`() {
        `when`(userRepository.findById(anyInt())).thenReturn(Optional.of(mockedUser))

        val result = userService.updatePassword(
            mockedUser.id,
            NEW_PASSWORD
        )

        result.success `should be` true
        result.message `should be equal to` "Password successfully updated."

        verify(userRepository).save(userEntityArgumentCaptor.capture())
        val updatedUser = userEntityArgumentCaptor.value
        updatedUser.password `should be equal to` NEW_PASSWORD

        verify(userRepository, times(1)).findById(mockedUser.id)
        verify(userRepository, times(1)).save(any())
        verifyNoMoreInteractions(userRepository)
    }
}
