package net.lsafer.sundry.compose.form

import androidx.compose.runtime.*
import androidx.compose.ui.focus.FocusRequester

sealed class FormField<T>(
    /**
     * The default value of the field for any entity.
     */
    val defaultValue: T,
    private val onValidate: ValidateScope.(T) -> Unit
) {
    /**
     * The form this field belongs/bound to.
     */
    lateinit var form: Form
        internal set

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

    /**
     * True, when the user changed the current value from the latest one.
     *
     * > UI logic should ignore this when [Form.isDraft] is true.
     */
    val isDirty by derivedStateOf { value != latestValue }

    private val _isValidValidationScope = ValidateScope() // this is to prevent gc

    /**
     * True, if [value] is valid according to field validation logic.
     *
     * > This is decoupled from [validate] and will respond to every value change.
     * > Field UI should use [error] instead.
     */
    val isValid by derivedStateOf {
        onValidate(_isValidValidationScope, value)
        val isValid = _isValidValidationScope.error == null
        _isValidValidationScope.error = null
        isValid
    }

    /**
     * The focus requester set to the UI component.
     */
    val focus = FocusRequester()

    val index by derivedStateOf { form.fields.indexOf(this) }
    val previous by derivedStateOf { form.fields.getOrNull(index - 1) }
    val next by derivedStateOf { form.fields.getOrNull(index + 1) }

    protected abstract fun setValue0(newValue: T)

    /**
     * Change field value to default one.
     */
    fun clear() {
        error = null
        setValue0(defaultValue)
    }

    /**
     * Change field value to latest one.
     */
    fun reset() {
        error = null
        setValue0(latestValue)
    }

    /**
     * Update latest field value to [newValue].
     */
    fun update(newValue: T) {
        error = null
        latestValue = newValue
        setValue0(newValue)
    }

    /**
     * Run validation. This should be invoked when field loses focus.
     *
     * > This is to populate [error] with validation error and NOT for [isValid].
     */
    fun validate() {
        // No need to cache scope object here, this is only invoked on focus loss.
        val scope = ValidateScope()
        scope.apply { onValidate(value) }
        error = scope.error
    }
}

class SingleFormField<T>(
    defaultValue: T,
    onValidate: ValidateScope.(T) -> Unit = { },
) : FormField<T>(defaultValue, onValidate) {
    override var value by mutableStateOf(defaultValue)

    override fun setValue0(newValue: T) {
        value = newValue
    }
}

class MapFormField<K, V>(
    defaultValue: Map<K, V>,
    onValidate: ValidateScope.(Map<K, V>) -> Unit = { },
) : FormField<Map<K, V>>(defaultValue, onValidate) {
    override val value = mutableStateMapOf<K, V>().also { it.putAll(defaultValue) }

    override fun setValue0(newValue: Map<K, V>) {
        value.clear()
        value.putAll(defaultValue)
    }
}

class ListFormField<E>(
    defaultValue: List<E>,
    onValidate: ValidateScope.(List<E>) -> Unit = { },
) : FormField<List<E>>(defaultValue, onValidate) {
    override val value = mutableStateListOf<E>().also { it.addAll(defaultValue) }

    override fun setValue0(newValue: List<E>) {
        value.clear()
        value.addAll(defaultValue)
    }
}

class SetFormField<E>(
    defaultValue: Set<E>,
    onValidate: ValidateScope.(Set<E>) -> Unit = { },
) : FormField<Set<E>>(defaultValue, onValidate) {
    override val value = mutableStateSetOf<E>().also { it.addAll(defaultValue) }

    override fun setValue0(newValue: Set<E>) {
        value.clear()
        value.addAll(defaultValue)
    }
}
