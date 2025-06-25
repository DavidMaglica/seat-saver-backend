package fipu.diplomski.dmaglica.repo

import fipu.diplomski.dmaglica.repo.entity.VenueEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface VenueRepository : JpaRepository<VenueEntity, Int> {
    override fun findAll(pageable: Pageable): Page<VenueEntity>
    fun findByLocation(location: String, pageable: Pageable): Page<VenueEntity>
    fun findByLocationIn(locations: List<String>, pageable: Pageable): Page<VenueEntity>

    @Query(
        """
        SELECT v FROM VenueEntity v
        WHERE v.averageRating > 4.0 AND v.availableCapacity > 0
        ORDER BY v.id DESC, v.averageRating DESC, v.availableCapacity DESC
    """
    )
    fun findSuggestedVenues(pageable: Pageable): Page<VenueEntity>

    @Query(
        """
        SELECT v FROM VenueEntity v
        WHERE (:searchQuery IS NULL OR LOWER(v.name) LIKE LOWER(CONCAT('%', :searchQuery, '%')))
        AND (:typeIds IS NULL OR v.venueTypeId IN :typeIds)
        ORDER BY v.name ASC
    """
    )
    fun findFilteredVenues(
        searchQuery: String?,
        typeIds: List<Int>?,
        pageable: Pageable
    ): Page<VenueEntity>
}
