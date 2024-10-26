package fipu.diplomski.dmaglica.repo

import fipu.diplomski.dmaglica.repo.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
}