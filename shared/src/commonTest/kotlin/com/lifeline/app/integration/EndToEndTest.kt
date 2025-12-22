package com.lifeline.app.integration

import app.cash.sqldelight.db.SqlDriver
import com.lifeline.app.AppContainer
import com.lifeline.app.database.DatabaseDriverFactory
import com.lifeline.app.database.LifelineDatabase
import com.lifeline.app.domain.finance.Transaction
import com.lifeline.app.domain.finance.TransactionType
import com.lifeline.app.domain.health.Symptom
import com.lifeline.app.domain.health.SymptomCategory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

abstract class EndToEndTest {
    abstract fun createDatabaseDriverFactory(): DatabaseDriverFactory
    
    private lateinit var container: AppContainer
    
    @BeforeTest
    fun setup() {
        val driverFactory = createDatabaseDriverFactory()
        container = AppContainer(driverFactory, null)
    }
    
    @Test
    fun `test complete health workflow`() = runTest {
        val symptom = Symptom(
            id = "symptom1",
            name = "Headache",
            severity = 5,
            timestamp = Clock.System.now(),
            category = SymptomCategory.PAIN
        )
        
        container.healthViewModel.addSymptom(symptom)
        
        val symptoms = container.healthViewModel.symptoms.first()
        assertEquals(1, symptoms.size)
        assertEquals(symptom.id, symptoms[0].id)
    }
    
    @Test
    fun `test complete finance workflow`() = runTest {
        val transaction = Transaction(
            id = "tx1",
            amount = 100.0,
            type = TransactionType.EXPENSE,
            category = "Food",
            timestamp = Clock.System.now()
        )
        
        container.moneyViewModel.addTransaction(transaction)
        
        val transactions = container.moneyViewModel.transactions.first()
        assertEquals(1, transactions.size)
    }
    
    @Test
    fun `test AI integration`() = runTest {
        container.healthViewModel.askAi("I have a headache")
        
        kotlinx.coroutines.delay(200)
        
        val uiState = container.healthViewModel.uiState.first()
        assertNotNull(uiState.aiResponse)
    }
}
