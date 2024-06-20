plugins {
    kotlin("jvm") version "1.9.23"
}

group = "com.github.antoniomarques.somixol"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    // implementation(group = "", name = "", version = "")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}