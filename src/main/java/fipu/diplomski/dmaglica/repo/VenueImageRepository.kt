package fipu.diplomski.dmaglica.repo

import fipu.diplomski.dmaglica.repo.entity.VenueImageEntity
import org.springframework.data.jpa.repository.JpaRepository

interface VenueImageRepository : JpaRepository<VenueImageEntity, Int> {
    fun findByVenueId(venueId: Int): List<VenueImageEntity>
}
