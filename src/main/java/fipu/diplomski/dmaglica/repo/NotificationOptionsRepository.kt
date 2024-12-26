package fipu.diplomski.dmaglica.repo

import fipu.diplomski.dmaglica.repo.entity.NotificationOptionsEntity
import org.springframework.data.jpa.repository.JpaRepository

interface NotificationOptionsRepository : JpaRepository<NotificationOptionsEntity, Long> {
    fun findByUserId(userId: Long): NotificationOptionsEntity
}