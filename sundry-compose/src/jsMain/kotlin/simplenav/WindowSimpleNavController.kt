package net.lsafer.sundry.compose.simplenav

import kotlinx.browser.window
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

inline fun <reified T : Any> WindowSimpleNavController() =
    WindowSimpleNavController<T>(serializer())

class WindowSimpleNavController<T : Any>(
    override val stateSerializer: KSerializer<State<T>>,
) : SimpleNavController<T> {
    @Serializable
    data class State<T : Any>(
        override val current: T? = null,
    ) : SimpleNavController.State<T>

    var isInstalled: Boolean = false
        internal set

    override val state = MutableStateFlow(State<T>())

    override fun push(value: T): Boolean {
        require(isInstalled) { "NavController not installed" }
        this.state.update {
            if (value == it.current) return false
            State(current = value)
        }
        return true
    }

    override fun back(): Boolean {
        require(isInstalled) { "NavController not installed" }
        window.history.back()
        return true
    }

    override fun forward(): Boolean {
        require(isInstalled) { "NavController not installed" }
        window.history.forward()
        return true
    }
}
