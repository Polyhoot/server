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