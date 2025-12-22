package com.lifeline.app.database

import app.cash.sqldelight.db.SqlDriver

// Note: SQLDelight doesn't support WASM runtime, but we need the type for expect/actual
actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        throw UnsupportedOperationException(
            "SQLDelight database is not supported on WASM platform. " +
            "Please use Android, iOS, JVM, or JS targets for database functionality."
        )
    }
}

