package fipu.diplomski.dmaglica.venue

import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles
import java.sql.SQLException

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
class CreateVenueTest : VenueServiceTest() {

    @Test
    fun `should throw if unable to save venue`() {
        `when`(venueRepository.save(any())).thenThrow(RuntimeException("Unable to save venue"))

        val exception = assertThrows<SQLException> {
            venueService.create(
                mockedVenue.name,
                mockedVenue.location,
                mockedVenue.description,
                mockedVenue.venueTypeId,
                mockedVenue.workingHours,
            )
        }

        exception.message `should be equal to` "Error while saving venue: ${mockedVenue.name}"
    }

    @Test
    fun `should save venue`() {
        `when`(venueRepository.save(any())).thenReturn(mockedVenue)

        val result = venueService.create(
            mockedVenue.name,
            mockedVenue.location,
            mockedVenue.description,
            mockedVenue.venueTypeId,
            mockedVenue.workingHours
        )

        result.success `should be equal to` true
        result.message `should be equal to` "Venue ${mockedVenue.name} created successfully"

        verify(venueRepository, times(1)).save(any())
    }
}