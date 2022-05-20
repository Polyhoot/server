package org.ciphen.polyhoot.services

import io.ktor.server.application.*
import io.ktor.server.websocket.*
import org.ciphen.polyhoot.services.routes.game.Create
import org.ciphen.polyhoot.services.routes.game.Host
import org.ciphen.polyhoot.services.routes.game.Session
import org.ciphen.polyhoot.utils.Log
import java.time.Duration

class WebSocket(application: Application) {
    companion object {
        private const val TAG = "WebSocket"
    }

    init {
        Log.logger!!.i(TAG, "Launching WebSockets...")
        application.install(WebSockets) {
            pingPeriod = Duration.ofSeconds(15)
            timeout = Duration.ofSeconds(15)
            maxFrameSize = Long.MAX_VALUE
            masking = false
        }

        Create()
        Host()
        Session()
    }
}