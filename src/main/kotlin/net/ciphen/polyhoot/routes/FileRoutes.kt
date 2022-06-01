/*
 * Copyright (C) 2022 The Polyhoot Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ciphen.polyhoot.routes

import com.aventrix.jnanoid.jnanoid.NanoIdUtils
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.ciphen.polyhoot.domain.FileResponse
import java.io.File

fun Application.fileRouting() {
    routing {
        authenticate("auth-jwt") {
            post("file/upload") { _ ->
                val multipart = call.receiveMultipart()
                val fileId = NanoIdUtils.randomNanoId()
                var fileName = ""
                multipart.forEachPart { part ->
                    if (part is PartData.FileItem) {
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
                    )
                )
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