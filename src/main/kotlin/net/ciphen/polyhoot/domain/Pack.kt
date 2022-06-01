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

package net.ciphen.polyhoot.domain

import kotlinx.serialization.Serializable

@Serializable
data class Pack(
    val id: String,
    val authorId: String,
    val name: String,
    val questions: List<Question>,
    val createdAt: String
)

@Serializable
data class Question(
    val id: String,
    val text: String,
    val time: Int,
    val type: Int,
    val media: Media?,
    val answers: List<Answer>
)

@Serializable
data class Media(
    val url: String,
    val startTime: Int?,
    val length: Int?,
    val hideName: Boolean?,
    val type: String
)

@Serializable
data class Answer(
    val text: String,
    val isCorrect: Boolean
)

@Serializable
data class CreatePackDTO(
    val name: String,
    val questions: List<Question>
)

@Serializable
data class AutosaveInfoResponse(
    val packId: String
)

@Serializable
data class SavePackDTO(
    val packId: String,
    val name: String,
    val questions: List<Question>
)

@Serializable
data class PackResponse(
    val id: String,
    val name: String,
    val questions: List<Question>,
    val authorId: String,
    val authorName: String,
    val createdAt: String
)

@Serializable
data class GetPacksResponse(
    val packs: List<PackResponse>
)