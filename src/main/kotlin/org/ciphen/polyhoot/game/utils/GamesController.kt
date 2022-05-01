package org.ciphen.polyhoot.game.utils

import io.ktor.websocket.*
import org.ciphen.polyhoot.game.GameSession
import org.ciphen.polyhoot.game.entities.Player
import org.ciphen.polyhoot.services.entities.Client

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
        println("GameController: added new game!")
        games[gameSession.gameId] = gameSession
    }

    fun getGameById(gameId: Int): GameSession? = games[gameId]

    fun connectPlayer(player: Player): Boolean {
        if (!games.keys.contains(player.gameId)) {
            return false
        } else {
            println("Adding player named ${player.name} with uuid = ${player.client.uuid} to game with uid ${player.gameId}")
            games[player.gameId]!!.connectPlayer(player)
            return true
        }
    }

    suspend fun hostDisconnected(client: Client): Boolean {
        val matchingGames = games.filter { it.value.client == client }
        if (matchingGames.isEmpty()) {
            return false
        }
        matchingGames.forEach {
            removeGame(it.key)
        }
        return true
    }

    suspend fun removeGame(gameId: Int) {
        games[gameId]!!.players.values.forEach {
            it.client.session.close(CloseReason(CloseReason.Codes.GOING_AWAY, "Game has ended."))
        }
        games.remove(gameId)
    }
}