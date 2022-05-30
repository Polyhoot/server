package net.ciphen.polyhoot.test.api

import net.ciphen.polyhoot.domain.CreateUserDTO
import net.ciphen.polyhoot.domain.CreateUserResponse
import net.ciphen.polyhoot.domain.LoginDTO
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
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.testing.*
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.runners.MethodSorters
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation.Plugin as ContentNegotiationPlugin

@FixMethodOrder(MethodSorters.JVM)
class ApplicationTest {
    private fun getClient() =
        HttpClient(Java) {
            install(ContentNegotiationPlugin) {
                json()
            }
        }

    @Before
    fun waitServer() {
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
    fun `valid user registration`() = testApplication {
        // Create user
        val response = getClient().post("http://localhost:8080/api/user/create") {
            contentType(ContentType.Application.Json)
            setBody(CreateUserDTO("Test", "helloworld", "test@yahoo.com"))
        }
        val responseData: CreateUserResponse = response.body()
        assertEquals(response.status, HttpStatusCode.OK)
        assertNotNull(responseData.token)
    }

    @Test
    fun `user registration with same email`() = testApplication {
        // Create the same user, expect to return 409
        // TODO: we don't have server-side data validation

        val responseIncorrect = getClient().post("http://localhost:8080/api/user/create") {
            contentType(ContentType.Application.Json)
            setBody(CreateUserDTO("Test", "asdasd", "test@yahoo.com"))
        }
        val responseIncorrectData: CreateUserResponse = responseIncorrect.body()

        assertEquals(responseIncorrect.status, HttpStatusCode.Conflict)
        assertNull(responseIncorrectData.token)
    }

    @Test
    fun `user login with valid credits` () = testApplication {
        val response = getClient().post("http://localhost:8080/api/user/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginDTO( "helloworld", "test@yahoo.com"))
        }
        val responseData: CreateUserResponse = response.body()

        assertEquals(response.status, HttpStatusCode.OK)
        assertNotNull(responseData.token)
    }

}