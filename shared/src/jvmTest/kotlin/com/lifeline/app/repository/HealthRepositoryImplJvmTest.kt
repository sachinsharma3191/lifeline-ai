package com.lifeline.app.repository

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.JdbcDriver
import com.lifeline.app.database.LifelineDatabase
import java.sql.DriverManager

class HealthRepositoryImplJvmTest : HealthRepositoryImplTest() {
    override fun createDriver(): SqlDriver {
        val connection = DriverManager.getConnection("jdbc:sqlite::memory:")
        val driver = JdbcDriver(connection)
        LifelineDatabase.Schema.create(driver)
        return driver
    }
}

