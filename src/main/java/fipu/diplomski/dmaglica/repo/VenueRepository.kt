package fipu.diplomski.dmaglica.repo

import fipu.diplomski.dmaglica.repo.entity.VenueEntity
import org.springframework.data.jpa.repository.JpaRepository

interface VenueRepository : JpaRepository<VenueEntity, Int>
