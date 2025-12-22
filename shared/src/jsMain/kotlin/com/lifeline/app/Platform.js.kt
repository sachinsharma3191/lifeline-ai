package com.lifeline.app

import com.lifeline.app.database.DatabaseDriverFactory
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.js.Js

actual fun createHttpClientEngine(): HttpClientEngine = Js.create()

class JsPlatform : Platform {
    override val name: String = "JavaScript"
}

actual fun getPlatform(): Platform = JsPlatform()

actual fun createDatabaseDriverFactory(): DatabaseDriverFactory {
    // JS doesn't support SQLDelight database, throw error or return null driver
    throw UnsupportedOperationException("Database is not supported on JS target")
}
