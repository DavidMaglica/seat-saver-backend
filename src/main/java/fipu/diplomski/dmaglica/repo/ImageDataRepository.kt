package fipu.diplomski.dmaglica.repo

import fipu.diplomski.dmaglica.repo.entity.ImageDataEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ImageDataRepository : JpaRepository<ImageDataEntity, Int> {
    fun findByVenueId(venueId: Int): List<ImageDataEntity>
}