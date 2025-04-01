package fipu.diplomski.dmaglica.util

import java.sql.SQLException

inline fun <reified T> dbActionWithTryCatch(errorMessage: String, block: () -> T): T = try {
    block()
} catch (e: Exception) {
    throw SQLException(errorMessage, e)
}
