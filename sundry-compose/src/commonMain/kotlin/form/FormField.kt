package net.lsafer.sundry.compose.form

import androidx.compose.runtime.*
import androidx.compose.ui.focus.FocusRequester

sealed class FormField<T> {
    abstract val form: Form
    abstract val defaultValue: T
    abstract val value: T

    var latestValue by mutableStateOf(defaultValue)
        protected set
    var error by mutableStateOf<String?>(null)
        protected set

    val isDirty by derivedStateOf { value != latestValue }
    val isValid by derivedStateOf { error == null }

    val focus = FocusRequester()

    val index by derivedStateOf { form.fields.indexOf(this) }
    val previous by derivedStateOf { form.fields.getOrNull(index - 1) }
    val next by derivedStateOf { form.fields.getOrNull(index + 1) }

    abstract fun clear()
    abstract fun reset()
    abstract fun update(newValue: T)
    abstract fun validate()
}

class SingleFormField<T> internal constructor(
    override val form: Form,
    override val defaultValue: T,
    private val onValidate: ValidateScope.(T) -> Unit = { },
) : FormField<T>() {
    override var value by mutableStateOf(defaultValue)

    override fun clear() {
        error = null
        value = defaultValue
    }

    override fun reset() {
        error = null
        value = latestValue
    }

    override fun update(newValue: T) {
        error = null
        latestValue = newValue
        value = newValue
    }

    override fun validate() {
        val scope = ValidateScope()
        scope.apply { onValidate(value) }
        error = scope.error
    }
}

class MapFormField<K, V> internal constructor(
    override val form: Form,
    override val defaultValue: Map<K, V>,
    private val onValidate: ValidateScope.(Map<K, V>) -> Unit = { },
) : FormField<Map<K, V>>() {
    override val value = mutableStateMapOf<K, V>().also { it.putAll(defaultValue) }

    override fun clear() {
        error = null
        value.clear()
        value.putAll(defaultValue)
    }

    override fun reset() {
        error = null
        value.clear()
        value.putAll(latestValue)
    }

    override fun update(newValue: Map<K, V>) {
        error = null
        latestValue = newValue
        value.clear()
        value.putAll(newValue)
    }

    override fun validate() {
        val scope = ValidateScope()
        scope.apply { onValidate(value) }
        error = scope.error
    }
}

class ListFormField<E> internal constructor(
    override val form: Form,
    override val defaultValue: List<E>,
    private val onValidate: ValidateScope.(List<E>) -> Unit = { },
) : FormField<List<E>>() {
    override val value = mutableStateListOf<E>().also { it.addAll(defaultValue) }

    override fun clear() {
        error = null
        value.clear()
        value.addAll(defaultValue)
    }

    override fun reset() {
        error = null
        value.clear()
        value.addAll(latestValue)
    }

    override fun update(newValue: List<E>) {
        error = null
        latestValue = newValue
        value.clear()
        value.addAll(newValue)
    }

    override fun validate() {
        val scope = ValidateScope()
        scope.apply { onValidate(value) }
        error = scope.error
    }
}

class SetFormField<E> internal constructor(
    override val form: Form,
    override val defaultValue: Set<E>,
    private val onValidate: ValidateScope.(Set<E>) -> Unit = { },
) : FormField<Set<E>>() {
    override val value = mutableStateSetOf<E>().also { it.addAll(defaultValue) }

    override fun clear() {
        error = null
        value.clear()
        value.addAll(defaultValue)
    }

    override fun reset() {
        error = null
        value.clear()
        value.addAll(latestValue)
    }

    override fun update(newValue: Set<E>) {
        error = null
        latestValue = newValue
        value.clear()
        value.addAll(newValue)
    }

    override fun validate() {
        val scope = ValidateScope()
        scope.apply { onValidate(value) }
        error = scope.error
    }
}
