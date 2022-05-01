package org.ciphen.polyhoot.game

import org.ciphen.polyhoot.game.entities.Player
import org.ciphen.polyhoot.game.utils.GamesController
import org.ciphen.polyhoot.services.entities.Client
import kotlin.random.Random

class GameSession(val client: Client, val packId: Long, val gameId: Int) {
    companion object {
        private const val GAME_ID_MAX = 999999
        private const val GAME_ID_MIN = 100000

        fun create(client: Client, packId: Long): GameSession {
            val random = Random(System.currentTimeMillis())
            var gameId = random.nextInt(GAME_ID_MIN, GAME_ID_MAX)
            val gameController = GamesController.getInstance()
            while (gameController.getGameById(gameId) != null) {
                gameId = random.nextInt(GAME_ID_MIN, GAME_ID_MAX)
            }
            println("Creating new game session with UID $gameId and pack ID $packId. Created by host uuid = ${client.uuid}")
            val gameSession = GameSession(client, packId, gameId)
            gameController.addGame(gameSession)
            return gameSession
        }
    }

    val players: MutableMap<Int, Player> = mutableMapOf()
    private var currId = 0

    init {
        println("GameSession: ${client.uuid}: Created new game session with Game ID $gameId!")
    }

    fun connectPlayer(player: Player): Boolean {
        if (players.filter { it.value == player }.isNotEmpty()) {
            return false
        }
        players[currId] = player
        currId++
        return true
    }

    fun removePlayer(client: Client) {
        players.filter { it.value.client == client }.forEach {
            players.remove(it.key)
        }
    }
}