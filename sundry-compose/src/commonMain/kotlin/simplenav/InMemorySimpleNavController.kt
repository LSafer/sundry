package net.lsafer.sundry.compose.simplenav

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

inline fun <reified T : Any> InMemorySimpleNavController() =
    InMemorySimpleNavController<T>(serializer())

class InMemorySimpleNavController<T : Any>(
    override val stateSerializer: KSerializer<State<T>>,
) : SimpleNavController<T> {
    @Serializable
    data class State<T : Any>(
        val entries: List<T> = emptyList(),
        val position: Int = entries.size - 1,
    ) : SimpleNavController.State<T> {
        override val route: T? get() = entries.getOrNull(position)
    }

    override val state = MutableStateFlow(State<T>())

    override fun push(route: T): Boolean {
        this.state.update {
            if (route == it.route) return false
            State(
                entries = it.entries.take(it.position + 1) + route,
                position = it.position + 1,
            )
        }
        return true
    }

    override fun back(): Boolean {
        this.state.update {
            if (it.position <= 0) return false
            it.copy(position = it.position - 1)
        }
        return true
    }

    override fun forward(): Boolean {
        this.state.update {
            if (it.position >= it.entries.lastIndex) return false
            it.copy(position = it.position + 1)
        }
        return true
    }
}
