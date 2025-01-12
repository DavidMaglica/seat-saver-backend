package fipu.diplomski.dmaglica.geolocation

import fipu.diplomski.dmaglica.service.GeolocationService
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.client.RestTemplate

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
abstract class AbstractGeolocationTest {

    @Mock
    protected lateinit var restTemplate: RestTemplate

    @InjectMocks
    protected lateinit var geolocationService: GeolocationService
}