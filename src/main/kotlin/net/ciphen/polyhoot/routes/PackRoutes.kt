package net.ciphen.polyhoot.routes

import com.aventrix.jnanoid.jnanoid.NanoIdUtils
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.ciphen.polyhoot.domain.*
import net.ciphen.polyhoot.db.DB
import org.litote.kmongo.*
import java.time.LocalDateTime

fun Application.packRouting() {
    val db = DB.database
    val packs = db.getCollection<Pack>("pack")
    val users = db.getCollection<User>("user")

    routing {
        authenticate("auth-jwt") {
            route("/api/pack") {
                get("/get/my") {
                    val max = call.request.queryParameters["max"]?.toInt() ?: 10
                    val principal = call.principal<JWTPrincipal>()
                    val packsFromDb = packs.find(Pack::authorId eq principal!!.payload.getClaim("id").asString()).sort(
                        descending(Pack::createdAt)
                    ).limit(max)
                    val user = users.findOne(User::id eq principal.payload.getClaim("id").asString())!!
                    val result = GetPacksResponse(packsFromDb.toList().map { e ->
                        PackResponse(
                            id = e.id,
                            name = e.name,
                            questions = e.questions,
                            authorId = e.authorId,
                            authorName = user.name,
                            createdAt = e.createdAt
                        )
                    })

                    call.respond(HttpStatusCode.OK, result)
                }
                get("/get/{id}") {
                    if (call.parameters["id"]?.isNotEmpty() == true) {
                        val pack = packs.findOne(Pack::id eq call.parameters["id"])
                        if (pack != null) {
                            val user = users.findOne(User::id eq pack.authorId)
                            if (user != null) {
                                call.respond(
                                    HttpStatusCode.OK, PackResponse(
                                        id = pack.id,
                                        name = pack.name,
                                        questions = pack.questions,
                                        authorId = pack.authorId,
                                        authorName = user.name,
                                        createdAt = pack.createdAt
                                    )
                                )
                            }
                            return@get
                        }
                    }
                    call.respond(HttpStatusCode.NotFound)
                }
                post("/create") {
                    val packDTO = call.receive<CreatePackDTO>()
                    val principal = call.principal<JWTPrincipal>()
                    val pack = Pack(
                        id = NanoIdUtils.randomNanoId(),
                        authorId = principal!!.payload.getClaim("id").asString(),
                        questions = packDTO.questions,
                        name = packDTO.name,
                        createdAt = LocalDateTime.now().toString()
                    )
                    packs.insertOne(pack)
                    call.respond(200)
                }
                post("/autosave") {
                    val packDTO = call.receive<CreatePackDTO>()
                    val principal = call.principal<JWTPrincipal>()
                    val packId = NanoIdUtils.randomNanoId()

                    val pack = Pack(
                        id = packId,
                        authorId = principal!!.payload.getClaim("id").asString(),
                        questions = packDTO.questions,
                        name = packDTO.name,
                        createdAt = LocalDateTime.now().toString()
                    )

                    packs.insertOne(pack)
                    call.respond(HttpStatusCode.OK, AutosaveInfoResponse(packId = packId))
                }
                post("/save") {
                    val packDTO = call.receive<SavePackDTO>()
                    val principal = call.principal<JWTPrincipal>()

                    val pack = packs.findOneAndUpdate(
                        and(
                            Pack::id eq packDTO.packId,
                            Pack::authorId eq principal!!.payload.getClaim("id").asString()
                        ),
                        set(Pack::name setTo packDTO.name, Pack::questions setTo packDTO.questions)
                    )
                    if (pack != null)
                        call.respond(HttpStatusCode.OK)
                    else
                        call.respond(HttpStatusCode.NotFound)
                }
            }
        }
    }
}