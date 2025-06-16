package fipu.diplomski.dmaglica.repo

import fipu.diplomski.dmaglica.repo.entity.VenueEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface VenueRepository : JpaRepository<VenueEntity, Int> {
    override fun findAll(pageable: Pageable): Page<VenueEntity>
    fun findByLocation(location: String): List<VenueEntity>
    fun findByLocationIn(locations: List<String>): List<VenueEntity>

    @Query(
        """
        SELECT v FROM VenueEntity v
        WHERE v.averageRating > 4.0 AND v.availableCapacity > 0
        ORDER BY v.id DESC, v.averageRating DESC, v.availableCapacity DESC
        LIMIT 20
    """
    )
    fun findSuggestedVenues(): List<VenueEntity>
}
