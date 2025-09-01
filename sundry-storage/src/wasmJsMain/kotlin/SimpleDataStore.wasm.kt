package net.lsafer.sundry.storage

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

fun createLocalStorageJsonObjectDataStore(
    name: String,
    coroutineScope: CoroutineScope,
): SimpleDataStore<JsonObject> {
    val initial = readFromLocalStorage(name)
        ?: JsonObject(emptyMap())
    val dataStore = SimpleDataStore(initial)

    coroutineScope.launch {
        dataStore.data.collect {
            writeToLocalStorage(name, it)
        }
    }

    return dataStore
}

private fun readFromLocalStorage(name: String): JsonObject? {
    val text = localStorage_get(name) ?: return null
    return try {
        Json.parseToJsonElement(text)
                as? JsonObject
    } catch (e: SerializationException) {
        e.printStackTrace()
        return null
    }
}

private fun writeToLocalStorage(name: String, value: JsonObject) {
    val text = Json.encodeToString(value)
    localStorage_set(name, text)
}

@Suppress("FunctionName")
private fun localStorage_get(name: String): String? =
    js("localStorage.get(name)")

@Suppress("FunctionName")
private fun localStorage_set(name: String, value: String): Unit =
    js("localStorage.set(name, value)")
