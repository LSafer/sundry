package net.lsafer.sundry.compose.form

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue

class FieldGroup internal constructor(
    val form: Form,
    val fields: List<FormField<*>>
) {
    val errors by derivedStateOf {
        fields.mapNotNull { it.error }
    }
    val isDirty by derivedStateOf {
        fields.fold(false) { result, field ->
            field.isDirty || result
        }
    }
    val isValid by derivedStateOf {
        fields.fold(true) { result, field ->
            field.isValid && result
        }
    }

    val isSubmittable get() = isValid && (form.isDraft || isDirty)

    fun reset() = fields.forEach { it.reset() }
    fun clear() = fields.forEach { it.clear() }
    fun validate() = fields.forEach { it.validate() }
}
