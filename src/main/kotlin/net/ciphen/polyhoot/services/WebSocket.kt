package net.ciphen.polyhoot.services

import io.ktor.server.application.*
import io.ktor.server.websocket.*
import net.ciphen.polyhoot.game.routes.Create
import net.ciphen.polyhoot.game.routes.Host
import net.ciphen.polyhoot.game.routes.Session
import net.ciphen.polyhoot.utils.Log
import java.time.Duration

class WebSocket(application: Application) {
    companion object {
        private const val TAG = "WebSocket"
    }

    init {
        Log.i(TAG, "Launching WebSockets...")
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