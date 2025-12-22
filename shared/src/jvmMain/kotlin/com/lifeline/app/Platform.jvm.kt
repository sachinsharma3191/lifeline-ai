package com.lifeline.app

import com.lifeline.app.database.DatabaseDriverFactory
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.java.Java

class JvmPlatform : Platform {
    override val name: String = "JVM"
}

actual fun getPlatform(): Platform = JvmPlatform()

actual fun createHttpClientEngine(): HttpClientEngine = Java.create()

actual fun createDatabaseDriverFactory(): DatabaseDriverFactory {
    return DatabaseDriverFactory()
}