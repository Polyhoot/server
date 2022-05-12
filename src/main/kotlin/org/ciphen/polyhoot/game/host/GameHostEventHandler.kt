package org.ciphen.polyhoot.game.host

import org.ciphen.polyhoot.game.session.GameSession
import org.ciphen.polyhoot.services.entities.Client

class GameHostEventHandler(val client: Client, val game: GameSession) {
    fun onHostAction(action: GameHostActions, data: String) {
        when (action) {
            GameHostActions.CONNECT -> {
                game.connectHost(client)
            }
            GameHostActions.SEND_PACK -> {

            }
        }
    }
}