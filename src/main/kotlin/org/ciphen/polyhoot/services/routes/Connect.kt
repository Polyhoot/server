package org.ciphen.polyhoot.services.routes

import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.serialization.json.*
import org.ciphen.polyhoot.Application
import org.ciphen.polyhoot.game.entities.Player
import org.ciphen.polyhoot.game.utils.GamesController
import org.ciphen.polyhoot.services.entities.Client
import org.ciphen.polyhoot.services.enums.ClientStatus
import org.ciphen.polyhoot.services.enums.ClientType
import org.ciphen.polyhoot.services.utils.ClientManager
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class Connect {
    init {
        Application.getInstance().ktorApplication.routing {
            webSocket("/connect") {
                var data: Frame.Text? = null
                val id = UUID.randomUUID().toString()
                println("Client connected to /connect. Generated uuid = $id")
                if (incoming.receive().also { data = it as Frame.Text } is Frame.Text) {
                    if (data is Frame.Text) {
                        val json = Json.parseToJsonElement(data!!.readText())
                        val clientType: ClientType? =
                            ClientType.fromString(json.jsonObject["clientType"].toString().removeSurrounding("\"", "\""))
                        when (clientType) {
                            ClientType.HOST -> {
                                val client = Client(this, id, clientType)
                                client.clientStatus = ClientStatus.CONNECTING
                                ClientManager.getInstance().registerClient(client)
                                outgoing.send(Frame.Text(JsonObject(mapOf(Pair("uuid", JsonPrimitive(id)))).toString()))
                            }
                            ClientType.PLAYER -> {
                                val client = Client(this, id, clientType)
                                client.clientStatus = ClientStatus.CONNECTING
                                ClientManager.getInstance().registerClient(client)
                                if (GamesController.getInstance().connectPlayer(Player(client, json.jsonObject["gameId"].toString().toInt(), json.jsonObject["name"]!!.jsonPrimitive.content))) {
                                    outgoing.send(Frame.Text(JsonObject(mapOf(Pair("status", JsonPrimitive("ok")))).toString()))
                                } else {
                                    outgoing.send(Frame.Text(JsonObject(mapOf(Pair("status", JsonPrimitive("fail")))).toString()))
                                }
                            }
                            null -> close(
                                CloseReason(
                                    CloseReason.Codes.CANNOT_ACCEPT,
                                    "No client type provided. Bye"
                                )
                            )
                        }
                    }
                }
                for (frame in incoming) {
                    when (frame) {
                        is Frame.Close -> {
                            ClientManager.getInstance().disconnectClient(id)
                        }
                        is Frame.Text -> {
                            val json = Json.parseToJsonElement(frame.readText())
                            if (json.jsonObject["status"].toString().removeSurrounding("\"", "\"") == "disconnected") {
                                ClientManager.getInstance().disconnectClient(id)
                                this.cancel()
                            }
                        }
                    }
                }
                if (this.outgoing.isClosedForSend) {
                    ClientManager.getInstance().removeClient(id)
                    this.cancel()
                }
            }
        }
    }
}