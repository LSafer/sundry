package net.lsafer.sundry.compose.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

class ViewModelRefScope<T : Any> : ViewModel() {
    lateinit var value: T
        internal set
}

@Composable
fun <T : Any> viewModelRef(key: String, block: ViewModelRefScope<T>.() -> T): T {
    return viewModel(key = key) { ViewModelRefScope<T>().apply { value = block() } }.value
}
