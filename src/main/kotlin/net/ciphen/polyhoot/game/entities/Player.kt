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

package net.ciphen.polyhoot.game.entities

import net.ciphen.polyhoot.services.entities.Client

data class Player(
    val client: Client,
    val gameId: Int,
    val name: String,
    var score: Int = 0,
)