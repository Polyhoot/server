package org.ciphen.polyhoot.mockhost

import io.ktor.client.*
import io.ktor.client.engine.java.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.*
import kotlin.properties.Delegates
import kotlin.system.exitProcess

class MockHost(private val domain: String, private val port: Int) {
    private val client = HttpClient(Java) {
        install(WebSockets)
    }

    private var gameId by Delegates.notNull<Int>()

    suspend fun start() {
        client.webSocket(
            method = HttpMethod.Get,
            host = domain,
            port = port,
            path = "/game/create"
        ) {
            gameId = Json.parseToJsonElement((incoming.receive() as Frame.Text).readText()).jsonObject["gameId"]!!.jsonPrimitive.int
            println("Received game ID: $gameId")
        }
        client.webSocket(
            method = HttpMethod.Get,
            host = domain,
            port = port,
            path = "/game/host"
        ) {
            val scope = GlobalScope.launch {
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

fun main() {
    runBlocking {
        MockHost("0.0.0.0", 8080).start()
    }
}