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
class UpdateNotificationOptionsTest : UserServiceTest() {

    @Test
    fun `should throw if user not found`() {
        `when`(userRepository.findByEmail(anyString())).thenReturn(null)

        assertThrows<UserNotFoundException> {
            userService.updateNotificationOptions(
                mockedUser.email,
                !mockedNotificationOptions.locationServicesTurnedOn,
                !mockedNotificationOptions.pushNotificationsTurnedOn,
                !mockedNotificationOptions.emailNotificationsTurnedOn
            )
        }
    }

    @Test
    fun `should throw if can't update notification options`() {
        `when`(userRepository.findByEmail(anyString())).thenReturn(mockedUser)
        `when`(notificationOptionsRepository.getByUserId(anyInt())).thenReturn(mockedNotificationOptions)
        `when`(notificationOptionsRepository.save(any())).thenThrow(RuntimeException())

        assertThrows<SQLException> {
            userService.updateNotificationOptions(
                mockedUser.email,
                !mockedNotificationOptions.locationServicesTurnedOn,
                !mockedNotificationOptions.pushNotificationsTurnedOn,
                !mockedNotificationOptions.emailNotificationsTurnedOn
            )
        }

        verify(userRepository, times(1)).findByEmail(mockedUser.email)
    }

    @Test
    fun `should update notification options`() {
        `when`(userRepository.findByEmail(anyString())).thenReturn(mockedUser)
        `when`(notificationOptionsRepository.getByUserId(anyInt())).thenReturn(mockedNotificationOptions)

        val result = userService.updateNotificationOptions(
            mockedUser.email,
            !mockedNotificationOptions.locationServicesTurnedOn,
            !mockedNotificationOptions.pushNotificationsTurnedOn,
            !mockedNotificationOptions.emailNotificationsTurnedOn
        )

        result.success `should be` true
        result.message `should be equal to` "Notification options for user with email ${mockedUser.email} successfully updated"

        verify(userRepository, times(1)).findByEmail(mockedUser.email)
        verify(notificationOptionsRepository, times(1)).getByUserId(mockedUser.id)
        verify(notificationOptionsRepository, times(1)).save(any())
    }
}
