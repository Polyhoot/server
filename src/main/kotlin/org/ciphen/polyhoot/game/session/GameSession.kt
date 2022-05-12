package org.ciphen.polyhoot.game.session

import io.ktor.websocket.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.ciphen.polyhoot.game.entities.Host
import org.ciphen.polyhoot.game.entities.Player
import org.ciphen.polyhoot.game.utils.GamesController
import org.ciphen.polyhoot.services.entities.Client
import kotlin.random.Random

class GameSession(val packId: String, val gameId: Int) {
    companion object {
        private const val GAME_ID_MAX = 999999
        private const val GAME_ID_MIN = 100000

        fun create(packId: String): GameSession {
            val random = Random(System.currentTimeMillis())
            var gameId = random.nextInt(GAME_ID_MIN, GAME_ID_MAX)
            val gameController = GamesController.getInstance()
            while (gameController.getGameById(gameId) != null) {
                gameId = random.nextInt(GAME_ID_MIN, GAME_ID_MAX)
            }
            println("Creating new game session with UID $gameId and pack ID $packId.")
            val gameSession = GameSession(packId, gameId)
            gameController.addGame(gameSession)
            return gameSession
        }
    }

    val players: MutableMap<Int, Player> = mutableMapOf()
    // Initialize without host
    var host: Client? = null
    private var currId = 0

    init {
        println("GameSession: Created new game session with Game ID $gameId!")
    }

    fun startGame() {
        players.forEach { idx, player ->
            runBlocking {

            }
        }
    }

    fun connectPlayer(player: Player): Boolean {
        if (players.filter { it.value == player }.isNotEmpty() || host == null) {
            return false
        }
        players[currId] = player
        currId++
        return true
    }

    fun connectHost(client: Client) {
        if (this.host == null) {
            host = client
        }
    }

    fun removePlayer(client: Client) {
        players.filter { it.value.client == client }.forEach {
            players.remove(it.key)
        }
    }
}