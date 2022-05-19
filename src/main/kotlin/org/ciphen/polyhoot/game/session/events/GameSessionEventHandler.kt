package org.ciphen.polyhoot.game.session.events

import io.ktor.websocket.*
import kotlinx.serialization.json.*
import org.ciphen.polyhoot.game.entities.Player
import org.ciphen.polyhoot.game.session.GameSession
import java.util.Observable

enum class GameSessionEventType {
    CONNECT, START_GAME, SCOREBOARD, QUESTION, FINISH, END, STATS, INVALID, TIME_UP, NAME_TAKEN, ANSWER, FORCE_STOP, GET_READY;
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

class GameSessionEventHandler(val gameSession: GameSession): Observable() {
    suspend fun onPlayerEvent(player: Player, event: GameSessionEventType, args: String = "") {
        when (event) {
            GameSessionEventType.ANSWER -> {
                gameSession.registerAnswer(player, Json.parseToJsonElement(args).jsonObject["answer"]!!.jsonPrimitive.int, Json.parseToJsonElement(args).jsonObject["score"]!!.jsonPrimitive.int)
            }
        }
    }

    suspend fun notifyPlayer(player: Player, event: GameSessionEventType, extra: Array<Pair<String, JsonElement>>? = null) {
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