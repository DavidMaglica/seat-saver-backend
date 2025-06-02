package fipu.diplomski.dmaglica.repo

import fipu.diplomski.dmaglica.repo.entity.ReservationEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface ReservationRepository : JpaRepository<ReservationEntity, Int> {
    fun findByUserId(userId: Int): List<ReservationEntity>
    fun findByVenueIdAndDatetimeIn(venueId: Int, datetimes: List<LocalDateTime>): List<ReservationEntity>
    fun findByDatetimeIn(datetimes: List<LocalDateTime>): List<ReservationEntity>
}
