package org.ciphen.polyhoot.services.routes.game

import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.cancel
import kotlinx.serialization.json.*
import org.ciphen.polyhoot.Application
import org.ciphen.polyhoot.game.entities.Player
import org.ciphen.polyhoot.game.session.GameSession
import org.ciphen.polyhoot.game.session.events.GameSessionEventType
import org.ciphen.polyhoot.game.utils.GamesController
import org.ciphen.polyhoot.services.entities.Client
import org.ciphen.polyhoot.services.enums.ClientType
import org.ciphen.polyhoot.services.utils.ClientManager
import java.util.*

class Session {
    init {
        Application.getInstance().ktorApplication.routing {
            webSocket("/game/session") {
                var data: String
                var player: Player? = null
                var game: GameSession? = null
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        val frameData = frame.readText()
                        val json = Json.parseToJsonElement(frameData)
                        val event = GameSessionEventType.fromString(json.jsonObject["event"]!!.jsonPrimitive.content)
                        if (event == GameSessionEventType.CONNECT) {
                            val gameId = json.jsonObject["gameId"]!!.jsonPrimitive.int
                            val name = json.jsonObject["name"]!!.jsonPrimitive.content
                            game = GamesController.getInstance().getGameById(gameId)
                            if (game != null) {
                                println("Session: Connecting player $name to game ID $gameId")
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
                                                Pair("event", JsonPrimitive(GameSessionEventType.NO_SUCH_GAME.toString()))
                                            )
                                        ).toString()
                                    )
                                )
                                close()
                            }
                        } else {
                            game!!.gameSessionEventHandler.onPlayerEvent(player!!, event, frameData)
                        }
                    }
                }
            }
        }
    }
}