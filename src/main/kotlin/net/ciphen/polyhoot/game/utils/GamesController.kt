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

package net.ciphen.polyhoot.game.utils

import kotlinx.coroutines.cancel
import net.ciphen.polyhoot.game.session.GameSession
import net.ciphen.polyhoot.game.session.events.GameSessionEventType
import net.ciphen.polyhoot.services.entities.Client
import net.ciphen.polyhoot.utils.Log

private const val TAG = "GamesController"

class GamesController {
    companion object {
        private var INSTANCE: GamesController? = null

        fun getInstance(): GamesController {
            if (INSTANCE == null) {
                INSTANCE = GamesController()
            }
            return INSTANCE!!
        }
    }

    val games: MutableMap<Int, GameSession> = mutableMapOf()

    fun addGame(gameSession: GameSession) {
        Log.i(TAG, "Added new game with ID ${gameSession.gameId}")
        games[gameSession.gameId] = gameSession
    }

    fun getGameById(gameId: Int): GameSession? = games[gameId]

    fun removeGameSoft(gameId: Int) {
        games.remove(gameId)
        Log.i(TAG, "Removed game with ID $gameId")
    }

    private fun getGameByHost(client: Client): GameSession? {
        games.forEach {
            if (it.value.host!!.uuid == client.uuid) {
                return it.value
            }
        }
        return null
    }

    suspend fun removeDisconnectedPlayer(client: Client) {
        Log.i(TAG, "Removing player with client ID ${client.uuid}")
        games.forEach {
            it.value.removePlayer(client)
        }
    }

    suspend fun hostDisconnected(client: Client): Boolean {
        Log.i(TAG, "Removing game created by disconnected host with client ID ${client.uuid}")
        removeGame((getGameByHost(client) ?: return false).gameId)
        return true
    }

    private suspend fun removeGame(gameId: Int) {
        games[gameId]!!.players.forEach {
            games[gameId]!!.gameSessionEventHandler.notifyPlayer(
                it.value, GameSessionEventType.FORCE_STOP
            )
            it.value.client.session.cancel()
        }
        games.remove(gameId)
    }
}