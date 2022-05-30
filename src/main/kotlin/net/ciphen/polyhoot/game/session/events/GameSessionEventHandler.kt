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