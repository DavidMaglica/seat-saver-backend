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

        val exception = assertThrows<UserNotFoundException> {
            userService.updateNotificationOptions(
                mockedUser.email,
                !mockedNotificationOptions.locationServicesEnabled,
                !mockedNotificationOptions.pushNotificationsEnabled,
                !mockedNotificationOptions.emailNotificationsEnabled
            )
        }

        exception.message `should be equal to` "User with email ${mockedUser.email} does not exist"
    }

    @Test
    fun `should throw if can't update notification options`() {
        `when`(userRepository.findByEmail(anyString())).thenReturn(mockedUser)
        `when`(notificationOptionsRepository.findByUserId(anyInt())).thenReturn(mockedNotificationOptions)
        `when`(notificationOptionsRepository.save(any())).thenThrow(RuntimeException())

        val exception = assertThrows<SQLException> {
            userService.updateNotificationOptions(
                mockedUser.email,
                pushNotificationsTurnedOn = false,
                emailNotificationsTurnedOn = false,
                locationServicesTurnedOn = false
            )
        }

        exception.message `should be equal to` "Error while updating notification options for user with email ${mockedUser.email}"

        verify(userRepository, times(1)).findByEmail(mockedUser.email)
    }

    @Test
    fun `should update only push notifications`() {
        `when`(userRepository.findByEmail(anyString())).thenReturn(mockedUser)
        `when`(notificationOptionsRepository.findByUserId(anyInt())).thenReturn(mockedNotificationOptions)

        val result = userService.updateNotificationOptions(
            mockedUser.email,
            pushNotificationsTurnedOn = true,
            emailNotificationsTurnedOn = false,
            locationServicesTurnedOn = false
        )

        result.success `should be` true
        result.message `should be equal to` "Notification options for user with email ${mockedUser.email} successfully updated"

        verify(notificationOptionsRepository).save(notificationOptionsArgumentCaptor.capture())
        val updatedNotificationOptions = notificationOptionsArgumentCaptor.value
        updatedNotificationOptions.locationServicesEnabled `should be equal to` false
        updatedNotificationOptions.pushNotificationsEnabled `should be equal to` true
        updatedNotificationOptions.emailNotificationsEnabled `should be equal to` false

        verify(userRepository, times(1)).findByEmail(mockedUser.email)
        verify(notificationOptionsRepository, times(1)).findByUserId(mockedUser.id)
        verify(notificationOptionsRepository, times(1)).save(any())
    }

    @Test
    fun `should update notification options`() {
        `when`(userRepository.findByEmail(anyString())).thenReturn(mockedUser)
        `when`(notificationOptionsRepository.findByUserId(anyInt())).thenReturn(mockedNotificationOptions)

        val result = userService.updateNotificationOptions(
            mockedUser.email,
            pushNotificationsTurnedOn = true,
            emailNotificationsTurnedOn = true,
            locationServicesTurnedOn = true
        )

        result.success `should be` true
        result.message `should be equal to` "Notification options for user with email ${mockedUser.email} successfully updated"

        verify(notificationOptionsRepository).save(notificationOptionsArgumentCaptor.capture())
        val updatedNotificationOptions = notificationOptionsArgumentCaptor.value
        updatedNotificationOptions.locationServicesEnabled `should be equal to` true
        updatedNotificationOptions.pushNotificationsEnabled `should be equal to` true
        updatedNotificationOptions.emailNotificationsEnabled `should be equal to` true

        verify(userRepository, times(1)).findByEmail(mockedUser.email)
        verify(notificationOptionsRepository, times(1)).findByUserId(mockedUser.id)
        verify(notificationOptionsRepository, times(1)).save(any())
    }
}
