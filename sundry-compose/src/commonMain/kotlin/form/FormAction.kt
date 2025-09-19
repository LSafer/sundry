package net.lsafer.sundry.compose.form

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import net.lsafer.sundry.compose.util.platformIODispatcher

class FormAction(
    private val coroutineScope: CoroutineScope,
    private val condition: () -> Boolean = { true },
    private val block: suspend () -> Unit,
) {
    var loadingCount by mutableStateOf(0)
        private set
    val isLoading get() = loadingCount > 0
    val isEnabled get() = !isLoading && condition()

    operator fun invoke() {
        coroutineScope.launch {
            if (!condition())
                return@launch

            try {
                loadingCount++
                block()
            } finally {
                loadingCount--
            }
        }
    }
}

context(vm: ViewModel)
fun FormAction(condition: () -> Boolean = { true }, block: suspend () -> Unit) =
    FormAction(vm.viewModelScope + platformIODispatcher, condition, block)
