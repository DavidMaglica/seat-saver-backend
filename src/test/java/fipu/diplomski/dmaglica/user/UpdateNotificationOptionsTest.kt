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
class UpdateNotificationOptionsTest : AbstractUserServiceTest() {

    companion object {
        private const val LOCATION_SERVICES_TURNED_ON = true
        private const val PUSH_NOTIFICATIONS_TURNED_ON = true
        private const val EMAIL_NOTIFICATIONS_TURNED_ON = true
    }

    @Test
    fun `should throw if user not found`() {
        `when`(userRepository.findByEmail(anyString())).thenReturn(null)

        assertThrows<UserNotFoundException> {
            userService.updateNotificationOptions(
                USER_EMAIL,
                LOCATION_SERVICES_TURNED_ON,
                PUSH_NOTIFICATIONS_TURNED_ON,
                EMAIL_NOTIFICATIONS_TURNED_ON
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
                USER_EMAIL,
                LOCATION_SERVICES_TURNED_ON,
                PUSH_NOTIFICATIONS_TURNED_ON,
                EMAIL_NOTIFICATIONS_TURNED_ON
            )
        }

        verify(userRepository, times(1)).findByEmail(USER_EMAIL)
    }

    @Test
    fun `should update notification options`() {
        `when`(userRepository.findByEmail(anyString())).thenReturn(mockedUser)
        `when`(notificationOptionsRepository.getByUserId(anyInt())).thenReturn(mockedNotificationOptions)

        val result = userService.updateNotificationOptions(
            USER_EMAIL,
            LOCATION_SERVICES_TURNED_ON,
            PUSH_NOTIFICATIONS_TURNED_ON,
            EMAIL_NOTIFICATIONS_TURNED_ON
        )

        result.success `should be` true
        result.message `should be equal to` "Notification options for user with email $USER_EMAIL successfully updated"

        verify(userRepository, times(1)).findByEmail(USER_EMAIL)
        verify(notificationOptionsRepository, times(1)).getByUserId(USER_ID)
        verify(notificationOptionsRepository, times(1)).save(any())
    }
}
