package fipu.diplomski.dmaglica.mobile.reservation

import fipu.diplomski.dmaglica.exception.VenueNotFoundException
import fipu.diplomski.dmaglica.model.request.CreateReservationRequest
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
class CreateReservationTest : BaseReservationServiceTest() {

    companion object {
        private val mockedRequest = CreateReservationRequest(
            userId = 1,
            venueId = 1,
            reservationDate = LocalDateTime.now(),
            numberOfGuests = 2,
        )
    }

    @Test
    fun `should return failure response if user does not exist`() {
        `when`(userRepository.findById(anyInt())).thenReturn(Optional.empty())

        val response = reservationService.create(mockedRequest)

        response.success `should be equal to` false
        response.message `should be equal to` "User not found. Please try again later."

        verify(userRepository, times(1)).findById(mockedUser.id)
        verifyNoInteractions(venueRepository)
        verifyNoInteractions(reservationRepository)
        verifyNoMoreInteractions(userRepository)
    }

    @Test
    fun `should throw if venue does not exist`() {
        `when`(userRepository.findById(anyInt())).thenReturn(Optional.of(mockedUser))
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.empty())

        val exception = assertThrows<VenueNotFoundException> {
            reservationService.create(mockedRequest)
        }

        exception.message `should be equal to` "Venue with id ${mockedRequest.venueId} not found"

        verify(userRepository, times(1)).findById(mockedUser.id)
        verify(venueRepository, times(1)).findById(mockedVenue.id)
        verifyNoInteractions(reservationRepository)
        verifyNoMoreInteractions(userRepository, venueRepository)
    }

    @Test
    fun `should return failure response if venue is fully booked`() {
        val (lowerBound, upperBound) = getSurroundingHalfHours(mockedRequest.reservationDate)

        `when`(userRepository.findById(anyInt())).thenReturn(Optional.of(mockedUser))
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(mockedVenue))
        `when`(
            reservationRepository.findByVenueIdAndDatetimeBetween(
                mockedRequest.venueId, lowerBound, upperBound
            )
        ).thenReturn(listOf(mockedReservation.copy(numberOfGuests = mockedVenue.maximumCapacity)))

        val response = reservationService.create(mockedRequest)

        response.success `should be equal to` false
        response.message `should be equal to` "The venue is fully booked for the selected time. Please choose a different time."

        verify(userRepository, times(1)).findById(mockedUser.id)
        verify(venueRepository, times(1)).findById(mockedVenue.id)
        verify(reservationRepository, times(1)).findByVenueIdAndDatetimeBetween(
            mockedVenue.id, lowerBound, upperBound
        )
        verifyNoMoreInteractions(userRepository, venueRepository, reservationRepository)
    }

    @Test
    fun `should return failure response if unable to save reservation`() {
        val (lowerBound, upperBound) = getSurroundingHalfHours(mockedRequest.reservationDate)

        `when`(userRepository.findById(anyInt())).thenReturn(Optional.of(mockedUser))
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(mockedVenue))
        `when`(
            reservationRepository.findByVenueIdAndDatetimeBetween(
                mockedRequest.venueId, lowerBound, upperBound
            )
        ).thenReturn(emptyList())
        `when`(reservationRepository.save(any())).thenThrow(RuntimeException())

        val response = reservationService.create(mockedRequest)

        response.success `should be equal to` false
        response.message `should be equal to` "Error while creating reservation. Please try again later."

        verify(userRepository, times(1)).findById(mockedUser.id)
        verify(venueRepository, times(1)).findById(mockedVenue.id)
        verify(reservationRepository, times(1)).findByVenueIdAndDatetimeBetween(
            mockedVenue.id, lowerBound, upperBound
        )
        verify(reservationRepository, times(1)).save(any())
        verifyNoMoreInteractions(userRepository, venueRepository, reservationRepository)
    }

    @Test
    fun `should create reservation`() {
        val (lowerBound, upperBound) = getSurroundingHalfHours(mockedRequest.reservationDate)

        `when`(userRepository.findById(anyInt())).thenReturn(Optional.of(mockedUser))
        `when`(venueRepository.findById(anyInt())).thenReturn(Optional.of(mockedVenue))
        `when`(
            reservationRepository.findByVenueIdAndDatetimeBetween(
                mockedRequest.venueId, lowerBound, upperBound
            )
        ).thenReturn(emptyList())
        `when`(reservationRepository.save(any())).thenReturn(mockedReservation)

        val response = reservationService.create(mockedRequest)

        response.success `should be equal to` true
        response.message `should be equal to` "Reservation created successfully."

        verify(reservationRepository).save(reservationArgumentCaptor.capture())
        val reservation = reservationArgumentCaptor.value
        reservation.userId `should be equal to` mockedUser.id
        reservation.venueId `should be equal to` mockedRequest.venueId
        reservation.datetime `should be equal to` mockedRequest.reservationDate
        reservation.numberOfGuests `should be equal to` mockedRequest.numberOfGuests

        verify(userRepository, times(1)).findById(mockedUser.id)
        verify(venueRepository, times(1)).findById(mockedVenue.id)
        verify(reservationRepository, times(1)).findByVenueIdAndDatetimeBetween(
            mockedVenue.id, lowerBound, upperBound
        )
        verifyNoMoreInteractions(userRepository, venueRepository, reservationRepository)
    }

    private fun getSurroundingHalfHours(time: LocalDateTime): Pair<LocalDateTime, LocalDateTime> {
        val minute = time.minute
        val second = time.second
        val nano = time.nano
        val truncated = time.minusSeconds(second.toLong()).minusNanos(nano.toLong())

        val previous = when {
            minute < 30 -> truncated.withMinute(0)
            else -> truncated.withMinute(30)
        }

        val next = previous.plusHours(1)

        return previous to next
    }
}
