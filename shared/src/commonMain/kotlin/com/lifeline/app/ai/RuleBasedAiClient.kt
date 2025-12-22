package com.lifeline.app.ai

import kotlinx.coroutines.delay

/**
 * Rule-based AI client that always works offline
 */
class RuleBasedAiClient : AiClient {
    override suspend fun processRequest(prompt: String, context: Map<String, Any>): AiResponse {
        delay(50) // Simulate processing
        
        val response = when {
            prompt.contains("health", ignoreCase = true) -> 
                "I can help with health tracking. Try logging symptoms or checking your health timeline."
            prompt.contains("money", ignoreCase = true) || prompt.contains("finance", ignoreCase = true) -> 
                "I can assist with financial planning. Check your transactions or set financial goals."
            prompt.contains("learn", ignoreCase = true) || prompt.contains("education", ignoreCase = true) -> 
                "I can help with learning goals. Track your progress or discover new modules."
            prompt.contains("service", ignoreCase = true) || prompt.contains("community", ignoreCase = true) -> 
                "I can help you find community services. Search by category or location."
            else -> 
                "I'm here to help with health, finance, learning, and community services. What would you like to know?"
        }
        
        return AiResponse(
            text = response,
            confidence = 0.7f,
            source = AiSource.RULE_BASED,
            metadata = mapOf("type" to "rule_based")
        )
    }
    
    override suspend fun isAvailable(): Boolean = true
}
