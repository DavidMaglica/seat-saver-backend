package fipu.diplomski.dmaglica.mobile.venue

import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
class GetUtilisationRateTest : BaseVenueServiceTest() {

    private val venue = createVenue(1)


    private val reservations = listOf(
        createReservation(1, venueId = 1, numberOfGuests = 20),
        createReservation(2, venueId = 1, numberOfGuests = 10),
        createReservation(3, venueId = 2, numberOfGuests = 5),
    )

    @Test
    fun `should return 0 when no venues owned`() {
        `when`(venueRepository.findByOwnerId(venue.ownerId)).thenReturn(emptyList())

        val result = venueService.getVenueUtilisationRate(venue.ownerId)

        result `should be equal to` 0.0

        verify(venueRepository).findByOwnerId(venue.ownerId)
        verifyNoMoreInteractions(venueRepository)
        verifyNoInteractions(reservationRepository)
    }

    @Test
    fun `should return 0 when no reservations exist for owned venues`() {
        val (lowerBound, upperBound) = getSurroundingHalfHours(LocalDateTime.now())
        `when`(venueRepository.findByOwnerId(venue.ownerId)).thenReturn(listOf(venue))
        `when`(
            reservationRepository.findByVenueIdInAndDatetimeBetween(
                listOf(venue.id),
                lowerBound,
                upperBound
            )
        ).thenReturn(emptyList())

        val result = venueService.getVenueUtilisationRate(venue.ownerId)

        result `should be equal to` 0.0

        verify(venueRepository).findByOwnerId(venue.ownerId)
        verify(reservationRepository).findByVenueIdInAndDatetimeBetween(listOf(venue.id), lowerBound, upperBound)
        verifyNoMoreInteractions(venueRepository, reservationRepository)
    }

    @Test
    fun `should return correct utilisation rate when reservations exist for owned venues`() {
        val (lowerBound, upperBound) = getSurroundingHalfHours(LocalDateTime.now())
        `when`(venueRepository.findByOwnerId(venue.ownerId)).thenReturn(listOf(venue))
        `when`(
            reservationRepository.findByVenueIdInAndDatetimeBetween(
                listOf(venue.id),
                lowerBound,
                upperBound
            )
        ).thenReturn(reservations)

        val result = venueService.getVenueUtilisationRate(venue.ownerId)


        // Total capacity = 100, Total booked = 35, Utilisation rate = 35%
        result `should be equal to` 35.0

        verify(venueRepository).findByOwnerId(venue.ownerId)
        verify(reservationRepository).findByVenueIdInAndDatetimeBetween(listOf(venue.id), lowerBound, upperBound)
        verifyNoMoreInteractions(venueRepository, reservationRepository)
    }
}