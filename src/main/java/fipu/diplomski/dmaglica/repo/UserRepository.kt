package fipu.diplomski.dmaglica.repo

import fipu.diplomski.dmaglica.repo.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<UserEntity, Long> {
    fun findByEmail(email: String): UserEntity?
    fun id(id: Long): MutableList<UserEntity>
}