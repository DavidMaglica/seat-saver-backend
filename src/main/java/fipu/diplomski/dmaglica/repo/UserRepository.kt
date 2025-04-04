package fipu.diplomski.dmaglica.repo

import fipu.diplomski.dmaglica.repo.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<UserEntity, Int> {
    fun findByEmail(email: String): UserEntity?
    fun getByEmail(email: String): UserEntity?
}
