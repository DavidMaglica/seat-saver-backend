package fipu.diplomski.dmaglica.venue

import fipu.diplomski.dmaglica.repo.VenueRatingRepository
import fipu.diplomski.dmaglica.repo.VenueRepository
import fipu.diplomski.dmaglica.repo.VenueTypeRepository
import fipu.diplomski.dmaglica.repo.entity.VenueEntity
import fipu.diplomski.dmaglica.repo.entity.VenueRatingEntity
import fipu.diplomski.dmaglica.service.ImageService
import fipu.diplomski.dmaglica.service.VenueService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.reset
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
abstract class VenueServiceTest {

    @Mock
    protected lateinit var venueRepository: VenueRepository

    @Mock
    protected lateinit var venueRatingRepository: VenueRatingRepository

    @Mock
    protected lateinit var venueTypeRepository: VenueTypeRepository

    @Mock
    protected lateinit var imageService: ImageService

    @InjectMocks
    protected lateinit var venueService: VenueService

    @AfterEach
    fun tearDown() {
        reset(venueRepository, venueRatingRepository, venueTypeRepository)
    }

    protected val mockedVenue = VenueEntity().also {
        it.id = 1
        it.name = "Test Venue"
        it.location = "Test Location"
        it.description = "Test Description"
        it.workingHours = "9 AM - 5 PM"
        it.averageRating = 0.0
        it.venueTypeId = 1
    }

    protected val mockedRating = VenueRatingEntity().also {
        it.id = 1
        it.venueId = mockedVenue.id
        it.rating = 4.0
    }

}