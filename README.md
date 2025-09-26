# Sundry (Various) Utilities [![](https://jitpack.io/v/net.lsafer/sundry.svg)](https://jitpack.io/#net.lsafer/sundry)

Utilities accumulated while using `kotlin-multiplatform` that can't be in a library on itself.

### Deprecation Notice

Here is why this library was deprecated.

- `:sundry-storage` (Can be moved to app layer with repository pattern)
- `platformIODispatcher` (Just use `Dispatchers.Default`, most libraries will switch to `Dispatchers.IO` when blocking)
- `Modifier.customShadow(...)` (CMP already supports it)
- `Modifier.shimming(...)` (Can be moved to app layer with Copy-Paste)
- `viewModelRef { ... }` (it had a faulty implementation)
- `SimpleInfinitePager` (I don't use it. I don't want to maintain it. It is not stable)
- `DatePickerDialogCompat(...)` (can be moved to app layer with Copy-Paste)
- `LoadingButton(...)` (can be moved to app layer with Copy-Paste)
- `LoadingTextButton(...)` (can be moved to app layer with Copy-Paste)
- Utils in `flow.kt` (Can be moved to app layer with Copy-Paste)
- Constants `Height` and `Width` (Can be moved to app layer with Copy-Paste)
- `currentWindowDpSize()` (CMP already supports it)
- `AdaptiveVerticalGrid` (I don't use it. I don't want to maintain it. It is not stable)
- Components in `ThreePaneLayout.kt` (CMP already supports it)

### Migration List

The following features are useful thus was moved to independent repositories:

- Moved `sundry-compose-table` to https://github.com/lsafer/compose-table
- Moved `net.lsafer.sundry.compose.simplenav` to https://github.com/lsafer/compose-simplenav
- Moved `net.lsafer.sundry.compose.form` to https://github.com/lsafer/compose-simpleform

### Install

The main way of installing this library is
using `jitpack.io`

```kts
repositories {
    // ...
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    // Replace MODULE and TAG with the desired module and version
    implementation("net.lsafer.sundry:sundry-MODULE:TAG")
}
```
