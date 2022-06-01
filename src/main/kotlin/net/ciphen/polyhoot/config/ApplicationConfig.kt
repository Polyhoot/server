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

package net.ciphen.polyhoot.config

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default

class ApplicationConfig(args: Array<String>) {
    private val parser = ArgParser("polyhoot")
    val port by parser.option(ArgType.Int, "port", "p", "Port server should listen to").default(8080)
    val debug by parser.option(ArgType.Boolean, "debug", "d", "Debug mode").default(false)

    init {
        parser.parse(args)
    }
}