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
