package fipu.diplomski.dmaglica.mobile.unit.reservation

import fipu.diplomski.dmaglica.exception.ReservationNotFoundException
import fipu.diplomski.dmaglica.model.request.UpdateReservationRequest
import fipu.diplomski.dmaglica.repo.entity.WorkingDaysEntity
import jakarta.persistence.EntityNotFoundException
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
class UpdateReservationTest : BaseReservationServiceTest() {

    companion object {
        private const val MOCK_RESERVATION_ID = 1
        private val mockedRequest = UpdateReservationRequest(
            reservationDate = LocalDateTime.now().withHour(12),
            numberOfGuests = 3,
        )
        private val invalidRequest = UpdateReservationRequest(
            reservationDate = null,
            numberOfGuests = 0,
        )
        private val noChangeRequest = UpdateReservationRequest(
            reservationDate = null,
            numberOfGuests = 2,
        )
    }

    private val allWorkingDays = IntRange(0, 6).map {
        WorkingDaysEntity().apply {
            venueId = mockedVenue.id
            dayOfWeek = it
        }
    }

    @Test
    fun `should throw if reservation does not exist`() {
        `when`(reservationRepository.findById(anyInt())).thenReturn(Optional.empty())

        val exception = assertThrows<ReservationNotFoundException> {
            reservationService.update(MOCK_RESERVATION_ID, mockedRequest)
        }

        exception.message `should be equal to` "Reservation not found"

        verify(reservationRepository, times(1)).findById(MOCK_RESERVATION_ID)
        verifyNoMoreInteractions(reservationRepository)
    }

    @Test
    fun `should return early if request is invalid`() {
        `when`(reservationRepository.findById(anyInt())).thenReturn(Optional.of(mockedReservation))

        val response =
            reservationService.update(MOCK_RESERVATION_ID, invalidRequest)

        response.success `should be` false
        response.message `should be equal to` "Request is not valid."

        verify(reservationRepository, times(1)).findById(mockedReservation.id)
        verifyNoMoreInteractions(reservationRepository)
    }

    @Test
    fun `should return early if request does not contain any changes`() {
        `when`(reservationRepository.findById(anyInt())).thenReturn(Optional.of(mockedReservation))

        val response = reservationService.update(MOCK_RESERVATION_ID, noChangeRequest)

        response.success `should be` false
        response.message `should be equal to` "No modifications found. Please change at least one field."

        verify(reservationRepository, times(1)).findById(mockedReservation.id)
        verifyNoMoreInteractions(reservationRepository)
    }

    @Test
    fun `should throw if venue does not exist`() {
        `when`(reservationRepository.findById(anyInt())).thenReturn(Optional.of(mockedReservation))
        `when`(venueRepository.findById(mockedReservation.id)).thenReturn(Optional.empty())

        val exception = assertThrows<EntityNotFoundException> {
            reservationService.update(mockedReservation.id, mockedRequest)
        }

        exception.message `should be equal to` "Venue with id ${mockedVenue.id} not found"
        verify(reservationRepository, times(1)).findById(mockedReservation.id)
        verify(venueRepository, times(1)).findById(mockedReservation.venueId)
        verifyNoMoreInteractions(reservationRepository, venueRepository)
    }

    @Test
    fun `should return early if new reservation time is outside working hours`() {
        `when`(reservationRepository.findById(anyInt())).thenReturn(Optional.of(mockedReservation))
        `when`(venueRepository.findById(mockedReservation.id)).thenReturn(Optional.of(mockedVenue))
        val invalidTimeRequest = mockedRequest.copy(reservationDate = LocalDateTime.of(2025, 12, 30, 18, 0))

        val response = reservationService.update(MOCK_RESERVATION_ID, invalidTimeRequest)

        response.success `should be` false
        response.message `should be equal to` "The venue is closed at the selected time. Please choose a different time."

        verify(reservationRepository, times(1)).findById(mockedReservation.id)
        verify(venueRepository, times(1)).findById(mockedReservation.venueId)
        verifyNoMoreInteractions(reservationRepository, venueRepository)
        verifyNoInteractions(workingDaysRepository)
    }

    @Test
    fun `should return early if new reservation time is outside working days`() {
        `when`(reservationRepository.findById(anyInt())).thenReturn(Optional.of(mockedReservation))
        `when`(venueRepository.findById(mockedReservation.id)).thenReturn(Optional.of(mockedVenue))
        `when`(workingDaysRepository.findAllByVenueId(mockedVenue.id)).thenReturn(emptyList())

        val response = reservationService.update(MOCK_RESERVATION_ID, mockedRequest)

        response.success `should be` false
        response.message `should be equal to` "The venue is closed on the selected day. Please choose a different day."

        verify(reservationRepository, times(1)).findById(mockedReservation.id)
        verify(venueRepository, times(1)).findById(mockedReservation.venueId)
        verify(workingDaysRepository, times(1)).findAllByVenueId(mockedVenue.id)
        verifyNoMoreInteractions(reservationRepository, venueRepository, workingDaysRepository)
    }

    @Test
    fun `should throw if unable to update reservation`() {
        `when`(reservationRepository.findById(anyInt())).thenReturn(Optional.of(mockedReservation))
        `when`(venueRepository.findById(mockedReservation.id)).thenReturn(Optional.of(mockedVenue))
        `when`(workingDaysRepository.findAllByVenueId(mockedVenue.id)).thenReturn(allWorkingDays)
        `when`(reservationRepository.save(any())).thenThrow(RuntimeException())

        val response = reservationService.update(MOCK_RESERVATION_ID, mockedRequest)

        response.success `should be equal to` false
        response.message `should be equal to` "Error while updating reservation. Please try again later."

        verify(reservationRepository, times(1)).findById(mockedReservation.id)
        verify(venueRepository, times(1)).findById(mockedReservation.venueId)
        verify(reservationRepository, times(1)).save(any())
        verifyNoMoreInteractions(reservationRepository, venueRepository)
    }

    @Test
    fun `should update reservation`() {
        `when`(reservationRepository.findById(anyInt())).thenReturn(Optional.of(mockedReservation))
        `when`(venueRepository.findById(mockedReservation.id)).thenReturn(Optional.of(mockedVenue))
        `when`(workingDaysRepository.findAllByVenueId(mockedVenue.id)).thenReturn(allWorkingDays)

        val response = reservationService.update(MOCK_RESERVATION_ID, mockedRequest)

        response.success `should be` true
        response.message `should be equal to` "Reservation updated successfully."

        verify(reservationRepository).save(reservationArgumentCaptor.capture())
        val updatedReservation = reservationArgumentCaptor.value
        updatedReservation.venueId `should be equal to` mockedReservation.venueId
        updatedReservation.userId `should be equal to` mockedUser.id
        updatedReservation.numberOfGuests `should be equal to` mockedRequest.numberOfGuests
        updatedReservation.datetime `should be equal to` mockedRequest.reservationDate

        verify(reservationRepository, times(1)).findById(mockedReservation.id)
        verify(venueRepository, times(1)).findById(mockedReservation.venueId)
        verify(reservationRepository, times(1)).save(reservationArgumentCaptor.capture())
        verifyNoMoreInteractions(reservationRepository, venueRepository)
    }
}
