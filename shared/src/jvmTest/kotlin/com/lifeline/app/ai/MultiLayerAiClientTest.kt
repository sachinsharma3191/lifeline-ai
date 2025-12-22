package com.lifeline.app.ai

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MultiLayerAiClientTest {
    
    @Test
    fun `test cloud AI is used when available`() = runTest {
        val cloudAi = mockk<AiClient>()
        val ruleBasedAi = mockk<AiClient>()
        
        coEvery { cloudAi.isAvailable() } returns true
        coEvery { cloudAi.processRequest(any(), any()) } returns AiResponse(
            text = "Cloud response",
            source = AiSource.CLOUD
        )
        
        val client = MultiLayerAiClient(cloudAi, null, ruleBasedAi)
        val response = client.processRequest("test")
        
        assertEquals(AiSource.CLOUD, response.source)
        assertEquals("Cloud response", response.text)
        coVerify(exactly = 0) { ruleBasedAi.processRequest(any(), any()) }
    }
    
    @Test
    fun `test local neural AI is used when cloud unavailable`() = runTest {
        val cloudAi = mockk<AiClient>()
        val localNeuralAi = mockk<AiClient>()
        val ruleBasedAi = mockk<AiClient>()
        
        coEvery { cloudAi.isAvailable() } returns false
        coEvery { localNeuralAi.isAvailable() } returns true
        coEvery { localNeuralAi.processRequest(any(), any()) } returns AiResponse(
            text = "Local neural response",
            source = AiSource.LOCAL_NEURAL
        )
        
        val client = MultiLayerAiClient(cloudAi, localNeuralAi, ruleBasedAi)
        val response = client.processRequest("test")
        
        assertEquals(AiSource.LOCAL_NEURAL, response.source)
        assertEquals("Local neural response", response.text)
        coVerify(exactly = 0) { ruleBasedAi.processRequest(any(), any()) }
    }
    
    @Test
    fun `test fallback to rule-based when all fail`() = runTest {
        val cloudAi = mockk<AiClient>()
        val localNeuralAi = mockk<AiClient>()
        val ruleBasedAi = RuleBasedAiClient()
        
        coEvery { cloudAi.isAvailable() } returns false
        coEvery { localNeuralAi.isAvailable() } returns false
        
        val client = MultiLayerAiClient(cloudAi, localNeuralAi, ruleBasedAi)
        val response = client.processRequest("test")
        
        assertEquals(AiSource.RULE_BASED, response.source)
        assertNotNull(response.text)
    }
    
    @Test
    fun `test exception handling in cloud AI`() = runTest {
        val cloudAi = mockk<AiClient>()
        val ruleBasedAi = RuleBasedAiClient()
        
        coEvery { cloudAi.isAvailable() } returns true
        coEvery { cloudAi.processRequest(any(), any()) } throws Exception("Network error")
        
        val client = MultiLayerAiClient(cloudAi, null, ruleBasedAi)
        val response = client.processRequest("test")
        
        assertEquals(AiSource.RULE_BASED, response.source)
    }
    
    @Test
    fun `test is always available`() = runTest {
        val client = MultiLayerAiClient(null, null, RuleBasedAiClient())
        assertTrue(client.isAvailable())
    }
}
