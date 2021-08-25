package com.wutsi.platform.security.test

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.RSAKeyProvider
import com.wutsi.platform.core.tracing.TracingContext
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpMethod.DELETE
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.POST
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import java.util.Date

open class TestRestTemplate(
    private val keyProvider: RSAKeyProvider = TestRSAKeyProvider(),
    private val scopes: List<String> = emptyList(),
    private val withAuthorizationHeader: Boolean = true,
) {
    private val rest = RestTemplate()

    fun <T> getForEntity(url: String, type: Class<T>): ResponseEntity<T> {
        return exchange(url, GET, "", type)
    }

    fun delete(url: String) {
        exchange(url, DELETE, "", Any::class.java)
    }

    fun <T> postForEntity(url: String, body: Any, type: Class<T>): ResponseEntity<T> {
        return exchange(url, POST, body, type)
    }

    private fun <T> exchange(url: String, method: HttpMethod, body: Any, type: Class<T>): ResponseEntity<T> {
        val headers = HttpHeaders()
        if (withAuthorizationHeader) {
            val token = createToken()
            headers["Authorization"] = listOf("Bearer $token")
        }
        headers[TracingContext.HEADER_CLIENT_ID] = listOf("test")
        headers[TracingContext.HEADER_TRACE_ID] = listOf("0000-0000-0000-0000")
        headers[TracingContext.HEADER_DEVICE_ID] = listOf("test")

        val request = HttpEntity(body, headers)
        return rest.exchange(url, method, request, type)
    }

    private fun createToken(): String =
        JWT.create()
            .withIssuer("wutsi-test")
            .withIssuedAt(Date())
            .withExpiresAt(Date(System.currentTimeMillis() + 86400))
            .withSubject("urn:user:wutsi:test")
            .withClaim("name", "test")
            .withClaim("scope", scopes)
            .sign(Algorithm.RSA256(keyProvider))
}
