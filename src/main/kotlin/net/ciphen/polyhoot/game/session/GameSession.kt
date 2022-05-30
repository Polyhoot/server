package net.ciphen.polyhoot.game.session

import io.ktor.websocket.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import net.ciphen.polyhoot.game.entities.Player
import net.ciphen.polyhoot.game.host.GameHostActions
import net.ciphen.polyhoot.game.session.events.GameSessionEventHandler
import net.ciphen.polyhoot.game.session.events.GameSessionEventType
import net.ciphen.polyhoot.game.utils.GamesController
import net.ciphen.polyhoot.services.entities.Client
import kotlin.random.Random

class GameSession(val gameId: Int) {
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
    private var currAnswer = 0
    private var currId = 0

    init {
        println("GameSession: Created new game session with Game ID $gameId!")
    }

    fun startGame() {
        players.forEach { (_, player) ->
            runBlocking {
                gameSessionEventHandler.notifyPlayer(player, GameSessionEventType.START_GAME)
            }
        }
    }

    fun nextQuestion(duration: Int, answer: Int, text: String = "") {
        currAnswer = answer
        players.forEach { (_, player) ->
            runBlocking {
                gameSessionEventHandler.notifyPlayer(
                    player,
                    GameSessionEventType.QUESTION,
                    arrayOf(
                        Pair("duration", JsonPrimitive(duration)),
                        Pair("text", JsonPrimitive(text))
                    )
                )
            }
        }
    }

    suspend fun showScoreboard() {
        val list = mutableListOf<JsonElement>()
        players.forEach { (_, player) ->
            list.add(
                JsonObject(
                    mapOf(
                        Pair("name", JsonPrimitive(player.name)),
                        Pair("score", JsonPrimitive(player.score))
                    )
                )
            )
        }
        val jsonArray = JsonArray(list)
        host!!.session.outgoing.send(
            Frame.Text(
                JsonObject(
                    mapOf(
                        Pair("action", JsonPrimitive(GameHostActions.SCOREBOARD.toString())),
                        Pair("scoreboard", jsonArray)
                    )
                ).toString()
            )
        )
    }

    fun questionTimeUp() {
        players.forEach { (_, player) ->
            runBlocking {
                gameSessionEventHandler.notifyPlayer(
                    player,
                    GameSessionEventType.TIME_UP,
                    arrayOf(Pair("score", JsonPrimitive(player.score)))
                )
            }
        }
    }

    fun getReady() {
        players.forEach {
            runBlocking {
                gameSessionEventHandler.notifyPlayer(it.value, GameSessionEventType.GET_READY)
            }
        }
    }

    fun endGame() {
        players.forEach {
            runBlocking {
                gameSessionEventHandler.notifyPlayer(it.value, GameSessionEventType.END)
            }
        }
    }

    suspend fun connectPlayer(player: Player): Boolean {
        if (players.filter { it.value == player || it.value.name == player.name }.isNotEmpty() || host == null) {
            return false
        }
        players[currId] = player
        currId++
        runBlocking {
            gameSessionEventHandler.notifyPlayer(player, GameSessionEventType.CONNECT)
        }
        host!!.session.outgoing.send(
            Frame.Text(
                JsonObject(
                    mapOf(
                        Pair(
                            "action",
                            JsonPrimitive(GameHostActions.PLAYER_CONNECTED.toString())
                        ), Pair("name", JsonPrimitive(player.name))
                    )
                ).toString()
            )
        )
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

    suspend fun registerAnswer(player: Player, answer: Int, score: Int) {
        if (answer == currAnswer) {
            player.score += score
            println("Player ${player.name} got the answer right! Score: ${player.score}")
        } else {
            println("Player ${player.name} has the wrong answer! Score: ${player.score}")
        }
        host!!.session.outgoing.send(
            Frame.Text(
                JsonObject(
                    mapOf(
                        Pair("action", JsonPrimitive(GameHostActions.ANSWER.toString())),
                        Pair("answer", JsonPrimitive(answer))
                    )
                ).toString()
            )
        )
    }
}