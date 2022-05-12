package org.ciphen.polyhoot.services.routes.game

import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.ciphen.polyhoot.Application
import org.ciphen.polyhoot.game.entities.Player
import org.ciphen.polyhoot.game.session.GameSession
import org.ciphen.polyhoot.game.session.events.GameSessionEventType
import org.ciphen.polyhoot.game.utils.GamesController
import org.ciphen.polyhoot.services.entities.Client
import org.ciphen.polyhoot.services.enums.ClientType
import java.util.*

class Session {
    init {
        Application.getInstance().ktorApplication.routing {
            webSocket("/game/session") {
                var data: String
                var player: Player? = null
                var game: GameSession? = null
                if (incoming.receive().also { data = (it as Frame.Text).readText() } is Frame.Text) {
                    val json = Json.parseToJsonElement(data)
                    val gameId = json.jsonObject["gameId"]!!.jsonPrimitive.int
                    val name = json.jsonObject["name"]!!.jsonPrimitive.content
                    game = GamesController.getInstance().getGameById(gameId)
                    if (game != null) {
                        println("Session: Connecting player $name to game ID $gameId")
                        player = Player(Client(this, UUID.randomUUID().toString(), ClientType.PLAYER), gameId, name)
                        game.connectPlayer(player)
                    }
                }
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        val frameData = frame.readText()
                        val json = Json.parseToJsonElement(frameData)
                        val event = GameSessionEventType.fromString(json.jsonObject["event"]!!.jsonPrimitive.content)
                        game!!.gameSessionEventHandler.onPlayerEvent(player!!, event)
                    }
                }
            }
        }
    }
}