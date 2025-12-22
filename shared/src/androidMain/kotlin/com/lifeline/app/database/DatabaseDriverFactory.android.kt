package com.lifeline.app.database

import android.content.Context
import app.cash.sqldelight.android.AndroidSqlDriver
import app.cash.sqldelight.db.SqlDriver

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqlDriver(
            LifelineDatabase.Schema,
            context,
            "lifeline.db"
        )
    }
}
