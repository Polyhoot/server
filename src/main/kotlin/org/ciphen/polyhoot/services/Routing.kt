package org.ciphen.polyhoot.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText(JWT.create().withClaim("hello", "world").sign(Algorithm.HMAC256("hellow")))
        }
    }
}