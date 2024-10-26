package fipu.diplomski.dmaglica.repo

import fipu.diplomski.dmaglica.model.NotificationOptions
import fipu.diplomski.dmaglica.repo.utils.IdableCache

interface NotificationOptionsRepository : IdableCache<NotificationOptions> {
    fun findByUserId(userId: Int): NotificationOptions?
}