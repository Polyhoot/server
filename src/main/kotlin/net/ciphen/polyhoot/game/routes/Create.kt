package net.ciphen.polyhoot.game.routes

import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import net.ciphen.polyhoot.Application
import net.ciphen.polyhoot.game.actions.GameActions
import net.ciphen.polyhoot.utils.Log

private const val TAG = "Routes.Create"

class Create {
    init {
        Application.getInstance().ktorApplication.routing {
            webSocket("/game/create") {
                Log.i(TAG, "Opened WebSocket with client.")
                val gameId = GameActions.Create()
                Log.i(TAG, "Created new game with ID $gameId")
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