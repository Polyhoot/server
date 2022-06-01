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

package org.ciphen.polyhoot.mockhost

import io.ktor.client.*
import io.ktor.client.engine.java.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.*
import kotlin.properties.Delegates
import kotlin.system.exitProcess

class MockHost(args: Array<String>) {
    private val parser = ArgParser("mockhost")
    private val hostName by parser.argument(ArgType.String, fullName = "hostName", description = "WebSocket URL to server backend")
    private val port by parser.option(ArgType.Int, shortName = "p", description = "Port to use for WebSocket host (default: none)").default(0)
    private val secure by parser.option(ArgType.Boolean, shortName = "s", description = "Enable WebSocket Secure").default(false)
    private val url: String

    init {
        parser.parse(args)
        url = "ws${if (secure) "s" else ""}://$hostName${if (port != 0) ":$port" else ""}"
    }

    private val client = HttpClient(Java) {
        install(WebSockets)
    }

    private var gameId by Delegates.notNull<Int>()

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun start() {
        client.webSocket("$url/game/create") {
            gameId =
                Json.parseToJsonElement((incoming.receive() as Frame.Text).readText()).jsonObject["gameId"]!!.jsonPrimitive.int
            println("Received game ID: $gameId")
        }
        client.webSocket("$url/game/host") {
            GlobalScope.launch {
                for (frame in incoming) {
                    val text = (frame as Frame.Text).readText()
                    println(text)
                    val json = Json.parseToJsonElement(text)
                    when (json.jsonObject["action"]!!.jsonPrimitive.content) {
                        "scoreboard" -> {
                            println("Scoreboard")
                            val scoreboard = json.jsonObject["scoreboard"]!!.jsonArray
                            scoreboard.forEach {
                                val name = it.jsonObject["name"]!!.jsonPrimitive.content
                                val score = it.jsonObject["score"]!!.jsonPrimitive.int
                                println("$name -> $score")
                            }
                        }
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
            println("Connected to game. Press any key to start game.")
            readLine()
            outgoing.send(
                Frame.Text(
                    JsonObject(
                        mapOf(
                            Pair("action", JsonPrimitive("start_game"))
                        ),
                    ).toString()
                )
            )
            println("Started game. Press any key to send question.")
            readLine()
            outgoing.send(
                Frame.Text(
                    JsonObject(
                        mapOf(
                            Pair("action", JsonPrimitive("get_ready"))
                        ),
                    ).toString()
                )
            )
            Thread.sleep(5000)
            outgoing.send(
                Frame.Text(
                    JsonObject(
                        mapOf(
                            Pair("action", JsonPrimitive("send_question")),
                            Pair("duration", JsonPrimitive(10)),
                            Pair("answers", JsonArray(listOf(JsonPrimitive(0))))
                        ),
                    ).toString()
                )
            )
            Thread.sleep(10000)
            outgoing.send(
                Frame.Text(
                    JsonObject(
                        mapOf(
                            Pair("action", JsonPrimitive("time_up")),
                        ),
                    ).toString()
                )
            )
            Thread.sleep(1000)
            outgoing.send(
                Frame.Text(
                    JsonObject(
                        mapOf(
                            Pair("action", JsonPrimitive("scoreboard")),
                        ),
                    ).toString()
                )
            )
            println("For next question press any key to send question.")
            readLine()
            outgoing.send(
                Frame.Text(
                    JsonObject(
                        mapOf(
                            Pair("action", JsonPrimitive("get_ready"))
                        ),
                    ).toString()
                )
            )
            Thread.sleep(5000)
            outgoing.send(
                Frame.Text(
                    JsonObject(
                        mapOf(
                            Pair("action", JsonPrimitive("send_question")),
                            Pair("duration", JsonPrimitive(10)),
                            Pair("answer", JsonPrimitive(0))
                        ),
                    ).toString()
                )
            )
            Thread.sleep(10000)
            outgoing.send(
                Frame.Text(
                    JsonObject(
                        mapOf(
                            Pair("action", JsonPrimitive("time_up")),
                        ),
                    ).toString()
                )
            )
            Thread.sleep(1000)
            outgoing.send(
                Frame.Text(
                    JsonObject(
                        mapOf(
                            Pair("action", JsonPrimitive("scoreboard")),
                        ),
                    ).toString()
                )
            )
            outgoing.send(
                Frame.Text(
                    JsonObject(
                        mapOf(
                            Pair("action", JsonPrimitive("end")),
                        ),
                    ).toString()
                )
            )
            exitProcess(0)
        }
    }
}

fun main(args: Array<String>) {
    runBlocking {
        MockHost(args).start()
    }
}