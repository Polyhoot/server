package org.ciphen.polyhoot.services

import io.ktor.server.websocket.*
import java.time.Duration
import io.ktor.server.application.*
import org.ciphen.polyhoot.services.routes.Connect
import org.ciphen.polyhoot.services.routes.debug.Debug
import org.ciphen.polyhoot.services.routes.game.Create

class WebSocket(application: Application) {
    init {
        application.install(WebSockets) {
            pingPeriod = Duration.ofSeconds(15)
            timeout = Duration.ofSeconds(15)
            maxFrameSize = Long.MAX_VALUE
            masking = false
        }

        Connect()
        Create()
    }
}