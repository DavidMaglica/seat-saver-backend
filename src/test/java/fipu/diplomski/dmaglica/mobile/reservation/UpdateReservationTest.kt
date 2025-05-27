package fipu.diplomski.dmaglica.mobile.reservation

import fipu.diplomski.dmaglica.exception.ReservationNotFoundException
import fipu.diplomski.dmaglica.exception.UserNotFoundException
import fipu.diplomski.dmaglica.exception.VenueNotFoundException
import fipu.diplomski.dmaglica.model.request.UpdateReservationRequest
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles
import java.sql.SQLException
import java.util.*

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
class UpdateReservationTest : BaseReservationServiceTest() {

    companion object {
        private val mockedRequest = UpdateReservationRequest(
            userEmail = "user1@mail.com",
            reservationId = 1,
            reservationDate = "02-08-2025 10:00",
            numberOfPeople = 3,
            venueId = 1
        )
        private val invalidRequest = UpdateReservationRequest(
            userEmail = "user1@mail.com",
            reservationId = 1,
            reservationDate = null,
            numberOfPeople = 0,
            venueId = 1
        )
        private val noChangeRequest = UpdateReservationRequest(
            userEmail = "user1@mail.com",
            reservationId = 1,
            reservationDate = "02-08-2025 10:00",
            numberOfPeople = 2,
            venueId = 1
        )
    }

    @Test
    fun `should throw if user does not exist`() {
        `when`(userRepository.findByEmail(anyString())).thenReturn(null)

        val exception = assertThrows<UserNotFoundException> {
            reservationService.update(mockedRequest)
        }

        exception.message `should be equal to` "User with email ${mockedRequest.userEmail} not found"

        verify(userRepository, times(1)).findByEmail(anyString())
        verifyNoInteractions(reservationRepository)
    }

    @Test
    fun `should throw if reservation does not exist`() {
        `when`(userRepository.findByEmail(anyString())).thenReturn(mockedUser)
        `when`(reservationRepository.findById(anyInt())).thenReturn(Optional.empty())

        val exception = assertThrows<ReservationNotFoundException> {
            reservationService.update(mockedRequest)
        }

        exception.message `should be equal to` "Reservation not found"

        verify(userRepository, times(1)).findByEmail(anyString())
        verify(reservationRepository, times(1)).findById(anyInt())
    }

    @Test
    fun `should return early if request is invalid`() {
        `when`(userRepository.findByEmail(anyString())).thenReturn(mockedUser)
        `when`(reservationRepository.findById(anyInt())).thenReturn(Optional.of(mockedReservation))

        val result =
            reservationService.update(invalidRequest)

        result.success `should be` false
        result.message `should be equal to` "Request is not valid"

        verify(userRepository, times(1)).findByEmail(anyString())
        verify(reservationRepository, times(1)).findById(anyInt())
    }

    @Test
    fun `should return early if request does not contain any changes`() {
        `when`(userRepository.findByEmail(anyString())).thenReturn(mockedUser)
        `when`(reservationRepository.findById(anyInt())).thenReturn(Optional.of(mockedReservation))

        val result =
            reservationService.update(noChangeRequest)

        result.success `should be` false
        result.message `should be equal to` "No changes to update"

        verify(userRepository, times(1)).findByEmail(anyString())
        verify(reservationRepository, times(1)).findById(anyInt())
    }

    @Test
    fun `should throw if venue does not exist`() {
        `when`(userRepository.findByEmail(anyString())).thenReturn(mockedUser)
        `when`(reservationRepository.findById(anyInt())).thenReturn(Optional.of(mockedReservation))
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.empty())

        val exception = assertThrows<VenueNotFoundException> {
            reservationService.update(mockedRequest)
        }

        exception.message `should be equal to` "Venue with id ${mockedRequest.venueId} not found"

        verify(userRepository, times(1)).findByEmail(anyString())
        verify(reservationRepository, times(1)).findById(anyInt())
        verify(venueRepository, times(1)).findById(anyInt())
        verifyNoMoreInteractions(reservationRepository)
    }

    @Test
    fun `should throw if unable to update reservation`() {
        `when`(userRepository.findByEmail(anyString())).thenReturn(mockedUser)
        `when`(reservationRepository.findById(anyInt())).thenReturn(Optional.of(mockedReservation))
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(mockedVenue))
        `when`(reservationRepository.save(any())).thenThrow(RuntimeException())

        val exception = assertThrows<SQLException> {
            reservationService.update(mockedRequest)
        }

        exception.message `should be equal to` "Error while updating reservation with id ${mockedRequest.reservationId}"

        verify(userRepository, times(1)).findByEmail(anyString())
        verify(reservationRepository, times(1)).findById(anyInt())
        verify(venueRepository, times(1)).findById(anyInt())
        verify(reservationRepository, times(1)).save(any())
    }

    @Test
    fun `should update reservation`() {
        `when`(userRepository.findByEmail(anyString())).thenReturn(mockedUser)
        `when`(reservationRepository.findById(anyInt())).thenReturn(Optional.of(mockedReservation))
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(mockedVenue))

        val result = reservationService.update(mockedRequest)

        result.success `should be` true
        result.message `should be equal to` "Reservation updated successfully"

        verify(reservationRepository).save(reservationArgumentCaptor.capture())
        val updatedReservation = reservationArgumentCaptor.value
        updatedReservation.venueId `should be equal to` mockedReservation.venueId
        updatedReservation.userId `should be equal to` mockedUser.id
        updatedReservation.numberOfGuests `should be equal to` mockedRequest.numberOfPeople
        updatedReservation.datetime `should be equal to` mockedRequest.reservationDate

        verify(venueRepository).save(venueArgumentCaptor.capture())
        val venue = venueArgumentCaptor.value
        venue.id `should be equal to` mockedVenue.id
        venue.availableCapacity `should be equal to` (mockedVenue.maximumCapacity - mockedRequest.numberOfPeople!!)

        verify(userRepository, times(1)).findByEmail(anyString())
        verify(reservationRepository, times(1)).findById(anyInt())
        verify(venueRepository, times(1)).findById(anyInt())
        verify(venueRepository, times(1)).save(venueArgumentCaptor.capture())
        verify(reservationRepository, times(1)).save(reservationArgumentCaptor.capture())
    }
}
