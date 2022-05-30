package net.ciphen.polyhoot.services.routes.debug

import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import net.ciphen.polyhoot.Application
import net.ciphen.polyhoot.game.utils.GamesController

class Debug {
    init {
        val application = Application.getInstance()
        if (application.applicationConfig.debug) {
            application.ktorApplication.routing {
                webSocket("/debug") {
                    for (frame in incoming) {
                        when (frame) {
                            is Frame.Text -> {
                                when (frame.readText()) {
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