package org.ciphen.polyhoot.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.aventrix.jnanoid.jnanoid.NanoIdUtils
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.ciphen.polyhoot.db.DB
import org.ciphen.polyhoot.domain.*
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

                if (findUser > 0) {
                    call.respond(HttpStatusCode.Conflict, CreateUserResponse(409, null, "Email already exists"))
                    return@post
                }

                val newUser = User(
                    id = NanoIdUtils.randomNanoId(),
                    name = userDTO.name,
                    email = userDTO.email,
                    password = BCrypt.hashpw(userDTO.password, BCrypt.gensalt()),
                    packs = emptyList()
                )
                users.insertOne(newUser)

                call.respond(
                    CreateUserResponse(
                        200, JWT.create()
                            .withClaim("id", newUser.id)
                            .withClaim("email", userDTO.email)
                            .sign(Algorithm.HMAC256(System.getenv("JWT_SECRET"))), null
                    )
                )
            }
            post("/login") {
                val loginDTO = call.receive<LoginDTO>()
                val user = users.findOne(User::email eq loginDTO.email)
                if (user == null) {
                    call.respond(HttpStatusCode.NotFound, CreateUserResponse(404, null, "User not found"))
                    return@post
                }
                val compare = BCrypt.checkpw(loginDTO.password, user.password)
                if (compare) {
                    call.respond(
                        CreateUserResponse(
                            200, JWT.create()
                                .withClaim("id", user.id)
                                .withClaim("email", user.email)
                                .sign(Algorithm.HMAC256(System.getenv("JWT_SECRET"))), null
                        )
                    )
                } else {
                    call.respond(HttpStatusCode.Unauthorized, CreateUserResponse(403, null, "Incorrect password"))
                }
            }
            authenticate("auth-jwt") {
                get("/info") {
                    val principal = call.principal<JWTPrincipal>()
                    val user = users.findOne(User::id eq principal!!.payload.getClaim("id").asString())
                    if (user == null) {
                        call.respond(HttpStatusCode.NotFound, CreateUserResponse(404, null, "User not found"))
                        return@get
                    }
                    call.respond(
                        UserDataResponse(
                            user.email,
                            user.name,
                            user.packs
                        )
                    )
                }
            }
        }
    }
}