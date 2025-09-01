import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    `maven-publish`
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.gradleup.patrouille)
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose)
}

compatPatrouille {
    java(libs.versions.java.get().toInt())
    kotlin(libs.versions.kotlin.get())
}

kotlin {
    jvm("desktop")
    js { browser() }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs { browser() }
    androidTarget()
    sourceSets {
        val commonMain by getting
        val jsMain by getting
        val wasmJsMain by getting
        val androidMain by getting
        val desktopMain by getting

        val webCommon by creating
        webCommon.dependsOn(commonMain)
        jsMain.dependsOn(webCommon)
        wasmJsMain.dependsOn(webCommon)

        val jvmCommon by creating
        jvmCommon.dependsOn(commonMain)
        androidMain.dependsOn(jvmCommon)
        desktopMain.dependsOn(jvmCommon)

        val nonAndroidCommon by creating
        nonAndroidCommon.dependsOn(commonMain)
        jsMain.dependsOn(nonAndroidCommon)
        wasmJsMain.dependsOn(nonAndroidCommon)
        desktopMain.dependsOn(nonAndroidCommon)
    }
    sourceSets.commonMain.dependencies {
        implementation(compose.runtime)
        implementation(compose.foundation)
        implementation(compose.ui)
        implementation(compose.material3)
    }
    sourceSets.named("webCommon").dependencies {
        implementation(libs.kotlinx.browser)
    }
    sourceSets.androidMain.dependencies {
        implementation(compose.preview)
    }
}

android {
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
    dependencies {
        debugImplementation(compose.uiTooling)
    }
}
