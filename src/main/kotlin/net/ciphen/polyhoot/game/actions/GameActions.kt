package net.ciphen.polyhoot.game.actions

import net.ciphen.polyhoot.game.session.GameSession

class GameActions {
    companion object {
        fun Create(): Int = GameSession.create().gameId
    }
}