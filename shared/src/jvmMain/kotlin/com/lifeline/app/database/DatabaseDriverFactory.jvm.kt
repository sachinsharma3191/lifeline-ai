package com.lifeline.app.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.asJdbcDriver
import org.sqlite.SQLiteDataSource
import javax.sql.DataSource

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        // Create a DataSource for SQLite in-memory database
        val dataSource = SQLiteDataSource().apply {
            url = "jdbc:sqlite::memory:"
        }
        // Use the extension function to create a JdbcDriver from a DataSource
        val driver = dataSource.asJdbcDriver()
        LifelineDatabase.Schema.create(driver)
        return driver
    }
}
