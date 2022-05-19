val ktor_version: String by project

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.github.johnrengelman.shadow")
}

group = "org.ciphen.polyhoot"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-client-websockets:$ktor_version")
    implementation("io.ktor:ktor-client-java:$ktor_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
}

tasks.shadowJar {
    archiveBaseName.set("mockhost")
    archiveClassifier.set("")
    archiveVersion.set("")
    minimize()
}

apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
