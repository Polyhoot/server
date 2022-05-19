package org.ciphen.polyhoot.game.host

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.ciphen.polyhoot.game.session.GameSession
import org.ciphen.polyhoot.services.entities.Client
import org.ciphen.polyhoot.utils.Log

class GameHostEventHandler(val client: Client, val game: GameSession) {
    suspend fun onHostAction(action: GameHostActions, data: String) {
        Log.logger!!.I("GameHostEventHandler", "Received action: ${action} with data $data")
        when (action) {
            GameHostActions.CONNECT -> {
                game.connectHost(client)
            }
            GameHostActions.START_GAME -> {
                game.startGame()
            }
            GameHostActions.SEND_QUESTION -> {
                val json = Json.parseToJsonElement(data)
                val duration = json.jsonObject["duration"]!!.jsonPrimitive.int
                val answer = json.jsonObject["answer"]!!.jsonPrimitive.int
                game.nextQuestion(duration, answer)
            }
            GameHostActions.TIME_UP -> {
                game.questionTimeUp()
            }
            GameHostActions.SCOREBOARD -> {
                game.showScoreboard()
            }
            GameHostActions.GET_READY -> {
                game.getReady()
            }
            GameHostActions.END -> {
                game.endGame()
            }
        }
    }
}