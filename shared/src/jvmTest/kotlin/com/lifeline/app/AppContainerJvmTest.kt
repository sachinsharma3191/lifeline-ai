package com.lifeline.app

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.JdbcDriver
import com.lifeline.app.database.DatabaseDriverFactory
import com.lifeline.app.database.LifelineDatabase
import java.sql.DriverManager

class AppContainerJvmTest : AppContainerTest() {
    override fun createDatabaseDriverFactory(): DatabaseDriverFactory {
        return object : DatabaseDriverFactory() {
            override fun createDriver(): SqlDriver {
                val connection = DriverManager.getConnection("jdbc:sqlite::memory:")
                val driver = JdbcDriver(connection)
                LifelineDatabase.Schema.create(driver)
                return driver
            }
        }
    }
}

