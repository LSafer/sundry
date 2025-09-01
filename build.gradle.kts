group = "net.lsafer.sundry"
version = "local"

tasks.wrapper {
    gradleVersion = "8.14"
}

subprojects {
    version = rootProject.version
    group = rootProject.group
}
