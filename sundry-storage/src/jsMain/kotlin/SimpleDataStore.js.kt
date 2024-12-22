package net.lsafer.sundry.storage

import kotlinx.browser.localStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import org.w3c.dom.get
import org.w3c.dom.set

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
    val text = localStorage[name] ?: return null
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
    localStorage[name] = text
}
