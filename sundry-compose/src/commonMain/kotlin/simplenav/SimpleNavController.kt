package net.lsafer.sundry.compose.simplenav

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.KSerializer

interface SimpleNavController<T : Any> {
    interface State<T : Any> {
        val current: T?
    }

    val stateSerializer: KSerializer<out State<T>>
    val state: MutableStateFlow<out State<T>>

    val current get() = state.value.current

    fun push(value: T): Boolean
    fun back(): Boolean
    fun forward(): Boolean
}
