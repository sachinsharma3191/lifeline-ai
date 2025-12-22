package com.lifeline.app.repository

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.asJdbcDriver
import com.lifeline.app.database.LifelineDatabase
import org.sqlite.SQLiteDataSource

class HealthRepositoryImplJvmTest : HealthRepositoryImplTest() {
    override fun createDriver(): SqlDriver {
        val dataSource = SQLiteDataSource().apply {
            url = "jdbc:sqlite::memory:"
        }
        val driver = dataSource.asJdbcDriver()
        LifelineDatabase.Schema.create(driver)
        return driver
    }
}

