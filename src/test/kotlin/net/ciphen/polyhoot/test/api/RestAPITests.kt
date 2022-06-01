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

package net.ciphen.polyhoot.test.api

import net.ciphen.polyhoot.utils.Log
import java.net.BindException
import de.bwaldvogel.mongo.MongoServer
import de.bwaldvogel.mongo.backend.memory.MemoryBackend
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.java.*
import io.ktor.client.request.*
import io.ktor.http.*
import net.ciphen.polyhoot.Application
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import net.ciphen.polyhoot.domain.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation.Plugin as ContentNegotiationPlugin

private val packQuestions = mutableListOf<Question>(
    Question(
        id = "123456",
        text = "Hello world",
        time = 20,
        type = 0,
        answers = mutableListOf(
            Answer(
                "hello 1",
                false
            ),
            Answer(
                "hello 2",
                false
            ),
            Answer(
                "hello 3",
                false
            ),
            Answer(
                "hello 4",
                true
            )
        ),
        media = null,
    )
)

// Will receive data in tests
private var token: String? = null
private lateinit var packId: String

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class ApplicationTest {
    private fun getClient() =
        HttpClient(Java) {
            install(ContentNegotiationPlugin) {
                json()
            }
        }

    @BeforeAll
    private fun waitServer() {
        Thread {
            try {
                MongoServer(MemoryBackend()).bind("localhost", 45678)
            } catch (_: BindException) {
            }
            // TODO: Don't use the main Application
            Application.main(arrayOf())
        }.start()
        while (!Application.getInstance().serverUp) {
            Log.e("Tests:", "Server is not up yet.")
            Thread.sleep(100)
        }
    }

    @Test
    @Order(1)
    fun validUserRegistration() = testApplication {
        // Create user
        val response = getClient().post("http://localhost:8080/api/user/create") {
            contentType(ContentType.Application.Json)
            setBody(CreateUserDTO("Ivan Ivanov", "helloworld", "test@yahoo.com"))
        }
        val responseData: CreateUserResponse = response.body()
        assertEquals(HttpStatusCode.OK, response.status)
        assertNotNull(responseData.token)
    }

    @Test
    @Order(2)
    fun userRegistrationWithSameEmail() = testApplication {
        // Create the same user, expect to return 409
        // TODO: we don't have server-side data validation

        val responseIncorrect = getClient().post("http://localhost:8080/api/user/create") {
            contentType(ContentType.Application.Json)
            setBody(CreateUserDTO("Test", "asdasd", "test@yahoo.com"))
        }
        val responseIncorrectData: CreateUserResponse = responseIncorrect.body()

        assertEquals(HttpStatusCode.Conflict, responseIncorrect.status)
        assertNull(responseIncorrectData.token)
    }

    @Test
    @Order(3)
    fun userLoginWithValidCredits() = testApplication {
        val response = getClient().post("http://localhost:8080/api/user/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginDTO("helloworld", "test@yahoo.com"))
        }
        val responseData: CreateUserResponse = response.body()

        assertEquals(HttpStatusCode.OK, response.status)
        assertNotNull(responseData.token)
        assertTrue(!responseData.token.isNullOrBlank())
        token = responseData.token!!
    }

    @Test
    @Order(4)
    fun userLoginWithInvalidCredits() = testApplication {
        val response = getClient().post("http://localhost:8080/api/user/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginDTO("test", "test@yahoo.com"))
        }
        val responseData: CreateUserResponse = response.body()

        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertNull(responseData.token)
    }

    @Test
    @Order(4)
    fun invalidUserLogin() = testApplication {
        val response = getClient().post("http://localhost:8080/api/user/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginDTO("test", "notfound@yahoo.com"))
        }
        val responseData: CreateUserResponse = response.body()

        assertEquals(HttpStatusCode.NotFound, response.status)
        assertNull(responseData.token)
    }

    @Test
    @Order(5)
    fun getUserInfo() = testApplication {
        println(token)
        val response = getClient().get("http://localhost:8080/api/user/info") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
        }
        val responseData: UserDataResponse = response.body()

        assertEquals(HttpStatusCode.OK, response.status)
        assertNotNull(responseData.email)
        assertEquals("Ivan Ivanov", responseData.name)
    }

    @Test
    @Order(6)
    fun savePack() = testApplication {
        println(token)
        val response = getClient().post("http://localhost:8080/api/pack/autosave") {
            contentType(ContentType.Application.Json)
            setBody(
                CreatePackDTO(
                    "Test pack",
                    packQuestions
                )
            )
            header("Authorization", "Bearer $token")
        }
        val responseData: AutosaveInfoResponse = response.body()

        assertEquals(HttpStatusCode.OK, response.status)
        assertNotNull(responseData.packId)
        packId = responseData.packId
    }

    @Test
    @Order(7)
    fun getMyPacks() = testApplication {
        val response = getClient().get("http://localhost:8080/api/pack/get/my") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
        }
        val responseData: GetPacksResponse = response.body()

        assertEquals(HttpStatusCode.OK, response.status)
        assertNotNull(responseData.packs)
        assertTrue(responseData.packs.size == 1)
        assertEquals("Test pack", responseData.packs[0].name)
    }

    @Test
    @Order(8)
    fun saveNotFoundPack() = testApplication {
        println(token)
        val response = getClient().post("http://localhost:8080/api/pack/save") {
            contentType(ContentType.Application.Json)
            setBody(
                SavePackDTO(
                    "helloworld",
                    "Test pack",
                    packQuestions
                )
            )
            header("Authorization", "Bearer $token")
        }

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    @Order(8)
    fun editPack() = testApplication {
        val response = getClient().post("http://localhost:8080/api/pack/save") {
            contentType(ContentType.Application.Json)
            setBody(SavePackDTO(packId, "Hello world", packQuestions))
            header("Authorization", "Bearer $token")
        }

        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    @Order(9)
    fun checkUpdatedPack() = testApplication {
        val response = getClient().get("http://localhost:8080/api/pack/get/$packId") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
        }
        val responseData: PackResponse = response.body()

        assertEquals(HttpStatusCode.OK, response.status)
        assertNotNull(responseData.name)
        assertTrue(responseData.questions.isNotEmpty())
        assertEquals("Hello world", responseData.name)
    }

    @Test
    @Order(9)
    fun getNotExistingPack() = testApplication {
        val response = getClient().get("http://localhost:8080/api/pack/get/helloworld") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
        }

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

}