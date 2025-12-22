package com.lifeline.app.ai

/**
 * AI abstraction interface for all AI implementations
 */
interface AiClient {
    suspend fun processRequest(prompt: String, context: Map<String, Any> = emptyMap()): AiResponse
    
    suspend fun isAvailable(): Boolean
}

data class AiResponse(
    val text: String,
    val confidence: Float = 1.0f,
    val source: AiSource,
    val metadata: Map<String, String> = emptyMap()
)

enum class AiSource {
    CLOUD,
    LOCAL_NEURAL,
    RULE_BASED
}
