/*
 * Copyright (C) 2022 The Polyhoot Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ciphen.polyhoot.test.game

import de.bwaldvogel.mongo.MongoServer
import de.bwaldvogel.mongo.backend.memory.MemoryBackend
import io.ktor.client.*
import io.ktor.client.engine.java.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.*
import net.ciphen.polyhoot.Application
import net.ciphen.polyhoot.game.session.GameSession
import net.ciphen.polyhoot.game.session.events.GameSessionEventType
import net.ciphen.polyhoot.game.utils.GamesController
import net.ciphen.polyhoot.utils.Log
import java.net.BindException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

private const val TAG = "GameProcessTest"

class GameProcessTest {
    init {
        Thread {
            try {
                MongoServer(MemoryBackend()).bind("localhost", 45678)
            } catch (_: BindException) {
            }
            Application.main(arrayOf())
        }.start()
    }

    private fun getClient() =
        HttpClient(Java) {
            install(WebSockets)
        }

    private fun createGame(): Int {
        while (!Application.getInstance().serverUp) {
            Log.e(TAG, "Server is not up yet.")
            Thread.sleep(100)
        }

        Thread.sleep(100)

        Log.i(TAG, "Creating new test game!")

        var gameId = -1
        runBlocking {
            getClient().webSocket("ws://0.0.0.0:8080/game/create") {
                gameId =
                    Json
                        .parseToJsonElement(
                            (incoming.receive() as Frame.Text).readText()
                        )
                        .jsonObject["gameId"]!!
                        .jsonPrimitive
                        .int
                Log.i(TAG, "Received game ID = $gameId")
            }
        }
        while (gameId == -1) {
            Log.e(TAG, "Waiting for game ID")
        }
        return gameId
    }

    @Test
    fun gameCreationTest() {
        val gameId = createGame()

        val game = GamesController.getInstance().getGameById(gameId)

        Log.i(TAG, "Starting gameCreationTest...")

        // Ensure game is not null
        assertNotNull(game)
        Log.i(TAG, "Passed game not null check.")

        // Ensure host is null since its not connected
        assertNull(game.host)
        Log.i(TAG, "Passed host is null check.")

        // Ensure GameSession has right game ID
        assertEquals(gameId, game.gameId)
        Log.i(TAG, "Passed game ID check.")
    }

    @OptIn(DelicateCoroutinesApi::class)
    @Test
    fun hostConnectionTest() {
        val gameId = createGame()
        runBlocking {
            getClient().webSocket("ws://0.0.0.0:8080/game/host") {
                GlobalScope.launch {
                    for (frame in incoming) {
                    }
                }
                outgoing.send(
                    Frame.Text(
                        JsonObject(
                            mapOf(
                                Pair("action", JsonPrimitive("connect")),
                                Pair("gameId", JsonPrimitive(gameId))
                            ),
                        ).toString()
                    )
                )

                withContext(Dispatchers.IO) {
                    Thread.sleep(200)
                }

                assertNotNull(GamesController.getInstance().getGameById(gameId))
                Log.i(TAG, "Passed created game not null check")

                assertNotNull(GamesController.getInstance().getGameById(gameId)!!.host)
                Log.i(TAG, "Passed game host not null check")
            }
        }
    }

    @Test
    fun hostDisconnectionHandlingTest() {
        val gameId = createGame()
        var game: GameSession? = null

        runBlocking {
            getClient().webSocket("ws://0.0.0.0:8080/game/host") {
                GlobalScope.launch {
                    for (frame in incoming) {
                    }
                }
                outgoing.send(
                    Frame.Text(
                        JsonObject(
                            mapOf(
                                Pair("action", JsonPrimitive("connect")),
                                Pair("gameId", JsonPrimitive(gameId))
                            ),
                        ).toString()
                    )
                )

                withContext(Dispatchers.IO) {
                    Thread.sleep(200)
                }

                game = GamesController.getInstance().getGameById(gameId)

                // Check if game not null for safety
                assertNotNull(game)

                this.cancel()
            }
        }

        Thread.sleep(200)

        // Check if GamesController has removed game created by disconnected host
        assertNull(GamesController.getInstance().getGameById(gameId))
        Log.i(TAG, "Passed game removed by GamesController check")

        // Ensure our game is not delusional
        assertNotNull(game)

        // Ensure host was connected once to removed game
        assertNotNull(game!!.host)
    }

    @OptIn(DelicateCoroutinesApi::class)
    @Test
    fun playerConnectionTest() {
        val gameId = createGame()
        runBlocking {
            getClient().webSocket("ws://0.0.0.0:8080/game/host") {
                GlobalScope.launch {
                    for (frame in incoming) {
                        if (frame is Frame.Text) {
                            assertEquals(
                                "player_connected",
                                Json.parseToJsonElement(frame.readText())
                                    .jsonObject["action"]!!
                                    .jsonPrimitive
                                    .content
                            )
                            Log.i(TAG, "Passed player connection host notification check")
                            break
                        }
                    }
                }
                outgoing.send(
                    Frame.Text(
                        JsonObject(
                            mapOf(
                                Pair("action", JsonPrimitive("connect")),
                                Pair("gameId", JsonPrimitive(gameId))
                            ),
                        ).toString()
                    )
                )

                withContext(Dispatchers.IO) {
                    Thread.sleep(200)
                }

                assertNotNull(GamesController.getInstance().getGameById(gameId))
                assertNotNull(GamesController.getInstance().getGameById(gameId)!!.host)

                getClient().webSocket("ws://0.0.0.0:8080/game/session") {
                    GlobalScope.launch {
                        for (frame in incoming) {
                            if (frame is Frame.Text) {
                                assertEquals(
                                    GameSessionEventType.CONNECT.toString(),
                                    Json
                                        .parseToJsonElement(frame.readText())
                                        .jsonObject["event"]!!
                                        .jsonPrimitive
                                        .content
                                )
                                Log.i(TAG, "Passed player connected player notification check")
                            }
                        }
                    }

                    outgoing.send(
                        Frame.Text(
                            "{\"event\":\"connect\",\"name\":\"test\",\"gameId\":$gameId}"
                        )
                    )

                    withContext(Dispatchers.IO) {
                        Thread.sleep(200)
                    }

                    val game = GamesController.getInstance().getGameById(gameId)

                    // Player was connected
                    // Ensure that is true
                    assertNotNull(game)

                    assertNotNull(game.players[0])
                    Log.i(TAG, "Passed player added to game check")

                    assertEquals("test", game.players[0]!!.name)
                    Log.i(TAG, "Passed test player name check")
                }

                withContext(Dispatchers.IO) {
                    Thread.sleep(200)
                }

                // PLAYER DISCONNECTS AT THIS POINT
                // Make sure player was actually disconnected

                assertEquals(
                    "player_disconnected",
                    Json.parseToJsonElement((incoming.receive() as Frame.Text).readText())
                        .jsonObject["action"]!!
                        .jsonPrimitive
                        .content
                )
                Log.i(TAG, "Passed player disconnection host notification check")

                val game = GamesController.getInstance().getGameById(gameId)

                assertNotNull(game)
                assertNull(game.players[0])
                Log.i(TAG, "Passed player removed from game check")
            }
        }
    }
}