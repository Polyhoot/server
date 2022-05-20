package org.ciphen.polyhoot.game.actions

import org.ciphen.polyhoot.game.session.GameSession

class GameActions {
    companion object {
        fun Create(): Int = GameSession.create().gameId
    }
}