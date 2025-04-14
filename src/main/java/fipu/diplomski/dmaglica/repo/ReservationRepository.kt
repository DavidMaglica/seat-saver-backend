package fipu.diplomski.dmaglica.repo

import fipu.diplomski.dmaglica.repo.entity.ReservationEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ReservationRepository : JpaRepository<ReservationEntity, Int>