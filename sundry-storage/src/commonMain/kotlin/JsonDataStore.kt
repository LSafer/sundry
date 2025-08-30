package net.lsafer.sundry.storage

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement

typealias JsonDataStore = SimpleDataStore<JsonObject>

fun JsonDataStore.edit(block: (MutableMap<String, JsonElement>) -> Unit) =
    data.update { JsonObject(buildMap { putAll(it); block(this) }) }

inline fun <reified T : Any> JsonDataStore.select(key: String, json: Json = Json): Flow<T?> {
    return data.map {
        val value = it[key] ?: return@map null
        safeSerialTryOrNull { json.decodeFromJsonElement<T>(value) }
    }
}
