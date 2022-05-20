package org.ciphen.polyhoot.domain

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