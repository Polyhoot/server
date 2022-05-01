package org.ciphen.polyhoot.services.routes.game

import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.json.*
import org.ciphen.polyhoot.Application
import org.ciphen.polyhoot.game.actions.GameActions

class Start {
    init {
        Application.getInstance().ktorApplication.routing {
            webSocket("/game/start") {
                var data: String
                if (incoming.receive().also { data = (it as Frame.Text).readText() } is Frame.Text) {
                    val json = Json.parseToJsonElement(data)
                    if (GameActions.Start(
                        json.jsonObject["uuid"]!!.jsonPrimitive.content,
                        json.jsonObject["gamePin"]!!.jsonPrimitive.content.toInt()
                    )) {
                        outgoing.send(Frame.Text(JsonObject(mapOf(Pair("status", JsonPrimitive("ok")))).toString()))
                    } else {
                        outgoing.send(Frame.Text(JsonObject(mapOf(Pair("status", JsonPrimitive("fail")))).toString()))
                    }
                }
            }
        }
    }
}