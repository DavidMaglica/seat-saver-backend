package fipu.diplomski.dmaglica.mobile.user

import org.amshove.kluent.`should be`
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should not be`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
class SignupTest : BaseUserServiceTest() {

    @Test
    fun `should return early if user already exists`() {
        `when`(userRepository.findByEmail(anyString())).thenReturn(mockedUser)

        val response = userService.signup(mockedUser.email, mockedUser.username, mockedUser.password)

        response.success `should be equal to` false
        response.message `should be equal to` "User with email ${mockedUser.email} already exists"

        verify(userRepository, times(1)).findByEmail(mockedUser.email)
        verifyNoInteractions(notificationOptionsRepository)
    }

    @Test
    fun `should return failure response if user not saved`() {
        `when`(userRepository.findByEmail(anyString())).thenReturn(null)
        `when`(userRepository.save(any())).thenThrow(RuntimeException("Error while saving user"))

        val response = userService.signup(mockedUser.email, mockedUser.username, mockedUser.password)

        response.success `should be equal to` false
        response.message `should be equal to` "Error while creating user. Please try again later."
        response.data `should be equal to` null

        verify(userRepository, times(1)).findByEmail(mockedUser.email)
        verify(userRepository, times(1)).save(any())
        verifyNoInteractions(notificationOptionsRepository)
        verifyNoMoreInteractions(userRepository)
    }

    @Test
    fun `should return failure response if notification options not saved`() {
        `when`(userRepository.findByEmail(anyString())).thenReturn(null)
        `when`(userRepository.save(any())).thenReturn(mockedUser)
        `when`(notificationOptionsRepository.save(any())).thenThrow(RuntimeException("Error while saving notification options"))

        val response = userService.signup(mockedUser.email, mockedUser.username, mockedUser.password)

        response.success `should be equal to` false
        response.message `should be equal to` "Error while creating user notification options. Please try again later."
        response.data `should be equal to` null

        verify(userRepository, times(1)).findByEmail(mockedUser.email)
        verify(userRepository, times(1)).save(any())
        verify(notificationOptionsRepository, times(1)).save(any())
        verifyNoMoreInteractions(userRepository, notificationOptionsRepository)
    }

    @Test
    fun `should save user and notification options`() {
        `when`(userRepository.findByEmail(anyString())).thenReturn(null)
        `when`(userRepository.save(any())).thenReturn(mockedUser)
        `when`(notificationOptionsRepository.save(any())).thenReturn(mockedNotificationOptions)

        val response = userService.signup(mockedUser.email, mockedUser.username, mockedUser.password)

        response.success `should be equal to` true
        response.message `should be equal to` "User with email ${mockedUser.email} successfully created"
        response.data `should not be` null
        response.data?.id `should be equal to` mockedUser.id
        response.data?.email `should be equal to` mockedUser.email
        response.data?.username `should be equal to` mockedUser.username
        passwordEncoder.matches(mockedUser.password, response.data?.password) `should be` true
        response.data?.roleId `should be equal to` mockedUser.roleId


        verify(userRepository).save(userEntityArgumentCaptor.capture())
        verify(notificationOptionsRepository).save(notificationOptionsArgumentCaptor.capture())
        val newUser = userEntityArgumentCaptor.value
        val newNotificationOptions = notificationOptionsArgumentCaptor.value

        newUser.email `should be equal to` mockedUser.email
        newUser.username `should be equal to` mockedUser.username
        passwordEncoder.matches(mockedUser.password, newUser.password)
        newUser.roleId `should be equal to` mockedUser.roleId
        newNotificationOptions.userId `should be equal to` mockedUser.id
        newNotificationOptions.emailNotificationsEnabled `should be equal to` mockedNotificationOptions.emailNotificationsEnabled
        newNotificationOptions.pushNotificationsEnabled `should be equal to` mockedNotificationOptions.pushNotificationsEnabled
        newNotificationOptions.locationServicesEnabled `should be equal to` mockedNotificationOptions.locationServicesEnabled

        verify(userRepository, times(1)).findByEmail(mockedUser.email)
        verify(userRepository, times(1)).save(any())
        verify(notificationOptionsRepository, times(1)).save(any())
        verifyNoMoreInteractions(userRepository, notificationOptionsRepository)
    }
}
