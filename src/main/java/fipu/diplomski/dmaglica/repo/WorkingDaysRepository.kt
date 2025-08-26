package fipu.diplomski.dmaglica.repo

import fipu.diplomski.dmaglica.repo.entity.WorkingDaysEntity
import org.springframework.data.jpa.repository.JpaRepository

interface WorkingDaysRepository : JpaRepository<WorkingDaysEntity, Int> {
    fun findAllByVenueId(venueId: Int): List<WorkingDaysEntity>
    fun findAllByVenueIdIn(venueIds: List<Int>): List<WorkingDaysEntity>
}