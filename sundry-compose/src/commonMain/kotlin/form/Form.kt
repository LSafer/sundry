package net.lsafer.sundry.compose.form

import androidx.compose.runtime.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

abstract class Form {
    private val _fields = mutableStateListOf<FormField<*>>()

    val fields: List<FormField<*>> get() = _fields

    protected fun group(vararg fields: FormField<*>): FieldGroup {
        val group = FieldGroup(fields.toList())
        group.form = this
        return group
    }

    protected operator fun <T, S : FormField<T>> (() -> S).provideDelegate(
        thisRef: Form,
        property: KProperty<*>
    ): ReadOnlyProperty<Form, S> {
        val field = this()
        field.form = this@Form
        _fields.add(field)
        return ReadOnlyProperty { _, _ -> field }
    }

    /**
     * True, indicating that the form is for creating an entity.
     *
     * Field UI Logic should ignore `field.isDirty` when this is true.
     */
    var isDraft by mutableStateOf(false)

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
