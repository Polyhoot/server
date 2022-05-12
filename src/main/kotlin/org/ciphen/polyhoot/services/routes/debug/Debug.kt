package org.ciphen.polyhoot.services.routes.debug

import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import org.ciphen.polyhoot.Application
import org.ciphen.polyhoot.game.utils.GamesController

class Debug {
    init {
        val application = Application.getInstance()
        if (application.applicationConfig.debug) {
            application.ktorApplication.routing {
                webSocket("/debug") {
                    for (frame in incoming) {
                        when (frame) {
                            is Frame.Text -> {
                                val text = frame.readText()
                                when (text) {
                                    "listSessions" -> outgoing.send(Frame.Text(GamesController.getInstance().games.toString()))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}