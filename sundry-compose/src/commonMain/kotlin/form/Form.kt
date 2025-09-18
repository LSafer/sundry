package net.lsafer.sundry.compose.form

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf

abstract class Form {
    private val _fields = mutableStateListOf<FormField<*>>()

    val fields: List<FormField<*>> get() = _fields

    protected fun <T> field(
        defaultValue: T,
        onValidate: ValidateScope.(T) -> Unit = { },
    ): SingleFormField<T> {
        return SingleFormField(this, defaultValue, onValidate)
            .also { _fields += it }
    }

    protected fun <E> fieldList(
        defaultValue: List<E> = emptyList(),
        onValidate: ValidateScope.(List<E>) -> Unit = { },
    ): ListFormField<E> {
        return ListFormField(this, defaultValue, onValidate)
            .also { _fields += it }
    }

    protected fun <E> fieldSet(
        defaultValue: Set<E> = emptySet(),
        onValidate: ValidateScope.(Set<E>) -> Unit = { },
    ): SetFormField<E> {
        return SetFormField(this, defaultValue, onValidate)
            .also { _fields += it }
    }

    protected fun <K, V> fieldMap(
        defaultValue: Map<K, V> = emptyMap(),
        onValidate: ValidateScope.(Map<K, V>) -> Unit = { },
    ): MapFormField<K, V> {
        return MapFormField(this, defaultValue, onValidate)
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
