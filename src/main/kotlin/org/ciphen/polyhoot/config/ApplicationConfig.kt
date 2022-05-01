package org.ciphen.polyhoot.config

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default

class ApplicationConfig(args: Array<String>) {
    private val parser = ArgParser("polyhoot")
    val port by parser.option(ArgType.Int, "port", "p", "Port server should listen to").default(8080)

    init {
        parser.parse(args)
    }
}