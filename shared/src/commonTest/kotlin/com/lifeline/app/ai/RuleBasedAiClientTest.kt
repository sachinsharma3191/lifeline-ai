package com.lifeline.app.ai

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RuleBasedAiClientTest {
    private val client = RuleBasedAiClient()
    
    @Test
    fun `test health prompt`() = runTest {
        val response = client.processRequest("I have a headache")
        
        assertEquals(AiSource.RULE_BASED, response.source)
        assertTrue(response.text.contains("health", ignoreCase = true))
        assertTrue(response.confidence > 0f)
    }
    
    @Test
    fun `test finance prompt`() = runTest {
        val response = client.processRequest("How much money do I have?")
        
        assertEquals(AiSource.RULE_BASED, response.source)
        assertTrue(response.text.contains("money", ignoreCase = true) || 
                  response.text.contains("finance", ignoreCase = true))
    }
    
    @Test
    fun `test learning prompt`() = runTest {
        val response = client.processRequest("I want to learn Kotlin")
        
        assertEquals(AiSource.RULE_BASED, response.source)
        assertTrue(response.text.contains("learn", ignoreCase = true))
    }
    
    @Test
    fun `test services prompt`() = runTest {
        val response = client.processRequest("Find community services")
        
        assertEquals(AiSource.RULE_BASED, response.source)
        assertTrue(response.text.contains("service", ignoreCase = true) || 
                  response.text.contains("community", ignoreCase = true))
    }
    
    @Test
    fun `test generic prompt`() = runTest {
        val response = client.processRequest("Hello")
        
        assertEquals(AiSource.RULE_BASED, response.source)
        assertTrue(response.text.isNotBlank())
    }
    
    @Test
    fun `test is always available`() = runTest {
        assertTrue(client.isAvailable())
    }
    
    @Test
    fun `test with context`() = runTest {
        val context = mapOf("userId" to "123", "domain" to "health")
        val response = client.processRequest("Help me", context)
        
        assertEquals(AiSource.RULE_BASED, response.source)
        assertTrue(response.text.isNotBlank())
    }
}
