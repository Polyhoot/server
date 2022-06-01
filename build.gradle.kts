/*
 * Copyright (C) 2022 The Polyhoot Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

val kotlin_version: String by project
val logback_version: String by project

plugins {
    application
    kotlin("jvm") version "1.6.21"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.21"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "net.ciphen.polyhoot"
version = "one"

application {
    mainClass.set("net.ciphen.polyhoot.Application")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

tasks.shadowJar {
    archiveBaseName.set("polyhoot_server")
    archiveClassifier.set("")
    archiveVersion.set("")
    minimize {
        exclude(dependency("org.litote.kmongo:.*:.*"))
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:2.0.2")
    implementation("io.ktor:ktor-server-auth-jvm:2.0.2")
    implementation("io.ktor:ktor-server-host-common-jvm:2.0.2")
    implementation("io.ktor:ktor-server-status-pages-jvm:2.0.2")
    implementation("io.ktor:ktor-server-locations-jvm:2.0.2")
    implementation("io.ktor:ktor-server-websockets-jvm:2.0.2")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:2.0.2")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.0.2")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:2.0.2")
    implementation("io.ktor:ktor-serialization-gson-jvm:2.0.2")
    implementation("io.ktor:ktor-server-netty-jvm:2.0.2")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-auth:2.0.2")
    implementation("io.ktor:ktor-server-auth-jwt:2.0.2")
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
    implementation("org.litote.kmongo:kmongo-coroutine:4.5.1")
    implementation("org.mindrot:jbcrypt:0.4")
    implementation("io.ktor:ktor-server-cors:2.0.2")
    implementation("com.aventrix.jnanoid:jnanoid:2.0.0")
    testImplementation("de.bwaldvogel:mongo-java-server:1.40.0")
    testImplementation("io.ktor:ktor-server-tests-jvm:2.0.2")
    testImplementation("io.ktor:ktor-client-content-negotiation:2.0.2")
    testImplementation(platform("org.junit:junit-bom:5.8.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:$kotlin_version")
    testImplementation("io.ktor:ktor-client-java-jvm:2.0.2")
    testImplementation("io.ktor:ktor-client-websockets-jvm:2.0.2")
}

tasks.test {
    useJUnitPlatform()
}