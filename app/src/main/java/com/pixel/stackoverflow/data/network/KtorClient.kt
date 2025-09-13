package com.pixel.stackoverflow.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.accept
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json


class KtorClient() {

    private val _client = HttpClient(OkHttp) {
        defaultRequest {
            url {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                protocol = URLProtocol.Companion.HTTPS

                // URL could be kept in build config, externally managed etc
                url("https://api.stackexchange.com/2.2")
            }
        }

        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }

        // Log JSON body, useful for debugging
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.BODY
        }

        install(HttpTimeout) {
            this.requestTimeoutMillis = 5000
        }
    }

    fun getClient() = _client
}