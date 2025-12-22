package com.lifeline.app.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        // Use fully qualified name to avoid import issues
        return app.cash.sqldelight.driver.android.AndroidSqliteDriver(
            LifelineDatabase.Schema,
            context,
            "lifeline.db"
        )
    }
}
