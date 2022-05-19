package org.ciphen.polyhoot.game.host

enum class GameHostActions {
    CONNECT, START_GAME, SEND_QUESTION, SCOREBOARD, PLAYER_CONNECTED, INVALID, TIME_UP, ANSWER, GET_READY, END;

    companion object {
        fun fromString(s: String): GameHostActions =
            when (s) {
                "connect" -> CONNECT
                "send_question" -> SEND_QUESTION
                "start_game" -> START_GAME
                "player_connected" -> PLAYER_CONNECTED
                "scoreboard" -> SCOREBOARD
                "time_up" -> TIME_UP
                "answer" -> ANSWER
                "get_ready" -> GET_READY
                "end" -> END
                else -> INVALID
            }
    }

    override fun toString(): String =
        when (this) {
            CONNECT -> "connect"
            START_GAME -> "start_game"
            SEND_QUESTION -> "send_question"
            PLAYER_CONNECTED -> "player_connected"
            SCOREBOARD -> "scoreboard"
            TIME_UP -> "time_up"
            INVALID -> "invalid"
            ANSWER -> "answer"
            GET_READY -> "get_ready"
            END -> "end"
        }
}