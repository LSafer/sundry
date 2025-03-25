package net.lsafer.sundry.storage

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement

class SimpleDataStore<T : Any>(initial: T) {
    val data = MutableStateFlow(initial)
}

fun SimpleDataStore<JsonObject>.edit(
    block: (MutableMap<String, JsonElement>) -> Unit,
) = data.update { JsonObject(buildMap { putAll(it); block(this) }) }

inline fun <reified T : Any> SimpleDataStore<JsonObject>.select(key: String, json: Json = Json): Flow<T?> {
    return data.map {
        val value = it[key] ?: return@map null
        safeSerialTryOrNull { json.decodeFromJsonElement<T>(value) }
    }
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
