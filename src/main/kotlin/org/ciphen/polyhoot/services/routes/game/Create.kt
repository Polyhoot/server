package org.ciphen.polyhoot.services.routes.game

import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.json.*
import org.ciphen.polyhoot.Application
import org.ciphen.polyhoot.game.GameSession
import org.ciphen.polyhoot.services.utils.ClientManager

class Create {
    private val clientManager = ClientManager.getInstance()

    init {
        Application.getInstance().ktorApplication.routing {
            webSocket("/game/create") {
                var data: Frame.Text
                if (incoming.receive().also { data = it as Frame.Text } is Frame.Text) {
                    val json = Json.parseToJsonElement(data.readText())
                    val uuid = json.jsonObject["uuid"]!!.jsonPrimitive.content
                    val packId = json.jsonObject["packId"]!!.jsonPrimitive.content.toLong()
                    if (clientManager.hasClient(uuid)) {
                        val session = GameSession.create(clientManager.getClient(uuid)!!, packId)
                        outgoing.send(Frame.Text(JsonObject(mapOf(Pair("gameId", JsonPrimitive(session.gameId)))).toString()))
                    }
                }
            }
        }
    }
}