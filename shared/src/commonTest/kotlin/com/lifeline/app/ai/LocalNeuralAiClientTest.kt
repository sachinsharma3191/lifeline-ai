package com.lifeline.app.ai

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LocalNeuralAiClientTest {
    private val client = LocalNeuralAiClient()
    
    @Test
    fun `test process request`() = runTest {
        val response = client.processRequest("test prompt", emptyMap())
        
        assertEquals(AiSource.LOCAL_NEURAL, response.source)
        assertTrue(response.text.isNotBlank())
        assertTrue(response.confidence > 0f)
        assertTrue(response.metadata.containsKey("model"))
    }
    
    @Test
    fun `test is available`() = runTest {
        assertTrue(client.isAvailable())
    }
    
    @Test
    fun `test load model`() = runTest {
        val result = client.loadModel()
        assertTrue(result)
    }
    
    @Test
    fun `test unload model`() {
        client.unloadModel()
        // Should not throw
    }
    
    @Test
    fun `test health domain response`() = runTest {
        val response = client.processRequest("I have health concerns", emptyMap())
        assertTrue(response.text.contains("health", ignoreCase = true))
    }
}
