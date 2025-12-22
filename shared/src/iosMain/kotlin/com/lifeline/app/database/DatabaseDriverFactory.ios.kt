package com.lifeline.app.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        // SQLDelight 2.0 with async generation - use the schema directly
        // The driver will handle the async schema internally
        return NativeSqliteDriver(
            schema = LifelineDatabase.Schema,
            name = "lifeline.db"
        )
    }
}
