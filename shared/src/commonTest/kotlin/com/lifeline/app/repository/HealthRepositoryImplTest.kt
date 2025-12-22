package com.lifeline.app.repository

import app.cash.sqldelight.db.SqlDriver
import com.lifeline.app.database.LifelineDatabase
import com.lifeline.app.domain.health.HealthTimelineEntry
import com.lifeline.app.domain.health.Symptom
import com.lifeline.app.domain.health.SymptomCategory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

abstract class HealthRepositoryImplTest {
    abstract fun createDriver(): SqlDriver
    
    private lateinit var database: LifelineDatabase
    private lateinit var repository: HealthRepository
    
    @BeforeTest
    fun setup() {
        val driver = createDriver()
        database = LifelineDatabase(driver)
        repository = HealthRepositoryImpl(database)
    }
    
    @Test
    fun `test add symptom`() = runTest {
        val symptom = createTestSymptom()
        repository.addSymptom(symptom)
        
        val symptoms = repository.getSymptoms(null, null).first()
        assertEquals(1, symptoms.size)
        assertEquals(symptom.id, symptoms[0].id)
        assertEquals(symptom.name, symptoms[0].name)
    }
    
    @Test
    fun `test get symptoms with date range`() = runTest {
        val now = Clock.System.now()
        val symptom1 = createTestSymptom(timestamp = now)
        val symptom2 = createTestSymptom(
            id = "symptom2",
            timestamp = now.minus(kotlinx.datetime.DateTimeUnit.DAY, 2)
        )
        
        repository.addSymptom(symptom1)
        repository.addSymptom(symptom2)
        
        val symptoms = repository.getSymptoms(now.minus(kotlinx.datetime.DateTimeUnit.DAY, 1), null).first()
        assertEquals(1, symptoms.size)
        assertEquals(symptom1.id, symptoms[0].id)
    }
    
    @Test
    fun `test add timeline entry`() = runTest {
        val symptom = createTestSymptom()
        repository.addSymptom(symptom)
        
        val entry = HealthTimelineEntry(
            id = "entry1",
            timestamp = Clock.System.now(),
            symptoms = listOf(symptom),
            notes = "Test entry"
        )
        
        repository.addTimelineEntry(entry)
        
        val entries = repository.getTimelineEntries(null, null).first()
        assertEquals(1, entries.size)
        assertEquals(entry.id, entries[0].id)
        assertEquals(1, entries[0].symptoms.size)
    }
    
    @Test
    fun `test delete symptom`() = runTest {
        val symptom = createTestSymptom()
        repository.addSymptom(symptom)
        
        repository.deleteSymptom(symptom.id)
        
        val symptoms = repository.getSymptoms(null, null).first()
        assertTrue(symptoms.isEmpty())
    }
    
    @Test
    fun `test delete timeline entry`() = runTest {
        val entry = HealthTimelineEntry(
            id = "entry1",
            timestamp = Clock.System.now(),
            symptoms = emptyList()
        )
        
        repository.addTimelineEntry(entry)
        repository.deleteTimelineEntry(entry.id)
        
        val entries = repository.getTimelineEntries(null, null).first()
        assertTrue(entries.isEmpty())
    }
    
    private fun createTestSymptom(
        id: String = "symptom1",
        name: String = "Headache",
        severity: Int = 5,
        timestamp: kotlinx.datetime.Instant = Clock.System.now(),
        notes: String? = "Test notes",
        category: SymptomCategory = SymptomCategory.PAIN
    ) = Symptom(
        id = id,
        name = name,
        severity = severity,
        timestamp = timestamp,
        notes = notes,
        category = category
    )
}

// Platform-specific test implementations
class HealthRepositoryImplJvmTest : HealthRepositoryImplTest() {
    override fun createDriver(): SqlDriver {
        val driver = app.cash.sqldelight.drivers.jdbc.sqlite.JdbcSqliteDriver(
            app.cash.sqldelight.drivers.jdbc.sqlite.JdbcSqliteDriver.IN_MEMORY
        )
        LifelineDatabase.Schema.create(driver)
        return driver
    }
}
```

