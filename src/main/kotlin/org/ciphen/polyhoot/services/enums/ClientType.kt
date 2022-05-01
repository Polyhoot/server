package org.ciphen.polyhoot.services.enums

enum class ClientType {
    HOST, PLAYER;

    companion object {
        fun fromString(string: String): ClientType? {
            return when (string) {
                "host" -> HOST
                "player" -> PLAYER
                else -> null
            }
        }
    }
}