package org.ciphen.polyhoot.domain
import io.ktor.http.*
import kotlinx.serialization.*
data class User(
    val name: String,
    val password: String,
    val email: String,
    val packs: List<String>
    )

@Serializable
data class CreateUserDTO(
    val name: String,
    val password: String,
    val email: String,
)

@Serializable
data class CreateUserResponse(
    val statusCode: Int,
    val token: String?,
    val errorMessage: String?
)