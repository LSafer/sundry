package net.lsafer.sundry.compose.simplenav

import kotlinx.browser.window
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

inline fun <reified T : Any> WindowSimpleNavController(
    route: T? = null,
) = WindowSimpleNavController(WindowSimpleNavController.State(route))

inline fun <reified T : Any> WindowSimpleNavController(
    initialState: WindowSimpleNavController.State<T> =
        WindowSimpleNavController.State(),
) = WindowSimpleNavController(initialState, serializer())

class WindowSimpleNavController<T : Any>(
    initialState: State<T> = State(),
    override val stateSerializer: KSerializer<State<T>>,
) : SimpleNavController<T> {
    @Serializable
    data class State<T : Any>(
        override val route: T? = null,
    ) : SimpleNavController.State<T>

    var isInstalled: Boolean = false
        internal set

    override val state = MutableStateFlow(initialState)

    override fun push(route: T): Boolean {
        require(isInstalled) { "NavController not installed" }
        this.state.update {
            if (route == it.route) return false
            State(route = route)
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
