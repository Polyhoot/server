package org.ciphen.polyhoot.domain

import kotlinx.serialization.Serializable
@Serializable
data class Pack(
    val id: String,
    val authorId: String,
    val name: String,
    val questions: List<Question>
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
    val start: Int?,
    val end: Int?,
    val hideName: Boolean?
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