# Sundry (Various) Utilities [![](https://jitpack.io/v/net.lsafer/sundry.svg)](https://jitpack.io/#net.lsafer/sundry)

Utilities accumulated while using `kotlin-multiplatform` that can't be in a library on itself.

### Deprecation List

- Moved `sundry-compose-table` to https://github.com/lsafer/compose-table
- Moved `net.lsafer.sundry.compose.simplenav` to https://github.com/lsafer/compose-simplenav

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
