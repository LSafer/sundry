plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.compose) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
}

group = "net.lsafer.sundry"

tasks.wrapper {
    gradleVersion = "8.9"
}

subprojects {
    version = rootProject.version
    group = rootProject.group
}
