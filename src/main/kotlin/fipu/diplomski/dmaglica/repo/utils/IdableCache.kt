package fipu.diplomski.dmaglica.repo.utils

interface IdableCache<T> : Cache<T, Int> {

    override fun findById(id: Int): T?
}