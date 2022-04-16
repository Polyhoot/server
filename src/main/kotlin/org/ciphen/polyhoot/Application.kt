package org.ciphen.polyhoot

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import org.ciphen.polyhoot.config.ApplicationConfig
import org.ciphen.polyhoot.services.WebSocket
import org.ciphen.polyhoot.services.configureRouting
import org.litote.kmongo.KMongo

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
//        val client = KMongo.createClient()
//        val database = client.getDatabase("users")
        embeddedServer(Netty, port = applicationConfig.port, host = "0.0.0.0") {
            ktorApplication = this
            WebSocket(this)
            install(Authentication) {
                jwt {
                    var secret = applicationConfig.secret
                }
            }
            configureRouting()
        }.start(wait = true)
    }
}

