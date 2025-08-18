package fipu.diplomski.dmaglica.repo

import fipu.diplomski.dmaglica.model.data.TrendingVenueProjection
import fipu.diplomski.dmaglica.repo.entity.ReservationEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime

interface ReservationRepository : JpaRepository<ReservationEntity, Int> {
    fun findByUserId(userId: Int): List<ReservationEntity>
    fun findByDatetimeBetween(first: LocalDateTime, second: LocalDateTime): List<ReservationEntity>
    fun findByVenueIdAndDatetimeBetween(
        venueId: Int,
        first: LocalDateTime,
        second: LocalDateTime
    ): List<ReservationEntity>
    fun findByVenueIdInAndDatetimeBetween(
        venueIds: List<Int>,
        first: LocalDateTime,
        second: LocalDateTime
    ): List<ReservationEntity>

    fun findByVenueIdIn(venueIds: List<Int>): List<ReservationEntity>
    fun countByVenueId(venueId: Int): Int
    fun countByVenueIdIn(venueIds: List<Int>): Int
    fun countByVenueIdInAndDatetimeBetween(venueIds: List<Int>, first: LocalDateTime, second: LocalDateTime): Int

    @Query(
        """
    SELECT r.venueId AS venueId, COUNT(r.id) AS reservationCount
    FROM ReservationEntity r
    GROUP BY venueId
    ORDER BY reservationCount DESC
    """
    )
    fun findTopVenuesByReservationCount(pageable: Pageable): Page<TrendingVenueProjection>
}
