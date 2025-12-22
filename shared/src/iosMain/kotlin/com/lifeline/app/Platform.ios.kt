package com.lifeline.app

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin

class IosPlatform : Platform {
    override val name: String = "iOS"
}

actual fun getPlatform(): Platform = IosPlatform()

actual fun createHttpClientEngine(): HttpClientEngine = Darwin.create()