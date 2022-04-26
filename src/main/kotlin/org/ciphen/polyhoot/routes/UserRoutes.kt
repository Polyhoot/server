package org.ciphen.polyhoot.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.ciphen.polyhoot.db.DB
import org.ciphen.polyhoot.domain.CreateUserDTO
import org.ciphen.polyhoot.domain.CreateUserResponse
import org.ciphen.polyhoot.domain.User
import org.litote.kmongo.eq
import org.mindrot.jbcrypt.BCrypt

fun Application.userRouting() {
    val db = DB.database
    val users = db.getCollection<User>("user")
    routing {
        route("/api/user") {
            post("/create") {
                val userDTO = call.receive<CreateUserDTO>()
                val findUser: Long = users.countDocuments(User::email eq userDTO.email)

                println(userDTO.email)
                println(findUser)
                if (findUser > 0) {
                    call.respond(CreateUserResponse(409,null, "Email already exists"))
                    return@post
                }
                val newUser = users.insertOne(User(
                    name = userDTO.name,
                    email = userDTO.email,
                    password = BCrypt.hashpw(userDTO.password, BCrypt.gensalt()),
                    packs = emptyList()
                ))

                call.respond(
                    CreateUserResponse(200, JWT.create()
                        .withClaim("id", newUser.insertedId?.toString())
                        .withClaim("email", userDTO.email)
                        .sign(Algorithm.HMAC256(System.getenv("JWT_SECRET"))), null)
                )
            }
        }
    }
}