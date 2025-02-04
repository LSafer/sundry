package net.lsafer.sundry.storage

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

suspend fun createFileJsonObjectDataStore(
    file: File,
    coroutineScope: CoroutineScope,
): SimpleDataStore<JsonObject> {
    val initial = readFromFile(file)
        ?: JsonObject(emptyMap())

    val dataStore = SimpleDataStore(initial)

    coroutineScope.launch {
        dataStore.data.collect {
            writeToFile(file, it)
        }
    }

    return dataStore
}

private suspend fun readFromFile(file: File): JsonObject? {
    val text = try {
        withContext(Dispatchers.IO) {
            file.readText()
        }
    } catch (_: FileNotFoundException) {
        return null
    } catch (e: IOException) {
        e.printStackTrace()
        return null
    }
    return try {
        Json.parseToJsonElement(text)
                as? JsonObject
    } catch (e: SerializationException) {
        e.printStackTrace()
        return null
    }
}

private suspend fun writeToFile(file: File, value: JsonObject) {
    val text = Json.encodeToString(value)

    try {
        withContext(Dispatchers.IO) {
            file.parentFile?.mkdirs()
            file.writeText(text)
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}
