package com.lifeline.app.ai

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

/**
 * Cloud AI client using HTTP/LLM API
 */
class CloudAiClient(
    private val httpClient: HttpClient,
    private val apiUrl: String = "https://api.example.com/ai",
    private val apiKey: String? = null
) : AiClient {
    
    @Serializable
    private data class CloudRequest(
        val prompt: String,
        val context: Map<String, String> = emptyMap()
    )
    
    @Serializable
    private data class CloudResponse(
        val text: String,
        val confidence: Float = 1.0f
    )
    
    override suspend fun processRequest(prompt: String, context: Map<String, Any>): AiResponse {
        return try {
            val response = httpClient.post(apiUrl) {
                contentType(ContentType.Application.Json)
                apiKey?.let { header("Authorization", "Bearer $it") }
                setBody(CloudRequest(prompt, context.mapValues { it.value.toString() }))
            }.body<CloudResponse>()
            
            AiResponse(
                text = response.text,
                confidence = response.confidence,
                source = AiSource.CLOUD,
                metadata = mapOf("api" to "cloud")
            )
        } catch (e: Exception) {
            throw AiException("Cloud AI unavailable", e)
        }
    }
    
    override suspend fun isAvailable(): Boolean {
        return try {
            httpClient.head(apiUrl).status.isSuccess()
        } catch (e: Exception) {
            false
        }
    }
}

class AiException(message: String, cause: Throwable? = null) : Exception(message, cause)
