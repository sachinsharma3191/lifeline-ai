package com.lifeline.app

import com.lifeline.app.database.DatabaseDriverFactory

actual fun createDatabaseDriverFactory(): DatabaseDriverFactory {
    return DatabaseDriverFactory()
}
