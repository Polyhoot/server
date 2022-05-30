package net.ciphen.polyhoot.services.utils

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.ciphen.polyhoot.game.utils.GamesController
import net.ciphen.polyhoot.services.entities.Client
import net.ciphen.polyhoot.services.enums.ClientStatus
import net.ciphen.polyhoot.services.enums.ClientType
import net.ciphen.polyhoot.utils.Log

class ClientManager {
    companion object {
        private const val TAG = "ClientManager"
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
        Log.i(TAG, "Observing connection state of client with UUID ${client.uuid}")
        while (client.isAlive()) {
            client.clientStatus = ClientStatus.CONNECTED
        }
        client.clientStatus = ClientStatus.DISCONNECTED
        removeClient(client.uuid)
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun registerClient(client: Client) {
        Log.i(TAG, "Registered client with UUID = ${client.uuid}")
        GlobalScope.launch {
            observeState(client)
        }
        clients[client.uuid] = client
    }

    private suspend fun removeClient(uuid: String) {
        Log.i(TAG, "Removing client with UUID = $uuid")
        val client: Client?
        if (getClient(uuid).also { client = it } != null) {
            when (client!!.clientType) {
                ClientType.HOST -> {
                    if (GamesController.getInstance().hostDisconnected(client)) {
                        Log.i(TAG, "Removed game session created by UUID = ${client.uuid}")
                    } else {
                        Log.i(TAG, "Client with UUID = ${client.uuid} didn't create any games.")
                    }
                }
                ClientType.PLAYER -> {
                    Log.i(TAG, "Player with UUID = $uuid has disconnected")
                    GamesController.getInstance().removeDisconnectedPlayer(client)
                }
            }
            client.clientStatus = ClientStatus.DISCONNECTED
            clients.remove(uuid)
        }
    }

    private fun getClient(uuid: String): Client? = clients[uuid]
}