package com.lifeline.app.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.asJdbcDriver
import org.sqlite.SQLiteDataSource

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        val dbPath = System.getProperty("lifeline.db.path")
            ?: System.getenv("LIFELINE_DB_PATH")
            ?: "./lifeline.db"

        val dataSource = SQLiteDataSource().apply {
            url = "jdbc:sqlite:$dbPath"
        }

        val driver: SqlDriver = dataSource.asJdbcDriver()

        val hasSchema = driver
            .executeQuery(
                identifier = null,
                sql = "SELECT 1 FROM sqlite_master WHERE type='table' AND name='financial_transaction' LIMIT 1",
                mapper = { cursor -> cursor.next() },
                parameters = 0
            )
            .value
            .run { this == true }

        if (!hasSchema) {
            LifelineDatabase.Schema.create(driver)
        }

        return driver
    }
}
