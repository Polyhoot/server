package org.ciphen.polyhoot.game.session

import io.ktor.websocket.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.ciphen.polyhoot.domain.Pack
import org.ciphen.polyhoot.game.entities.Host
import org.ciphen.polyhoot.game.entities.Player
import org.ciphen.polyhoot.game.host.GameHostActions
import org.ciphen.polyhoot.game.session.events.GameSessionEventHandler
import org.ciphen.polyhoot.game.session.events.GameSessionEventType
import org.ciphen.polyhoot.game.utils.GamesController
import org.ciphen.polyhoot.services.entities.Client
import java.util.*
import kotlin.random.Random

class GameSession(val gameId: Int): Observer {
    companion object {
        private const val GAME_ID_MAX = 999999
        private const val GAME_ID_MIN = 100000

        fun create(): GameSession {
            val random = Random(System.currentTimeMillis())
            var gameId = random.nextInt(GAME_ID_MIN, GAME_ID_MAX)
            val gameController = GamesController.getInstance()
            while (gameController.getGameById(gameId) != null) {
                gameId = random.nextInt(GAME_ID_MIN, GAME_ID_MAX)
            }
            println("Creating new game session with UID $gameId.")
            val gameSession = GameSession(gameId)
            gameController.addGame(gameSession)
            return gameSession
        }
    }

    val players: MutableMap<Int, Player> = mutableMapOf()
    val gameSessionEventHandler = GameSessionEventHandler(this)
    // Initialize without host
    var host: Client? = null
    private var currId = 0

    init {
        println("GameSession: Created new game session with Game ID $gameId!")
    }

    fun startGame() {
        players.forEach { (idx, player) ->
            runBlocking {
                gameSessionEventHandler.notifyPlayer(player, GameSessionEventType.START_GAME)
            }
        }
    }

    fun nextQuestion() {
        players.forEach { (idx, player) ->
            runBlocking {
                gameSessionEventHandler.notifyPlayer(player, GameSessionEventType.QUESTION)
            }
        }
    }

    fun showScoreboard() {
        players.forEach { (idx, player) ->
            runBlocking {
                gameSessionEventHandler.notifyPlayer(player, GameSessionEventType.SCOREBOARD)
            }
        }
    }

    suspend fun connectPlayer(player: Player): Boolean {
        if (players.filter { it.value == player }.isNotEmpty() || host == null) {
            return false
        }
        players[currId] = player
        currId++
        host!!.session.outgoing.send(Frame.Text(JsonObject(mapOf(Pair("action", JsonPrimitive(GameHostActions.PLAYER_CONNECTED.toString())), Pair("name", JsonPrimitive(player.name)))).toString()))
        return true
    }

    fun connectHost(client: Client) {
        if (this.host == null) {
            println("GameSession: Connected host to Game ID $gameId")
            host = client
        }
    }

    fun removePlayer(client: Client) {
        players.filter { it.value.client == client }.forEach {
            players.remove(it.key)
        }
    }

    override fun update(o: Observable?, arg: Any?) {

    }
}