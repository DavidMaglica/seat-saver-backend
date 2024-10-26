package fipu.diplomski.dmaglica.repo.utils

import java.io.Serializable

interface Cache<T, ID : Serializable> {
    fun findById(id: ID): T?
    fun findAll(): List<T>
    fun findAll(ids: Iterable<ID>): List<T>
    fun count(): Long
}