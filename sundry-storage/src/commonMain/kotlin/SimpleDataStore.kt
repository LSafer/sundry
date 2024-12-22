package net.lsafer.sundry.storage

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

class SimpleDataStore<T : Any>(initial: T) {
    val data = MutableStateFlow(initial)
}

fun MutableStateFlow<JsonObject>.edit(
    block: (MutableMap<String, JsonElement>) -> Unit,
) = update { JsonObject(buildMap { putAll(it); block(this) }) }
