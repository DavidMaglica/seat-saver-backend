package fipu.diplomski.dmaglica.repo.utils

import fipu.diplomski.dmaglica.model.NotificationOptions
import fipu.diplomski.dmaglica.model.Role
import fipu.diplomski.dmaglica.repo.entity.NotificationOptions as NotificationOptionsEntity
import fipu.diplomski.dmaglica.repo.entity.Role as RoleEntity

fun List<RoleEntity>.convertToModel(): List<Role> = this.map { role -> Role.valueOf(role.role) }

fun NotificationOptionsEntity.convertToModel(): NotificationOptions = NotificationOptions(
    this.emailNotificationsTurnedOn,
    this.pushNotificationsTurnedOn,
    this.locationServicesTurnedOn
)