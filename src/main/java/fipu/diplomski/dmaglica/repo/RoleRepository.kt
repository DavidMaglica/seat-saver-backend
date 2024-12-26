package fipu.diplomski.dmaglica.repo

import fipu.diplomski.dmaglica.repo.entity.RoleEntity
import org.springframework.data.jpa.repository.JpaRepository

interface RoleRepository : JpaRepository<RoleEntity, Long> {
    fun findAllByUserId(id: Long): List<RoleEntity>
}