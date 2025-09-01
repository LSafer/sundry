plugins {
    `compose-conventions`
}

kotlin {
    sourceSets.commonMain.dependencies {
        implementation(libs.androidx.lifecycle.viewmodel.compose)
        implementation(libs.kotlinx.serialization.json)
        implementation(libs.kotlinx.datetime)
    }
}

android {
    namespace = "net.lsafer.sundry.compose"
}
