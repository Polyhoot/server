package net.ciphen.polyhoot

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.*
import io.ktor.server.response.*
import net.ciphen.polyhoot.config.ApplicationConfig
import net.ciphen.polyhoot.routes.fileRouting
import net.ciphen.polyhoot.routes.packRouting
import net.ciphen.polyhoot.routes.userRouting
import net.ciphen.polyhoot.services.WebSocket
import net.ciphen.polyhoot.services.configureRouting
import net.ciphen.polyhoot.utils.Log
import org.slf4j.LoggerFactory

class Application {
    companion object {
        private var INSTANCE: Application? = null
        private const val TAG = "Application"
        fun getInstance(): Application {
            if (INSTANCE == null) {
                INSTANCE = Application()
            }
            return INSTANCE!!
        }

        @JvmStatic
        fun main(args: Array<String>) {
            Log.i(TAG, "Starting Polyhoot server!")
            if (!ApplicationConfig(args).also { getInstance().applicationConfig = it }.debug) {
                (LoggerFactory.getILoggerFactory() as LoggerContext).getLogger("org.mongodb.driver").level = Level.ERROR
            }
            Runtime.getRuntime().addShutdownHook(
                Thread {
                    getInstance().onDestroy()
                }
            )
            getInstance().onConfigLoaded()
        }
    }

    lateinit var applicationConfig: ApplicationConfig
    lateinit var ktorApplication: io.ktor.server.application.Application
    var serverUp = false

    fun onConfigLoaded() {
        Log.i(TAG, "Config loaded. Launching embedded server on localhost on port ${applicationConfig.port}.")
        embeddedServer(Netty, port = applicationConfig.port, host = "0.0.0.0") {
            ktorApplication = this
            WebSocket(this)
            install(ContentNegotiation) {
                json()
            }
            install(CORS) {
                anyHost()
                allowHeader(HttpHeaders.ContentType)
                allowHeader(HttpHeaders.Authorization)
            }
            install(Authentication) {
                jwt("auth-jwt") {
                    verifier(
                        JWT
                            .require(Algorithm.HMAC256(System.getenv("JWT_SECRET")))
                            .build()
                    )
                    validate { credential ->
                        if (credential.payload.getClaim("id").asString() != "") {
                            JWTPrincipal(credential.payload)
                        } else {
                            null
                        }
                    }
                    challenge { _, _ ->
                        call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
                    }
                }
            }
            configureRouting()
            userRouting()
            packRouting()
            fileRouting()
            serverUp = true
        }.start(wait = true)
    }

    fun onDestroy() {
        Log.onDestroy()
    }
}

