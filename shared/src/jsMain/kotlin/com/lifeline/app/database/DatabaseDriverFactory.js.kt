package com.lifeline.app.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.drivers.web.WebSqlDriver

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return WebSqlDriver(
            LifelineDatabase.Schema,
            "lifeline.db"
        )
    }
}
