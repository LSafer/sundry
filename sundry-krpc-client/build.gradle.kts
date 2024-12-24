plugins {
    `maven-publish`
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    jvm()

    js {
        browser()
        useEsModules()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.coroutines.core)

            implementation(libs.kotlinx.rpc.core)
            implementation(libs.kotlinx.rpc.krpc.core)
            implementation(libs.kotlinx.rpc.krpc.serialization.json)
            implementation(libs.kotlinx.rpc.krpc.client)
            implementation(libs.kotlinx.rpc.krpc.ktor.client)

            implementation(libs.ktor.client.core)
        }
    }
}

afterEvaluate {
    publishing {
        publications {
            // Creates a Maven publication called "release".
            create<MavenPublication>("release") {
                // Applies the component for the release build variant.
                from(components["kotlin"])

                // You can then customize attributes of the publication as shown below.
                groupId = "net.lsafer"
                artifactId = "sundry-storage"
                version = project.version.toString()
            }
        }
    }
}
