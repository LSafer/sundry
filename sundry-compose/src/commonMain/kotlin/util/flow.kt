package net.lsafer.sundry.compose.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Composable
fun <T> SubscribeEffect(flow: Flow<T>, block: suspend (T) -> Unit) {
    LaunchedEffect(flow, block) {
        launch { flow.collect { block(it) } }
    }
}

fun <T> StateFlow<T>.collectAsStateIn(
    coroutineScope: CoroutineScope,
): State<T> {
    val state = mutableStateOf(value)
    onEach { state.value = it }.launchIn(coroutineScope)
    return state
}

fun <T : R, R> Flow<T>.collectAsStateIn(
    coroutineScope: CoroutineScope,
    initial: R,
): State<R> {
    val state = mutableStateOf(initial)
    onEach { state.value = it }.launchIn(coroutineScope)
    return state
}
