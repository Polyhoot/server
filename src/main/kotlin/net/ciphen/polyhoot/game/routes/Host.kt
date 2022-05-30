package net.ciphen.polyhoot.game.routes

import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.json.*
import net.ciphen.polyhoot.Application
import net.ciphen.polyhoot.game.host.GameHostActions
import net.ciphen.polyhoot.game.host.GameHostEventHandler
import net.ciphen.polyhoot.game.utils.GamesController
import net.ciphen.polyhoot.services.entities.Client
import net.ciphen.polyhoot.services.enums.ClientType
import net.ciphen.polyhoot.services.utils.ClientManager
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
                        ClientManager.getInstance().registerClient(client)
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