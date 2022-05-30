package net.ciphen.polyhoot.game.routes

import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.json.*
import net.ciphen.polyhoot.Application
import net.ciphen.polyhoot.game.entities.Player
import net.ciphen.polyhoot.game.session.GameSession
import net.ciphen.polyhoot.game.session.events.GameSessionEventType
import net.ciphen.polyhoot.game.utils.GamesController
import net.ciphen.polyhoot.services.entities.Client
import net.ciphen.polyhoot.services.enums.ClientType
import net.ciphen.polyhoot.services.utils.ClientManager
import net.ciphen.polyhoot.utils.Log
import java.util.*

private const val TAG = "Routes.Session"

class Session {
    init {
        Application.getInstance().ktorApplication.routing {
            webSocket("/game/session") {
                Log.i(TAG, "Opened WebSocket.")
                var data: String
                var player: Player? = null
                var game: GameSession? = null
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        Log.i(TAG, "Received text data on WebSocket.")
                        val frameData = frame.readText()
                        val json = Json.parseToJsonElement(frameData)
                        val event = GameSessionEventType.fromString(json.jsonObject["event"]!!.jsonPrimitive.content)
                        if (event == GameSessionEventType.CONNECT) {
                            val gameId = json.jsonObject["gameId"]!!.jsonPrimitive.int
                            val name = json.jsonObject["name"]!!.jsonPrimitive.content
                            game = GamesController.getInstance().getGameById(gameId)
                            if (game != null) {
                                Log.i(TAG, "Connecting player $name to game ID $gameId")
                                val client = Client(this, UUID.randomUUID().toString(), ClientType.PLAYER)
                                ClientManager.getInstance().registerClient(client)
                                player = Player(client, gameId, name)
                                if (!game.connectPlayer(player)) {
                                    game.gameSessionEventHandler.notifyPlayer(player, GameSessionEventType.NAME_TAKEN)
                                }
                            } else {
                                outgoing.send(
                                    Frame.Text(
                                        JsonObject(
                                            mapOf(
                                                Pair(
                                                    "event",
                                                    JsonPrimitive(GameSessionEventType.NO_SUCH_GAME.toString())
                                                )
                                            )
                                        ).toString()
                                    )
                                )
                                close()
                            }
                        } else {
                            Log.i(TAG, "Received player event $event from ${player!!.name}")
                            game!!.gameSessionEventHandler.onPlayerEvent(player, event, frameData)
                        }
                    }
                }
            }
        }
    }
}