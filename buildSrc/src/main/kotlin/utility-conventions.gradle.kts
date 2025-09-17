import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    `maven-publish`
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    jvmToolchain(17)
    jvm()
    js()
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs()
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
    }
    sourceSets {
        val commonMain by getting
        val webCommon by creating
        val jsMain by getting
        val wasmJsMain by getting
        webCommon.dependsOn(commonMain)
        jsMain.dependsOn(webCommon)
        wasmJsMain.dependsOn(webCommon)
    }
}
