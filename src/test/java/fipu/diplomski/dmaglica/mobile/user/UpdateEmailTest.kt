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
class UpdateEmailTest : BaseUserServiceTest() {

    companion object {
        const val NEW_EMAIL = "test@test.com"
    }

    @Test
    fun `should return failure response if new email is empty`() {
        val response = userService.updateEmail(mockedUser.id, "")

        response.success `should be` false
        response.message `should be equal to` "Email cannot be empty."

        verifyNoInteractions(userRepository)
    }

    @Test
    fun `should return failure response if user not found`() {
        `when`(userRepository.findById(anyInt())).thenReturn(Optional.empty())

        val response = userService.updateEmail(mockedUser.id, NEW_EMAIL)

        response.success `should be` false
        response.message `should be equal to` "User not found."

        verify(userRepository, times(1)).findById(mockedUser.id)
        verifyNoMoreInteractions(userRepository)
    }

    @Test
    fun `should return failure response if can't update email`() {
        `when`(userRepository.findById(anyInt())).thenReturn(Optional.of(mockedUser))
        `when`(userRepository.save(any())).thenThrow(RuntimeException())

        val response = userService.updateEmail(mockedUser.id, NEW_EMAIL)

        response.success `should be` false
        response.message `should be equal to` "Error while updating email. Please try again later."

        verify(userRepository, times(1)).findById(mockedUser.id)
        verify(userRepository, times(1)).save(any())
        verifyNoMoreInteractions(userRepository)
    }

    @Test
    fun `should update email`() {
        `when`(userRepository.findById(anyInt())).thenReturn(Optional.of(mockedUser))

        val response = userService.updateEmail(mockedUser.id, NEW_EMAIL)

        response.success `should be` true
        response.message `should be equal to` "Email updated to $NEW_EMAIL successfully."

        verify(userRepository).save(userEntityArgumentCaptor.capture())
        val updatedUser = userEntityArgumentCaptor.value
        updatedUser.email `should be equal to` NEW_EMAIL

        verify(userRepository, times(1)).findById(mockedUser.id)
        verify(userRepository, times(1)).save(any())
        verifyNoMoreInteractions(userRepository)
    }
}
