package fipu.diplomski.dmaglica.mobile.venue

import fipu.diplomski.dmaglica.repo.VenueRatingRepository
import fipu.diplomski.dmaglica.repo.VenueRepository
import fipu.diplomski.dmaglica.repo.VenueTypeRepository
import fipu.diplomski.dmaglica.repo.entity.VenueEntity
import fipu.diplomski.dmaglica.repo.entity.VenueRatingEntity
import fipu.diplomski.dmaglica.service.ImageService
import fipu.diplomski.dmaglica.service.VenueService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.reset
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
abstract class BaseVenueServiceTest {

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

    protected val venueArgumentCaptor: ArgumentCaptor<VenueEntity> = ArgumentCaptor.forClass(VenueEntity::class.java)
    protected val venueRatingArgumentCaptor: ArgumentCaptor<VenueRatingEntity> =
        ArgumentCaptor.forClass(VenueRatingEntity::class.java)

    protected val mockedVenue = VenueEntity().apply {
        id = 1
        name = "Test Venue"
        location = "Test Location"
        description = "Test Description"
        workingHours = "9 AM - 5 PM"
        maximumCapacity = 100
        availableCapacity = 50
        averageRating = 0.0
        venueTypeId = 1
    }

    protected val mockedRating = VenueRatingEntity().apply {
        id = 1
        venueId = mockedVenue.id
        rating = 4.0
    }

}