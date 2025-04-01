package fipu.diplomski.dmaglica.repo

import fipu.diplomski.dmaglica.repo.entity.VenueTypeEntity
import org.springframework.data.jpa.repository.JpaRepository

interface VenueTypeRepository : JpaRepository<VenueTypeEntity, Int>