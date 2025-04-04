package fipu.diplomski.dmaglica.repo

import fipu.diplomski.dmaglica.repo.entity.MenuImageEntity
import org.springframework.data.jpa.repository.JpaRepository

interface MenuImageRepository : JpaRepository<MenuImageEntity, Int> {
    fun findByVenueId(venueId: Int): MenuImageEntity?
}
