package fipu.diplomski.dmaglica.repo

import fipu.diplomski.dmaglica.repo.entity.NotificationOptions
import org.springframework.data.jpa.repository.JpaRepository

interface NotificationOptionsRepository : JpaRepository<NotificationOptions, Long> {
    fun findByUserId(userId: Long): NotificationOptions
}