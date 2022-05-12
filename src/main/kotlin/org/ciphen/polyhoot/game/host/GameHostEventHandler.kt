package org.ciphen.polyhoot.game.host

import org.ciphen.polyhoot.game.session.GameSession
import org.ciphen.polyhoot.services.entities.Client
import org.ciphen.polyhoot.utils.Log

class GameHostEventHandler(val client: Client, val game: GameSession) {
    fun onHostAction(action: GameHostActions, data: String) {
        Log.logger!!.I("GameHostEventHandler", "Received action: ${action} with data $data")
        when (action) {
            GameHostActions.CONNECT -> {
                game.connectHost(client)
            }
            GameHostActions.START_GAME -> {
                game.startGame()
            }
            GameHostActions.SEND_QUESTION -> {
                game.nextQuestion()
            }
            GameHostActions.SHOW_SCOREBOARD -> {
                game.showScoreboard()
            }
        }
    }
}