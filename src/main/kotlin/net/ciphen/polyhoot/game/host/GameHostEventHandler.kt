package net.ciphen.polyhoot.game.host

import kotlinx.serialization.json.*
import net.ciphen.polyhoot.game.session.GameSession
import net.ciphen.polyhoot.services.entities.Client
import net.ciphen.polyhoot.utils.Log

private const val TAG = "GameHostEventHandler"

class GameHostEventHandler(val client: Client, val game: GameSession) {
    suspend fun onHostAction(action: GameHostActions, data: String) {
        Log.i(TAG, "Received action $action with data $data")
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
                val answers = json.jsonObject["answers"]!!.jsonArray.map { it.jsonPrimitive.int }
                val text = json.jsonObject["text"]?.jsonPrimitive?.content ?: ""
                game.nextQuestion(duration, answers, text)
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
            else -> Log.e(TAG, "Invalid action.")
        }
    }
}