package org.ciphen.polyhoot.services.routes.game

import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.ciphen.polyhoot.Application
import org.ciphen.polyhoot.game.actions.GameActions

class Create {
    init {
        Application.getInstance().ktorApplication.routing {
            webSocket("/game/create") {
                val gameId = GameActions.Create()
                outgoing.send(
                    Frame.Text(
                        JsonObject(
                            mapOf(
                                Pair(
                                    "gameId",
                                    JsonPrimitive(gameId)
                                )
                            )
                        ).toString()
                    )
                )
            }
        }
    }
}