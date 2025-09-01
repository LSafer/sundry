plugins {
    `compose-conventions`
}

android {
    namespace = "net.lsafer.sundry.compose.table"
}

kotlin {
    sourceSets.commonMain.dependencies {
        implementation(compose.materialIconsExtended)
    }
}
