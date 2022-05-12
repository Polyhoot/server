package org.ciphen.polyhoot.game.utils

import io.ktor.websocket.*
import org.ciphen.polyhoot.game.session.GameSession
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

    suspend fun connectPlayer(player: Player): Boolean {
        if (!games.keys.contains(player.gameId)) {
            return false
        } else {
            println("Adding player named ${player.name} with uuid = ${player.client.uuid} to game with uid ${player.gameId}")
            games[player.gameId]!!.connectPlayer(player)
            return true
        }
    }

    private fun getGameByHost(client: Client): GameSession? = games.filter { it.value.host == client }[0]

    suspend fun hostDisconnected(client: Client): Boolean {
        removeGame((getGameByHost(client) ?: return false).gameId)
        return true
    }

    private suspend fun removeGame(gameId: Int) {
        games[gameId]!!.players.values.forEach {
            it.client.session.close(CloseReason(CloseReason.Codes.GOING_AWAY, "Game has ended."))
        }
        games.remove(gameId)
    }
}