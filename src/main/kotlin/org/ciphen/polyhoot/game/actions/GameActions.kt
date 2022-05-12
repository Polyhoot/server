package org.ciphen.polyhoot.game.actions

import org.ciphen.polyhoot.game.session.GameSession
import org.ciphen.polyhoot.game.utils.GamesController
import org.ciphen.polyhoot.services.utils.ClientManager

class GameActions {
    companion object {
        private val clientManager = ClientManager.getInstance()
        private val gamesController = GamesController.getInstance()

        fun Create(): Int = GameSession.create().gameId
    }
}