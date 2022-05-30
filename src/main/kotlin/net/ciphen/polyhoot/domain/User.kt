package net.ciphen.polyhoot.domain

import kotlinx.serialization.Serializable

data class User(
    val id: String,
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

@Serializable
data class LoginDTO(
    val password: String,
    val email: String
)

@Serializable
data class UserDataResponse(
    val email: String,
    val name: String,
    val packs: List<String>
)