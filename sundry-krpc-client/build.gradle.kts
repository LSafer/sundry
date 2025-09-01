plugins {
    `utility-conventions`
}

kotlin {
    sourceSets.commonMain.dependencies {
        implementation(libs.kotlinx.serialization.json)
        implementation(libs.kotlinx.coroutines.core)

        implementation(libs.kotlinx.rpc.core)
        implementation(libs.kotlinx.rpc.krpc.core)
        implementation(libs.kotlinx.rpc.krpc.serialization.json)
        implementation(libs.kotlinx.rpc.krpc.client)
        implementation(libs.kotlinx.rpc.krpc.ktor.client)

        implementation(ktorLibs.client.core)
    }
}
