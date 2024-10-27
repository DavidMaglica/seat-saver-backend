package fipu.diplomski.dmaglica.repo

import fipu.diplomski.dmaglica.repo.entity.Role
import org.springframework.data.jpa.repository.JpaRepository

interface RoleRepository : JpaRepository<Role, Long> {
    fun findAllByUserId(id: Long): List<Role>
}