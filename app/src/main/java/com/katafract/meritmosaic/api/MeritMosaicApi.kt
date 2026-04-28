// MeritMosaicApi.kt — thin Ktor client for api.meritmosaic.io.
//
// Network calls are wrapped in `Result<T>` so callers can degrade to mock
// data without try/catch boilerplate. Each call returns a Result, and the
// ViewModel's pattern is `result.getOrElse { mockFallback() }`.
package com.katafract.meritmosaic.api

import com.katafract.meritmosaic.BuildConfig
import com.katafract.meritmosaic.data.AppConfig
import com.katafract.meritmosaic.data.ClarifyRequest
import com.katafract.meritmosaic.data.ClarifyResponse
import com.katafract.meritmosaic.data.JournalCreateRequest
import com.katafract.meritmosaic.data.JournalCreateResponse
import com.katafract.meritmosaic.data.JournalEntry
import com.katafract.meritmosaic.data.PromptBalance
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.client.call.body
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class MeritMosaicApi(
    private val tokenProvider: suspend () -> String? = { null }
) {
    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls     = false
        prettyPrint       = false
    }

    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) { json(json) }
        install(HttpTimeout) {
            requestTimeoutMillis = 15_000
            connectTimeoutMillis = 10_000
            socketTimeoutMillis  = 15_000
        }
        if (BuildConfig.DEBUG) {
            install(Logging) { level = LogLevel.INFO }
        }
        defaultRequest {
            contentType(ContentType.Application.Json)
        }
    }

    private suspend fun authHeaderValue(): String? = tokenProvider()?.let { "Bearer $it" }

    private val base = AppConfig.apiBaseURL

    suspend fun listJournal(): Result<List<JournalEntry>> = runCatching {
        val resp = client.get("$base/journal") {
            authHeaderValue()?.let { header("Authorization", it) }
        }
        json.decodeFromString(resp.bodyAsText())
    }

    suspend fun createJournalEntry(req: JournalCreateRequest): Result<JournalCreateResponse> = runCatching {
        client.post("$base/journal") {
            authHeaderValue()?.let { header("Authorization", it) }
            setBody(req)
        }.body()
    }

    suspend fun getBalance(): Result<PromptBalance> = runCatching {
        client.get("$base/prompts/balance") {
            authHeaderValue()?.let { header("Authorization", it) }
        }.body()
    }

    suspend fun clarify(req: ClarifyRequest): Result<ClarifyResponse> = runCatching {
        client.post("$base/ai/clarify") {
            authHeaderValue()?.let { header("Authorization", it) }
            setBody(req)
        }.body()
    }
}
