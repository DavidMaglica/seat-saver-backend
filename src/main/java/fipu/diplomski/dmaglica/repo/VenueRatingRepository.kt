package fipu.diplomski.dmaglica.repo

import fipu.diplomski.dmaglica.repo.entity.VenueRatingEntity
import org.springframework.data.jpa.repository.JpaRepository

interface VenueRatingRepository : JpaRepository<VenueRatingEntity, Int> {
    fun findByVenueId(venueId: Int): List<VenueRatingEntity>
}
