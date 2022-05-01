package org.ciphen.polyhoot

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.ciphen.polyhoot.config.ApplicationConfig
import org.ciphen.polyhoot.services.WebSocket

class Application {
    companion object {
        private var INSTANCE: Application? = null
        fun getInstance(): Application {
            if (INSTANCE == null) {
                INSTANCE = Application()
            }
            return INSTANCE!!
        }

        @JvmStatic
        fun main(args: Array<String>) {
            getInstance().applicationConfig = ApplicationConfig(args)
            getInstance().onConfigLoaded()
        }
    }

    lateinit var applicationConfig: ApplicationConfig
    lateinit var ktorApplication: io.ktor.server.application.Application

    fun onConfigLoaded() {
        println("Port: ${applicationConfig.port}")
        embeddedServer(Netty, port = applicationConfig.port, host = "0.0.0.0") {
            ktorApplication = this
            WebSocket(this)
        }.start(wait = true)
    }
}

