package org.ciphen.polyhoot.game.host

enum class GameHostActions {
    CONNECT, SEND_PACK, PLAYER_CONNECTED, INVALID;

    companion object {
        fun fromString(s: String): GameHostActions =
            when (s) {
                "connect" -> CONNECT
                "send_pack" -> SEND_PACK
                "player_connected" -> PLAYER_CONNECTED
                else -> INVALID
            }
    }

    override fun toString(): String =
        when (this) {
            CONNECT -> "connect"
            SEND_PACK -> "send_pack"
            PLAYER_CONNECTED -> "player_connected"
            INVALID -> "invalid"
        }
}