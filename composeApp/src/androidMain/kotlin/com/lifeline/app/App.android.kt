package com.lifeline.app

import android.content.Context
import com.lifeline.app.database.DatabaseDriverFactory

actual fun createDatabaseDriverFactory(): DatabaseDriverFactory {
    // Get the application context using a more reliable method
    // This uses the ActivityThread to get the current application context
    val context = try {
        val activityThread = Class.forName("android.app.ActivityThread")
        val currentApplication = activityThread.getMethod("currentApplication")
        currentApplication.invoke(null) as? android.app.Application
    } catch (e: Exception) {
        null
    }?.applicationContext
    
    ?: throw IllegalStateException(
        "Cannot get Android context. Make sure this is called from an Android application context."
    )
    
    return DatabaseDriverFactory(context)
}

