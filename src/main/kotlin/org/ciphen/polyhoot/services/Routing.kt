package org.ciphen.polyhoot.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.ciphen.polyhoot.db.DB
import org.ciphen.polyhoot.domain.User

fun Application.configureRouting() {
    routing {
//        singlePageApplication {
//            react("/home/vladdenisov/projects/web2/build")
//        }
    }
}