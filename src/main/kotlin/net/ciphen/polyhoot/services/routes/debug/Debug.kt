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

package net.ciphen.polyhoot.services.routes.debug

import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import net.ciphen.polyhoot.Application
import net.ciphen.polyhoot.game.utils.GamesController

class Debug {
    init {
        val application = Application.getInstance()
        if (application.applicationConfig.debug) {
            application.ktorApplication.routing {
                webSocket("/debug") {
                    for (frame in incoming) {
                        when (frame) {
                            is Frame.Text -> {
                                when (frame.readText()) {
                                    "listSessions" -> outgoing.send(Frame.Text(GamesController.getInstance().games.toString()))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}