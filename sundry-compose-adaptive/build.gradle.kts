plugins {
    `compose-conventions`
}

kotlin {
    sourceSets.commonMain.dependencies {
        implementation(libs.material3.adaptive)
        implementation(libs.material3.adaptive.layout)
        implementation(libs.material3.adaptive.navigation)
    }
}

android {
    namespace = "net.lsafer.sundry.compose.adaptive"
}
