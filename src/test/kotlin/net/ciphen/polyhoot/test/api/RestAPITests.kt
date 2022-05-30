package net.ciphen.polyhoot.test.api

import net.ciphen.polyhoot.utils.Log
import java.net.BindException
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import de.bwaldvogel.mongo.MongoServer
import de.bwaldvogel.mongo.backend.memory.MemoryBackend
import io.ktor.client.*
import io.ktor.client.call.*
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
            } catch (_: BindException) {}
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
            setBody(CreateUserDTO("Test", "helloworld", "test@yahoo.com"))
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

        assertEquals(HttpStatusCode.Conflict, responseIncorrect.status,)
        assertNull(responseIncorrectData.token)
    }

    @Test
    @Order(3)
    fun userLoginWithValidCredits() = testApplication {
        val response = getClient().post("http://localhost:8080/api/user/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginDTO( "helloworld", "test@yahoo.com"))
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
            setBody(LoginDTO( "test", "test@yahoo.com"))
        }
        val responseData: CreateUserResponse = response.body()

        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertNull(responseData.token)
    }

    @Test
    @Order(5)
    fun savePack() = testApplication {
        println(token)
        val response = getClient().post("http://localhost:8080/api/pack/autosave") {
            contentType(ContentType.Application.Json)
            setBody(CreatePackDTO(
                "Test pack",
                packQuestions
            ))
            header("Authorization", "Bearer $token")
        }
        val responseData: AutosaveInfoResponse = response.body()

        assertEquals(HttpStatusCode.OK, response.status,)
        assertNotNull(responseData.packId)
        packId = responseData.packId
    }

    @Test
    @Order(6)
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
    @Order(7)
    fun editPack() = testApplication {
        val response = getClient().post("http://localhost:8080/api/pack/save") {
            contentType(ContentType.Application.Json)
            setBody(SavePackDTO(packId, "Hello world", packQuestions))
            header("Authorization", "Bearer $token")
        }

        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    @Order(8)
    fun checkUpdatedPack() = testApplication {
        val response = getClient().get("http://localhost:8080/api/pack/get/my") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
        }
        val responseData: GetPacksResponse = response.body()

        assertEquals(HttpStatusCode.OK, response.status)
        assertNotNull(responseData.packs)
        assertTrue(responseData.packs.size == 1)
        assertEquals("Hello world", responseData.packs[0].name)
    }

}