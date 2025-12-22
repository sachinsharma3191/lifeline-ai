package com.lifeline.app.database

import app.cash.sqldelight.db.SqlDriver

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        // JS database driver is not currently configured
        // SQLDelight sqljs-driver may not be available in version 2.0.2
        // For now, JS platform database operations will throw this exception
        // To fix: Add sqljs-driver dependency or use a different SQLDelight version
        throw UnsupportedOperationException(
            "SQLDelight JS driver is not available. " +
            "Database operations are not supported on JS platform in this build. " +
            "Consider using IndexedDB or localStorage for JS platform persistence."
        )
    }
}
