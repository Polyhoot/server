package org.ciphen.polyhoot.services

import io.ktor.server.websocket.*
import java.time.Duration
import io.ktor.server.application.*
import org.ciphen.polyhoot.services.routes.Connect
import org.ciphen.polyhoot.services.routes.game.Create
import org.ciphen.polyhoot.utils.Log

class WebSocket(application: Application) {
    companion object {
        private val TAG = "WebSocket"
    }
    init {
        Log.logger!!.I(TAG, "Launching WebSockets...")
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