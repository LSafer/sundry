rootProject.name = "lsafer-sundry"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    versionCatalogs {
        create("ktorLibs") {
            from("io.ktor:ktor-version-catalog:3.2.2")
        }
    }
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        maven("https://jitpack.io")
        mavenCentral()
    }
}

include(":sundry-compose")
include(":sundry-compose-adaptive")
include(":sundry-compose-table")
include(":sundry-storage")
