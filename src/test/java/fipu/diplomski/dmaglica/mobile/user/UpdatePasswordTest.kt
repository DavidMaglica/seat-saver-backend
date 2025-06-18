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
        val response = userService.updatePassword(mockedUser.id, "")

        response.success `should be` false
        response.message `should be equal to` "Password cannot be empty."

        verifyNoInteractions(userRepository)
    }

    @Test
    fun `should return failure response if user not found`() {
        `when`(userRepository.findById(anyInt())).thenReturn(Optional.empty())

        val response = userService.updatePassword(mockedUser.id, NEW_PASSWORD)

        response.success `should be` false
        response.message `should be equal to` "User not found."

        verify(userRepository, times(1)).findById(mockedUser.id)
        verifyNoMoreInteractions(userRepository)
    }

    @Test
    fun `should return failure response if can't update password`() {
        `when`(userRepository.findById(anyInt())).thenReturn(Optional.of(mockedUser))
        `when`(userRepository.save(any())).thenThrow(RuntimeException())

        val response = userService.updatePassword(mockedUser.id, NEW_PASSWORD)

        response.success `should be` false
        response.message `should be equal to` "Error while updating password. Please try again later."

        verify(userRepository, times(1)).findById(mockedUser.id)
        verify(userRepository, times(1)).save(any())
        verifyNoMoreInteractions(userRepository)
    }

    @Test
    fun `should update password`() {
        `when`(userRepository.findById(anyInt())).thenReturn(Optional.of(mockedUser))

        val response = userService.updatePassword(
            mockedUser.id,
            NEW_PASSWORD
        )

        response.success `should be` true
        response.message `should be equal to` "Password successfully updated."

        verify(userRepository).save(userEntityArgumentCaptor.capture())
        val updatedUser = userEntityArgumentCaptor.value
        passwordEncoder.matches(NEW_PASSWORD, updatedUser.password) `should be` true

        verify(userRepository, times(1)).findById(mockedUser.id)
        verify(userRepository, times(1)).save(any())
        verifyNoMoreInteractions(userRepository)
    }
}
