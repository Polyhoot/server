package org.ciphen.polyhoot.routes

import com.aventrix.jnanoid.jnanoid.NanoIdUtils
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.ciphen.polyhoot.domain.FileResponse
import org.ciphen.polyhoot.domain.Pack
import org.ciphen.polyhoot.domain.PackResponse
import org.ciphen.polyhoot.domain.User
import org.litote.kmongo.eq
import java.io.File

fun Application.fileRouting() {
    routing {
        authenticate("auth-jwt") {
            post("file/upload") { _ ->
                val multipart = call.receiveMultipart()
                val fileId = NanoIdUtils.randomNanoId()
                var fileName = ""
                multipart.forEachPart { part ->
                    if(part is PartData.FileItem) {
                        val name = part.originalFileName!!
                        fileName = name
                        val file = File("uploads/${fileId}_${name}")
                        println(file.absolutePath)
                        part.streamProvider().use { its ->
                            file.outputStream().buffered().use {
                                its.copyTo(it)
                            }
                        }
                    }
                    part.dispose()
                }
                call.respond(
                    HttpStatusCode.OK, FileResponse(
                    url = "/file/get/${fileId}_${fileName}"
                ))
            }
            get("file/get/{name}") {
                if (call.parameters["name"]?.isNotEmpty() == true) {
                    val file = File("uploads/${call.parameters["name"]}")
                    if (file.exists()) {
                        call.respondFile(file)
                    }
                }
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }

}