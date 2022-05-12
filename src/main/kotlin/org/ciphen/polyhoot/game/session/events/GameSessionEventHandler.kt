package org.ciphen.polyhoot.game.session.events

import io.ktor.websocket.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.ciphen.polyhoot.game.entities.Player
import org.ciphen.polyhoot.game.session.GameSession
import java.util.Observable

enum class GameSessionEventType {
    CONNECT, START_GAME, SCOREBOARD, QUESTION, FINISH, END, STATS, INVALID;
    companion object {
        fun fromString(s: String): GameSessionEventType =
            when (s) {
                "stats" -> STATS
                else -> INVALID
            }
    }
}

class GameSessionEventHandler(gameSession: GameSession): Observable() {
    init {
        addObserver(gameSession)
    }
    fun onPlayerEvent(player: Player, event: GameSessionEventType, args: String = "") {
        // Player reporting their answer + time elapsed (ms)
    }

    suspend fun notifyPlayer(player: Player, event: GameSessionEventType, args: String = "") {
        player.client.session.outgoing.send(
            Frame.Text(
                JsonObject(mapOf(Pair("event", JsonPrimitive(event.toString())))).toString()
            )
        )
    }
}