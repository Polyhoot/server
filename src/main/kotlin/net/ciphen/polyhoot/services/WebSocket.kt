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

package net.ciphen.polyhoot.services

import io.ktor.server.application.*
import io.ktor.server.websocket.*
import net.ciphen.polyhoot.game.routes.Create
import net.ciphen.polyhoot.game.routes.Host
import net.ciphen.polyhoot.game.routes.Session
import net.ciphen.polyhoot.utils.Log
import java.time.Duration

class WebSocket(application: Application) {
    companion object {
        private const val TAG = "WebSocket"
    }

    init {
        Log.i(TAG, "Launching WebSockets...")
        application.install(WebSockets) {
            pingPeriod = Duration.ofSeconds(15)
            timeout = Duration.ofSeconds(15)
            maxFrameSize = Long.MAX_VALUE
            masking = false
        }

        Create()
        Host()
        Session()
    }
}