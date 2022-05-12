package org.ciphen.polyhoot.services.routes.game

import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import org.ciphen.polyhoot.Application

class Session {
    init {
        Application.getInstance().ktorApplication.routing {
            webSocket("/game/session") {
                var data: String
                if (incoming.receive().also { data = (it as Frame.Text).readText() } is Frame.Text) {

                }
            }
        }
    }
}