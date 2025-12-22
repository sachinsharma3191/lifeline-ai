package com.lifeline.app

import com.lifeline.app.database.DatabaseDriverFactory

actual fun createDatabaseDriverFactory(): DatabaseDriverFactory {
    // JS doesn't support SQLDelight database
    throw UnsupportedOperationException("Database is not supported on JS target")
}

