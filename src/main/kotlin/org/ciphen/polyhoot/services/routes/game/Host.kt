package org.ciphen.polyhoot.services.routes.game

import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.json.*
import org.ciphen.polyhoot.Application
import org.ciphen.polyhoot.game.host.GameHostActions
import org.ciphen.polyhoot.game.host.GameHostEventHandler
import org.ciphen.polyhoot.game.utils.GamesController
import org.ciphen.polyhoot.services.entities.Client
import org.ciphen.polyhoot.services.enums.ClientType
import java.util.*

class Host {
    init {
        Application.getInstance().ktorApplication.routing {
            webSocket("/game/host") {
                var data: String
                var gameHostEventHandler: GameHostEventHandler? = null
                if (incoming.receive().also { data = (it as Frame.Text).readText() } is Frame.Text) {
                    val json = Json.parseToJsonElement(data)
                    val action = GameHostActions.fromString(json.jsonObject["action"]!!.jsonPrimitive.content)
                    println(action)
                    if (action != GameHostActions.CONNECT) {
                        outgoing.send(
                            Frame.Text(
                                JsonObject(
                                    mapOf(
                                        Pair("status", JsonPrimitive("fail")),
                                        Pair("message", JsonPrimitive("First handshake should be connect action!"))
                                    )
                                ).toString()
                            )
                        )
                        close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Should be connect action!"))
                    } else {
                        val client = Client(this, UUID.randomUUID().toString(), ClientType.HOST)
                        val gameId = json.jsonObject["gameId"]!!.jsonPrimitive.int
                        val game = GamesController.getInstance().getGameById(gameId)
                        if (game != null) {
                            gameHostEventHandler = GameHostEventHandler(client, game)
                            gameHostEventHandler.onHostAction(action, "")
                        } else {
                            close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Game not found!"))
                        }
                    }
                }
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        val frameData = frame.readText()
                        val json = Json.parseToJsonElement(frameData)
                        val action = GameHostActions.fromString(json.jsonObject["action"]!!.jsonPrimitive.content)
                        gameHostEventHandler!!.onHostAction(action, frameData)
                    }
                }
            }
        }
    }
}