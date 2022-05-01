package org.ciphen.polyhoot.game.actions

import org.ciphen.polyhoot.game.session.GameSession
import org.ciphen.polyhoot.game.utils.GamesController
import org.ciphen.polyhoot.services.utils.ClientManager

class GameActions {
    companion object {
        private val clientManager = ClientManager.getInstance()
        private val gamesController = GamesController.getInstance()

        fun Create(uuid: String, packId: String): Int? =
            if (clientManager.hasClient(uuid)) {
                GameSession.create(clientManager.getClient(uuid)!!, packId).gameId
            } else {
                null
            }

        fun Start(uuid: String, gamePin: Int): Boolean {
            val game = gamesController.getGameById(gamePin)
            if (game != null && game.client.uuid == uuid) {
                game.startGame()
                return true
            }
            return false
        }
    }
}