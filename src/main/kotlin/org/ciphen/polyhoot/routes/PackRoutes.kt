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
import org.litote.kmongo.util.idValue
import org.mindrot.jbcrypt.BCrypt

fun Application.packRouting() {
    val db = DB.database
    val packs = db.getCollection<Pack>("pack")

    routing {
        authenticate("auth-jwt") {
            route("/api/pack") {
                post("/create") {
                    val packDTO = call.receive<CreatePackDTO>()
                    val principal = call.principal<JWTPrincipal>()
                    val pack = Pack(
                        id = NanoIdUtils.randomNanoId(),
                        authorId = principal!!.payload.getClaim("id").asString(),
                        questions = packDTO.questions,
                        name = packDTO.name
                    )
                    packs.insertOne(pack)
                    call.respond(200)
                }
            }
        }
    }
}