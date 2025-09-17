package net.lsafer.sundry.compose.form

import androidx.compose.runtime.*
import androidx.compose.ui.focus.FocusRequester
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import net.lsafer.sundry.compose.util.platformIODispatcher

class ValidateScope(var error: String? = null) {
    fun rule(condition: Boolean, message: () -> String) {
        if (!condition) error = message()
    }
}

class FormField<T> internal constructor(
    val form: Form,
    val defaultValue: T,
    private val onValidate: ValidateScope.(T) -> Unit = { },
) {
    var latestValue by mutableStateOf(defaultValue)
    var value by mutableStateOf(defaultValue)
    var error by mutableStateOf<String?>(null)
        private set

    val isDirty by derivedStateOf { value != latestValue }
    val isValid by derivedStateOf { error == null }

    val focus = FocusRequester()

    val index by derivedStateOf { form.fields.indexOf(this) }
    val previous by derivedStateOf { form.fields.getOrNull(index - 1) }
    val next by derivedStateOf { form.fields.getOrNull(index + 1) }

    fun clear() {
        error = null
        value = defaultValue
    }

    fun reset() {
        error = null
        value = latestValue
    }

    fun update(newValue: T) {
        error = null
        latestValue = newValue
        value = newValue
    }

    fun validate() {
        val scope = ValidateScope()
        scope.apply { onValidate(value) }
        error = scope.error
    }
}

abstract class Form {
    private val _fields = mutableStateListOf<FormField<*>>()

    val fields: List<FormField<*>> get() = _fields

    protected fun <T> field(
        defaultValue: T,
        onValidate: ValidateScope.(T) -> Unit = { },
    ): FormField<T> {
        return FormField(this, defaultValue, onValidate)
            .also { _fields += it }
    }

    val errors by derivedStateOf {
        _fields.mapNotNull { it.error }
    }

    val isDirty by derivedStateOf {
        _fields.fold(false) { result, field ->
            field.isDirty || result
        }
    }

    val isValid by derivedStateOf {
        _fields.fold(true) { result, field ->
            field.isValid && result
        }
    }

    val isSubmittable get() = isDirty && isValid

    fun reset() {
        _fields.forEach { it.reset() }
    }

    fun clear() {
        _fields.forEach { it.clear() }
    }
}

class FormAction(
    private val coroutineScope: CoroutineScope,
    private val condition: () -> Boolean = { true },
    private val block: suspend () -> Unit,
) {
    var loadingCount by mutableStateOf(0)
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
