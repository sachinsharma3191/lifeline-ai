package com.lifeline.app.ai

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Multi-layer AI client with fallback strategy:
 * 1. Try Cloud AI
 * 2. Try Offline Insights AI (always available, data-driven)
 * 3. Try Local Neural AI (if available)
 * 4. Fallback to Rule-Based AI (always available)
 */
class MultiLayerAiClient(
    private val cloudAi: AiClient?,
    private val localNeuralAi: AiClient?,
    private val offlineInsightsAi: AiClient = OfflineInsightsAiClient(),
    private val ruleBasedAi: AiClient = RuleBasedAiClient()
) : AiClient {
    
    override suspend fun processRequest(prompt: String, context: Map<String, Any>): AiResponse {
        // Try Cloud AI first
        cloudAi?.let { client ->
            if (client.isAvailable()) {
                try {
                    return client.processRequest(prompt, context)
                } catch (e: Exception) {
                    // Fall through to next layer
                }
            }
        }
        
        // Always-available offline insights (data-driven)
        try {
            return offlineInsightsAi.processRequest(prompt, context)
        } catch (e: Exception) {
            // Fall through to next layer
        }

        // Try Local Neural AI
        localNeuralAi?.let { client ->
            if (client.isAvailable()) {
                try {
                    return client.processRequest(prompt, context)
                } catch (e: Exception) {
                    // Fall through to rule-based
                }
            }
        }
        
        // Always fallback to rule-based
        return ruleBasedAi.processRequest(prompt, context)
    }
    
    override suspend fun isAvailable(): Boolean = true // Always available via rule-based
    
    /**
     * Process request with streaming responses (for future use)
     */
    fun processRequestStreaming(prompt: String, context: Map<String, Any> = emptyMap()): Flow<AiResponse> = flow {
        emit(processRequest(prompt, context))
    }
}
