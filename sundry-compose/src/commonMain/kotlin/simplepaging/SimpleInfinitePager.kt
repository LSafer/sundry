package net.lsafer.sundry.compose.simplepaging

import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SimpleInfinitePager<K : Any, T : Any>(
    private val batchSize: Int,
    private val coroutineScope: CoroutineScope,
    private val fetcher: Fetcher<K, T>,
) {
    fun interface Fetcher<K : Any, T : Any> {
        suspend fun fetch(query: BatchQuery<K>): Result<BatchResult<K, T>>
    }

    data class BatchQuery<K : Any>(
        val key: K?,
        val limit: Int,
    )

    data class BatchResult<K : Any, T : Any>(
        val items: List<T> = emptyList(),
        val nextKey: K? = null,
        val remaining: Long? = null,
    )

    private val mtx = Mutex()

    private val values = mutableStateListOf<T>()
    private var nextKey: K? by mutableStateOf(null)

    var error by mutableStateOf<Throwable?>(null)
        private set
    var remaining by mutableStateOf<Long?>(null)
        private set
    var isLoading by mutableStateOf(false)
        private set

    val valueCount get() = values.size
    val itemCount by derivedStateOf {
        values.size + (remaining?.toInt()?.coerceAtMost(batchSize) ?: batchSize)
    }

    fun refresh() {
        coroutineScope.launch {
            mtx.withLock {
                values.clear()
                nextKey = null
                error = null
                remaining = null
            }
        }
    }

    fun retry() {
        coroutineScope.launch {
            mtx.withLock {
                error = null
            }
        }
    }

    operator fun get(index: Int): T? {
        if (
            values.size > index ||
            isLoading ||
            remaining == 0L ||
            error != null
        ) return values.getOrNull(index)

        coroutineScope.launch {
            mtx.withLock {
                if (
                    values.size > index ||
                    remaining == 0L ||
                    error != null
                ) return@withLock

                val query = BatchQuery(
                    key = nextKey,
                    limit = batchSize,
                )

                try {
                    isLoading = true
                    fetcher.fetch(query).fold(
                        onSuccess = { batch ->
                            values += batch.items
                            nextKey = batch.nextKey
                            remaining = if (batch.nextKey == null) 0L
                            else batch.remaining ?: remaining?.let { it - batch.items.size }
                        },
                        onFailure = { throwable ->
                            error = throwable
                        },
                    )
                } finally {
                    isLoading = false
                }
            }
        }
        return null
    }
}
