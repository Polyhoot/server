plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("application")
    id("com.github.johnrengelman.shadow")
}

group = "org.ciphen.polyhoot"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
    implementation("io.ktor:ktor-client-java-jvm:2.0.2")
    implementation("io.ktor:ktor-client-websockets-jvm:2.0.2")
}

application {
    mainClass.set("org.ciphen.polyhoot.mockhost.MockHostKt")
}

tasks.shadowJar {
    archiveBaseName.set("mockhost")
    archiveClassifier.set("")
    archiveVersion.set("")
    minimize()
}

apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
