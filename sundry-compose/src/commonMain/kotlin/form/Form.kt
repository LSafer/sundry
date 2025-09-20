package net.lsafer.sundry.compose.form

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf

abstract class Form {
    private val _fields = mutableStateListOf<FormField<*>>()

    val fields: List<FormField<*>> get() = _fields

    protected fun group(vararg fields: FormField<*>) =
        FieldGroup(this, fields.toList())

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

    /**
     * True, indicating that the form is for creating an entity.
     *
     * Field UI Logic should ignore `field.isDirty` when this is true.
     */
    val isDraft by mutableStateOf(false)

    /**
     * A list of all error messages of fields.
     */
    val errors by derivedStateOf {
        _fields.mapNotNull { it.error }
    }

    /**
     * True, indicating that at least one field has changed from latest value.
     *
     * > UI logic should ignore this when [isDraft] is true.
     */
    val isDirty by derivedStateOf {
        _fields.fold(false) { result, field ->
            field.isDirty || result
        }
    }

    /**
     * True, indicating that all fields validation passed.
     */
    val isValid by derivedStateOf {
        _fields.fold(true) { result, field ->
            field.isValid && result
        }
    }

    /**
     * True, when the form is ready to be submitted.
     */
    val isSubmittable get() = isValid && (isDraft || isDirty)

    /**
     * Change all fields to their latest values.
     */
    fun reset() {
        _fields.forEach { it.reset() }
    }

    /**
     * Change all fields to their default values.
     */
    fun clear() {
        _fields.forEach { it.clear() }
    }

    /**
     * Run validation for all the fields.
     */
    fun validate() {
        _fields.forEach { it.validate() }
    }
}
