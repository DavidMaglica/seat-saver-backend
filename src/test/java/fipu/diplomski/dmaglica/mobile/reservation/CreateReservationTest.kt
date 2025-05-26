package fipu.diplomski.dmaglica.mobile.reservation

import fipu.diplomski.dmaglica.exception.UserNotFoundException
import fipu.diplomski.dmaglica.exception.VenueNotFoundException
import fipu.diplomski.dmaglica.model.request.CreateReservationRequest
import jakarta.persistence.EntityNotFoundException
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles
import java.util.*

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
class CreateReservationTest : BaseReservationServiceTest() {

    companion object {
        private val mockedRequest = CreateReservationRequest(
            userEmail = "user1@mail.com",
            venueId = 1,
            reservationDate = "02-08-2025 10:00",
            numberOfPeople = 2,
        )
    }

    @Test
    fun `should throw if user does not exist`() {
        `when`(userRepository.findByEmail(anyString())).thenReturn(null)

        val exception = assertThrows<UserNotFoundException> {
            reservationService.create(mockedRequest)
        }

        exception.message `should be equal to` "User with email ${mockedRequest.userEmail} not found"

        verify(userRepository, times(1)).findByEmail(anyString())
        verifyNoInteractions(venueRepository)
        verifyNoInteractions(reservationRepository)
    }

    @Test
    fun `should throw if venue does not exist`() {
        `when`(userRepository.findByEmail(anyString())).thenReturn(mockedUser)
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.empty())

        val exception = assertThrows<VenueNotFoundException> {
            reservationService.create(mockedRequest)
        }

        exception.message `should be equal to` "Venue with id ${mockedRequest.venueId} not found"

        verify(userRepository, times(1)).findByEmail(anyString())
        verify(venueRepository, times(1)).findById(anyInt())
        verifyNoInteractions(reservationRepository)
    }

    @Test
    fun `should throw if unable to save reservation`() {
        `when`(userRepository.findByEmail(anyString())).thenReturn(mockedUser)
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(mockedVenue))
        `when`(reservationRepository.save(any())).thenThrow(RuntimeException())

        val exception = assertThrows<EntityNotFoundException> {
            reservationService.create(mockedRequest)
        }

        exception.message `should be equal to` "Error while creating reservation for user with email ${mockedUser.email}"

        verify(userRepository, times(1)).findByEmail(anyString())
        verify(venueRepository, times(1)).findById(anyInt())
        verify(reservationRepository, times(1)).save(any())
    }

    @Test
    fun `should create reservation`() {
        `when`(userRepository.findByEmail(anyString())).thenReturn(mockedUser)
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(mockedVenue))
        `when`(reservationRepository.save(any())).thenReturn(mockedReservation)

        val response =
            reservationService.create(mockedRequest)

        response.success `should be equal to` true
        response.message `should be equal to` "Reservation created successfully"

        verify(reservationRepository).save(reservationArgumentCaptor.capture())
        val reservation = reservationArgumentCaptor.value
        reservation.userId `should be equal to` mockedUser.id
        reservation.venueId `should be equal to` mockedRequest.venueId
        reservation.datetime `should be equal to` mockedRequest.reservationDate
        reservation.numberOfGuests `should be equal to` mockedRequest.numberOfPeople

        verify(userRepository, times(1)).findByEmail(anyString())
        verify(venueRepository, times(1)).findById(anyInt())
        verify(reservationRepository, times(1)).save(reservationArgumentCaptor.capture())
    }
}
