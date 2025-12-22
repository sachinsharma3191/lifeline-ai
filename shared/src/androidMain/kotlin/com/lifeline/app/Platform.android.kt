package com.lifeline.app

import android.content.Context
import android.os.Build
import com.lifeline.app.database.DatabaseDriverFactory
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.Android

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual fun createHttpClientEngine(): HttpClientEngine = Android.create()

actual fun createDatabaseDriverFactory(): DatabaseDriverFactory {
    val context = android.app.Application::class.java
        .getMethod("getApplicationContext")
        .invoke(null) as? Context
        ?: throw IllegalStateException("Cannot get Android context")
    return DatabaseDriverFactory(context)
}