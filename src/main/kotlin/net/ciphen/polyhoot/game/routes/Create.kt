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