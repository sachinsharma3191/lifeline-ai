package com.lifeline.app.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.drivers.native.NativeSqlDriver

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqlDriver(
            LifelineDatabase.Schema,
            "lifeline.db"
        )
    }
}
