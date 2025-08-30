package net.lsafer.sundry.storage

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.SerializationException

class SimpleDataStore<T : Any>(initial: T) {
    val data = MutableStateFlow(initial)
}

@PublishedApi
internal inline fun <R> safeSerialTryOrNull(action: () -> R): R? {
    return try {
        action()
    } catch (_: SerializationException) {
        null
    } catch (_: IllegalArgumentException) {
        null
    }
}
