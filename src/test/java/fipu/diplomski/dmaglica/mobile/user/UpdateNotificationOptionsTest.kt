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
class UpdateNotificationOptionsTest : BaseUserServiceTest() {

    @Test
    fun `should return failure response if user not found`() {
        `when`(userRepository.findById(anyInt())).thenReturn(Optional.empty())

        val result = userService.updateNotificationOptions(
            mockedUser.id,
            pushNotificationsTurnedOn = true,
            emailNotificationsTurnedOn = true,
            locationServicesTurnedOn = true
        )

        result.success `should be` false
        result.message `should be equal to` "User not found."

        verify(userRepository, times(1)).findById(mockedUser.id)
        verifyNoMoreInteractions(userRepository)
        verifyNoInteractions(notificationOptionsRepository)
    }

    @Test
    fun `should return failure response if can't update notification options`() {
        `when`(userRepository.findById(anyInt())).thenReturn(Optional.of(mockedUser))
        `when`(notificationOptionsRepository.findByUserId(anyInt())).thenReturn(mockedNotificationOptions)
        `when`(notificationOptionsRepository.save(any())).thenThrow(RuntimeException())

        val result = userService.updateNotificationOptions(
            mockedUser.id,
            pushNotificationsTurnedOn = false,
            emailNotificationsTurnedOn = false,
            locationServicesTurnedOn = false
        )

        result.success `should be` false
        result.message `should be equal to` "Error while updating notification options. Please try again later."

        verify(userRepository, times(1)).findById(mockedUser.id)
        verify(notificationOptionsRepository, times(1)).findByUserId(mockedUser.id)
        verify(notificationOptionsRepository, times(1)).save(any())
        verifyNoMoreInteractions(userRepository, notificationOptionsRepository)
    }

    @Test
    fun `should update only push notifications`() {
        `when`(userRepository.findById(anyInt())).thenReturn(Optional.of(mockedUser))
        `when`(notificationOptionsRepository.findByUserId(anyInt())).thenReturn(mockedNotificationOptions)

        val result = userService.updateNotificationOptions(
            mockedUser.id,
            pushNotificationsTurnedOn = true,
            emailNotificationsTurnedOn = false,
            locationServicesTurnedOn = false
        )

        result.success `should be` true
        result.message `should be equal to` "Notification options successfully updated."

        verify(notificationOptionsRepository).save(notificationOptionsArgumentCaptor.capture())
        val updatedNotificationOptions = notificationOptionsArgumentCaptor.value
        updatedNotificationOptions.locationServicesEnabled `should be equal to` false
        updatedNotificationOptions.pushNotificationsEnabled `should be equal to` true
        updatedNotificationOptions.emailNotificationsEnabled `should be equal to` false

        verify(userRepository, times(1)).findById(mockedUser.id)
        verify(notificationOptionsRepository, times(1)).findByUserId(mockedUser.id)
        verify(notificationOptionsRepository, times(1)).save(any())
        verifyNoMoreInteractions(userRepository, notificationOptionsRepository)
    }

    @Test
    fun `should update notification options`() {
        `when`(userRepository.findById(anyInt())).thenReturn(Optional.of(mockedUser))
        `when`(notificationOptionsRepository.findByUserId(anyInt())).thenReturn(mockedNotificationOptions)

        val result = userService.updateNotificationOptions(
            mockedUser.id,
            pushNotificationsTurnedOn = true,
            emailNotificationsTurnedOn = true,
            locationServicesTurnedOn = true
        )

        result.success `should be` true
        result.message `should be equal to` "Notification options successfully updated."

        verify(notificationOptionsRepository).save(notificationOptionsArgumentCaptor.capture())
        val updatedNotificationOptions = notificationOptionsArgumentCaptor.value
        updatedNotificationOptions.locationServicesEnabled `should be equal to` true
        updatedNotificationOptions.pushNotificationsEnabled `should be equal to` true
        updatedNotificationOptions.emailNotificationsEnabled `should be equal to` true

        verify(userRepository, times(1)).findById(mockedUser.id)
        verify(notificationOptionsRepository, times(1)).findByUserId(mockedUser.id)
        verify(notificationOptionsRepository, times(1)).save(any())
        verifyNoMoreInteractions(userRepository, notificationOptionsRepository)
    }
}
