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

package net.ciphen.polyhoot.game.host

enum class GameHostActions {
    CONNECT,
    START_GAME,
    SEND_QUESTION,
    SCOREBOARD,
    PLAYER_CONNECTED,
    INVALID,
    TIME_UP,
    ANSWER,
    GET_READY,
    END,
    PLAYER_DISCONNECTED;

    companion object {
        fun fromString(s: String): GameHostActions =
            when (s) {
                "connect" -> CONNECT
                "send_question" -> SEND_QUESTION
                "start_game" -> START_GAME
                "player_connected" -> PLAYER_CONNECTED
                "player_disconnected" -> PLAYER_DISCONNECTED
                "scoreboard" -> SCOREBOARD
                "time_up" -> TIME_UP
                "answer" -> ANSWER
                "get_ready" -> GET_READY
                "end" -> END
                else -> INVALID
            }
    }

    override fun toString(): String =
        when (this) {
            CONNECT -> "connect"
            START_GAME -> "start_game"
            SEND_QUESTION -> "send_question"
            PLAYER_CONNECTED -> "player_connected"
            PLAYER_DISCONNECTED -> "player_disconnected"
            SCOREBOARD -> "scoreboard"
            TIME_UP -> "time_up"
            INVALID -> "invalid"
            ANSWER -> "answer"
            GET_READY -> "get_ready"
            END -> "end"
        }
}