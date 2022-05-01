package org.ciphen.polyhoot.services.entities

import io.ktor.server.websocket.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.ciphen.polyhoot.services.enums.ClientStatus
import org.ciphen.polyhoot.services.enums.ClientType

data class Client(
    val session: DefaultWebSocketServerSession,
    val uuid: String,
    val clientType: ClientType
) {
    var clientStatus: ClientStatus = ClientStatus.NOT_CONNECTED

    @OptIn(ExperimentalCoroutinesApi::class)
    fun isAlive() = !session.outgoing.isClosedForSend

    override fun toString(): String =
        "${clientStatus} Client typed ${clientType} with uuid = $uuid"
}