package com.lifeline.app.domain.health

import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class HealthModelsTest {
    @Test
    fun `test symptom creation`() {
        val symptom = Symptom(
            id = "symptom1",
            name = "Headache",
            severity = 5,
            timestamp = Clock.System.now(),
            notes = "Test notes",
            category = SymptomCategory.PAIN
        )
        
        assertEquals("symptom1", symptom.id)
        assertEquals("Headache", symptom.name)
        assertEquals(5, symptom.severity)
        assertEquals(SymptomCategory.PAIN, symptom.category)
        assertNotNull(symptom.notes)
    }
    
    @Test
    fun `test symptom without notes`() {
        val symptom = Symptom(
            id = "symptom1",
            name = "Headache",
            severity = 5,
            timestamp = Clock.System.now(),
            notes = null,
            category = SymptomCategory.PAIN
        )
        
        assertEquals(null, symptom.notes)
    }
    
    @Test
    fun `test health timeline entry`() {
        val symptom = Symptom(
            id = "symptom1",
            name = "Headache",
            severity = 5,
            timestamp = Clock.System.now(),
            category = SymptomCategory.PAIN
        )
        
        val entry = HealthTimelineEntry(
            id = "entry1",
            timestamp = Clock.System.now(),
            symptoms = listOf(symptom),
            notes = "Test entry"
        )
        
        assertEquals("entry1", entry.id)
        assertEquals(1, entry.symptoms.size)
        assertEquals(symptom, entry.symptoms[0])
    }
    
    @Test
    fun `test symptom category enum`() {
        val categories = SymptomCategory.values()
        assertTrue(categories.isNotEmpty())
        assertTrue(categories.contains(SymptomCategory.PAIN))
    }
}
