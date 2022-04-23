package org.ciphen.polyhoot.routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.userRouting() {
    routing {
        route("/api/user") {
            get("/create") {
                call.respondText("Hello")
            }
        }
    }
}