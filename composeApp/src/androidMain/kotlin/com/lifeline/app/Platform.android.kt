package com.lifeline.app

import android.content.Context
import com.lifeline.app.database.DatabaseDriverFactory

actual fun createDatabaseDriverFactory(): DatabaseDriverFactory {
    val context = android.app.Application::class.java
        .getMethod("getApplicationContext")
        .invoke(null) as? Context
        ?: throw IllegalStateException("Cannot get Android context")
    return DatabaseDriverFactory(context)
}

