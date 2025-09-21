package net.lsafer.sundry.compose.form

typealias FormFieldSpec<T> = () -> FormField<T>
typealias SingleFormFieldSpec<T> = () -> SingleFormField<T>
typealias MapFormFieldSpec<K, V> = () -> MapFormField<K, V>
typealias ListFormFieldSpec<E> = () -> ListFormField<E>
typealias SetFormFieldSpec<E> = () -> SetFormField<E>

fun <T> field(
    defaultValue: T,
    onValidate: ValidateScope.(T) -> Unit = { },
): SingleFormFieldSpec<T> = {
    SingleFormField(defaultValue, onValidate)
}

fun <E> fieldList(
    defaultValue: List<E> = emptyList(),
    onValidate: ValidateScope.(List<E>) -> Unit = { },
): ListFormFieldSpec<E> = {
    ListFormField(defaultValue, onValidate)
}

fun <E> fieldSet(
    defaultValue: Set<E> = emptySet(),
    onValidate: ValidateScope.(Set<E>) -> Unit = { },
): SetFormFieldSpec<E> = {
    SetFormField(defaultValue, onValidate)
}

fun <K, V> fieldMap(
    defaultValue: Map<K, V> = emptyMap(),
    onValidate: ValidateScope.(Map<K, V>) -> Unit = { },
): MapFormFieldSpec<K, V> = {
    MapFormField(defaultValue, onValidate)
}
