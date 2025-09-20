package net.lsafer.sundry.compose.form

import androidx.compose.runtime.*
import androidx.compose.ui.focus.FocusRequester

sealed class FormField<T>(
    private val onValidate: ValidateScope.(T) -> Unit
) {
    /**
     * The form this field belongs/bound to.
     */
    abstract val form: Form

    /**
     * The default value of the field for any entity.
     */
    abstract val defaultValue: T

    /**
     * The current value of the field. Mutated by the user.
     */
    abstract val value: T

    /**
     * The actual value the entity is currently having.
     *
     * > Can be changed via [update]
     */
    var latestValue by mutableStateOf(defaultValue)
        protected set

    /**
     * The current validation or api error of the field.
     *
     * > Can be changed directly.
     */
    var error by mutableStateOf<String?>(null)
        protected set

    /**
     * True, when the user changed the current value from the latest one.
     *
     * > UI logic should ignore this when [Form.isDraft] is true.
     */
    val isDirty by derivedStateOf { value != latestValue }

    /**
     * True, when validation failed.
     *
     * > Invoke [validate] to run validation.
     */
    var isValid by mutableStateOf(false)
        protected set

    /**
     * The focus requester set to the UI component.
     */
    val focus = FocusRequester()

    val index by derivedStateOf { form.fields.indexOf(this) }
    val previous by derivedStateOf { form.fields.getOrNull(index - 1) }
    val next by derivedStateOf { form.fields.getOrNull(index + 1) }

    protected abstract fun setValue(newValue: T)

    /**
     * Change field value to default one.
     */
    fun clear() {
        isValid = true
        error = null
        setValue(defaultValue)
    }

    /**
     * Change field value to latest one.
     */
    fun reset() {
        isValid = true
        error = null
        setValue(latestValue)
    }

    /**
     * Update latest field value to [newValue].
     */
    fun update(newValue: T) {
        isValid = true
        error = null
        latestValue = newValue
        setValue(newValue)
    }

    /**
     * Run validation. This should be invoked when field loses focus.
     */
    fun validate() {
        val scope = ValidateScope()
        scope.apply { onValidate(value) }
        if (error != null) isValid = false
        error = scope.error
    }
}

class SingleFormField<T> internal constructor(
    override val form: Form,
    override val defaultValue: T,
    onValidate: ValidateScope.(T) -> Unit = { },
) : FormField<T>(onValidate) {
    override var value by mutableStateOf(defaultValue)

    override fun setValue(newValue: T) {
        value = newValue
    }
}

class MapFormField<K, V> internal constructor(
    override val form: Form,
    override val defaultValue: Map<K, V>,
    onValidate: ValidateScope.(Map<K, V>) -> Unit = { },
) : FormField<Map<K, V>>(onValidate) {
    override val value = mutableStateMapOf<K, V>().also { it.putAll(defaultValue) }

    override fun setValue(newValue: Map<K, V>) {
        value.clear()
        value.putAll(defaultValue)
    }
}

class ListFormField<E> internal constructor(
    override val form: Form,
    override val defaultValue: List<E>,
    onValidate: ValidateScope.(List<E>) -> Unit = { },
) : FormField<List<E>>(onValidate) {
    override val value = mutableStateListOf<E>().also { it.addAll(defaultValue) }

    override fun setValue(newValue: List<E>) {
        value.clear()
        value.addAll(defaultValue)
    }
}

class SetFormField<E> internal constructor(
    override val form: Form,
    override val defaultValue: Set<E>,
    onValidate: ValidateScope.(Set<E>) -> Unit = { },
) : FormField<Set<E>>(onValidate) {
    override val value = mutableStateSetOf<E>().also { it.addAll(defaultValue) }

    override fun setValue(newValue: Set<E>) {
        value.clear()
        value.addAll(defaultValue)
    }
}
