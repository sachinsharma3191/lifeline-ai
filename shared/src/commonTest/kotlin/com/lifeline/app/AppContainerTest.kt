package com.lifeline.app

import app.cash.sqldelight.db.SqlDriver
import com.lifeline.app.database.DatabaseDriverFactory
import com.lifeline.app.database.LifelineDatabase
// MockK is only available in jvmTest, not commonTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull

abstract class AppContainerTest {
    abstract fun createDatabaseDriverFactory(): DatabaseDriverFactory
    
    @Test
    fun `test app container initialization`() {
        val driverFactory = createDatabaseDriverFactory()
        val container = AppContainer(driverFactory, null)
        
        assertNotNull(container.database)
        assertNotNull(container.aiClient)
        assertNotNull(container.healthRepository)
        assertNotNull(container.financeRepository)
        assertNotNull(container.learningRepository)
        assertNotNull(container.servicesRepository)
        assertNotNull(container.healthViewModel)
        assertNotNull(container.moneyViewModel)
        assertNotNull(container.learningViewModel)
        assertNotNull(container.servicesViewModel)
    }
    
    @Test
    fun `test cleanup`() {
        val driverFactory = createDatabaseDriverFactory()
        val container = AppContainer(driverFactory, null)
        
        container.cleanup()
        // Should not throw
    }
}
