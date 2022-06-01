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

package net.ciphen.polyhoot.game.session.events

import io.ktor.websocket.*
import kotlinx.serialization.json.*
import net.ciphen.polyhoot.game.entities.Player
import net.ciphen.polyhoot.game.session.GameSession
import net.ciphen.polyhoot.utils.Log

private const val TAG = "GameSessionEventHandler"

enum class GameSessionEventType {
    CONNECT, START_GAME, QUESTION, END, STATS, INVALID, TIME_UP, NAME_TAKEN, ANSWER, FORCE_STOP, GET_READY, NO_SUCH_GAME;

    companion object {
        fun fromString(s: String): GameSessionEventType =
            when (s) {
                "stats" -> STATS
                "connect" -> CONNECT
                "answer" -> ANSWER
                else -> INVALID
            }
    }
}

class GameSessionEventHandler(private val gameSession: GameSession) {
    suspend fun onPlayerEvent(player: Player, event: GameSessionEventType, args: String = "") {
        Log.i(TAG, "Received event $event from player ${player.name}")
        when (event) {
            GameSessionEventType.ANSWER -> {
                gameSession.registerAnswer(
                    player,
                    Json.parseToJsonElement(args).jsonObject["answer"]!!.jsonPrimitive.int,
                    Json.parseToJsonElement(args).jsonObject["score"]!!.jsonPrimitive.int
                )
            }
            else -> Log.e(TAG, "Received invalid event.")
        }
    }

    suspend fun notifyPlayer(
        player: Player,
        event: GameSessionEventType,
        extra: Array<Pair<String, JsonElement>>? = null
    ) {
        Log.i(TAG, "Sending player ${player.name} event notice $event")
        player.client.session.outgoing.send(
            Frame.Text(
                if (extra == null) {
                    JsonObject(mapOf(Pair("event", JsonPrimitive(event.toString())))).toString()
                } else {
                    JsonObject(mapOf(Pair("event", JsonPrimitive(event.toString())), *extra)).toString()
                }
            )
        )
    }
}