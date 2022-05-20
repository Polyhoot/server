package org.ciphen.polyhoot.services.utils

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.ciphen.polyhoot.game.utils.GamesController
import org.ciphen.polyhoot.services.entities.Client
import org.ciphen.polyhoot.services.enums.ClientStatus
import org.ciphen.polyhoot.services.enums.ClientType

class ClientManager {
    companion object {
        private var INSTANCE: ClientManager? = null

        fun getInstance(): ClientManager {
            if (INSTANCE == null) {
                INSTANCE = ClientManager()
            }
            return INSTANCE!!
        }
    }

    private val clients: MutableMap<String, Client> = mutableMapOf()

    private suspend fun observeState(client: Client) {
        println("Observing connection state of client with UUID ${client.uuid}")
        while (client.isAlive()) {
            client.clientStatus = ClientStatus.CONNECTED
        }
        client.clientStatus = ClientStatus.DISCONNECTED
        removeClient(client.uuid)
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun registerClient(client: Client) {
        println("Registered client!")
        println(client)
        GlobalScope.launch {
            observeState(client)
        }
        clients[client.uuid] = client
    }

    private suspend fun removeClient(uuid: String) {
        println("Removing client with uuid = $uuid")
        val client: Client?
        if (getClient(uuid).also { client = it } != null) {
            when (client!!.clientType) {
                ClientType.HOST -> {
                    if (GamesController.getInstance().hostDisconnected(client)) {
                        println("Removed game session created by uuid = ${client.uuid}")
                    } else {
                        println("Client with uuid = ${client.uuid} didn't create any games.")
                    }
                }
                ClientType.PLAYER -> {
                    GamesController.getInstance().removeDisconnectedPlayer(client)
                }
            }
            client.clientStatus = ClientStatus.DISCONNECTED
            clients.remove(uuid)
        }
    }

    private fun getClient(uuid: String): Client? = clients[uuid]
}