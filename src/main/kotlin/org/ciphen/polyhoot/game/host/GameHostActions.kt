package org.ciphen.polyhoot.game.host

enum class GameHostActions {
    CONNECT, START_GAME, SEND_QUESTION, SHOW_SCOREBOARD, PLAYER_CONNECTED, INVALID;

    companion object {
        fun fromString(s: String): GameHostActions =
            when (s) {
                "connect" -> CONNECT
                "send_question" -> SEND_QUESTION
                "start_game" -> START_GAME
                "player_connected" -> PLAYER_CONNECTED
                "show_scoreboard" -> SHOW_SCOREBOARD
                else -> INVALID
            }
    }

    override fun toString(): String =
        when (this) {
            CONNECT -> "connect"
            START_GAME -> "start_game"
            SEND_QUESTION -> "send_question"
            PLAYER_CONNECTED -> "player_connected"
            SHOW_SCOREBOARD -> "show_scoreboard"
            INVALID -> "invalid"
        }
}