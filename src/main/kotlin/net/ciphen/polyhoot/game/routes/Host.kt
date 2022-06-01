/*
 * Copyright (C) 2022 The Polyhoot Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import net.ciphen.polyhoot.utils.Log
import java.util.*

private const val TAG = "Routes.Host"

class Host {
    init {
        Application.getInstance().ktorApplication.routing {
            webSocket("/game/host") {
                Log.i(TAG, "Opened WebSocket with client.")
                var data: String
                var gameHostEventHandler: GameHostEventHandler? = null
                if (incoming.receive().also { data = (it as Frame.Text).readText() } is Frame.Text) {
                    val json = Json.parseToJsonElement(data)
                    val action = GameHostActions.fromString(json.jsonObject["action"]!!.jsonPrimitive.content)
                    Log.i(TAG, "Received action $action from the client.")
                    if (action != GameHostActions.CONNECT) {
                        Log.e(TAG, "First action should be GameHostActions.CONNECT!")
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
                        Log.i(TAG, "Registered new client with UUID = ${client.uuid}")
                        val gameId = json.jsonObject["gameId"]!!.jsonPrimitive.int
                        Log.i(TAG, "Connecting host to game with ID = $gameId")
                        val game = GamesController.getInstance().getGameById(gameId)
                        if (game != null) {
                            gameHostEventHandler = GameHostEventHandler(client, game)
                            gameHostEventHandler.onHostAction(action, "")
                        } else {
                            Log.e(TAG, "Tried to connect to non-existing game!")
                            close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Game not found!"))
                        }
                    }
                }
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        Log.i(TAG, "Received text data from the host.")
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