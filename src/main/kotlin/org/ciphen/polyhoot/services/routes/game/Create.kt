package org.ciphen.polyhoot.services.routes.game

import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.json.*
import org.ciphen.polyhoot.Application
import org.ciphen.polyhoot.game.actions.GameActions
import org.ciphen.polyhoot.services.utils.ClientManager

class Create {
    private val clientManager = ClientManager.getInstance()

    init {
        Application.getInstance().ktorApplication.routing {
            webSocket("/game/create") {
                var data: Frame.Text
                if (incoming.receive().also { data = it as Frame.Text } is Frame.Text) {
                    val gamePin = GameActions.Create()
                    outgoing.send(
                        Frame.Text(
                            JsonObject(
                                mapOf(
                                    Pair(
                                        "gamePin",
                                        JsonPrimitive(gamePin)
                                    )
                                )
                            ).toString()
                        )
                    )
                }
            }
        }
    }
}