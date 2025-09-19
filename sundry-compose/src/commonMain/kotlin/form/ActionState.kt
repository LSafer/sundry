package net.lsafer.sundry.compose.form

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.lsafer.sundry.compose.util.platformIODispatcher

class ActionState {
    private val mutex = Mutex()
    var isLoading by mutableStateOf(false)
        private set

    suspend fun use(block: suspend () -> Unit) {
        mutex.withLock {
            try {
                isLoading = true
                block()
            } finally {
                isLoading = false
            }
        }
    }
}

context(vm: ViewModel)
fun ActionState.useIO(block: suspend () -> Unit): Job {
    return vm.viewModelScope.launch(platformIODispatcher) { use(block) }
}
