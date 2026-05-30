package com.lifeline.app.config

import kotlinx.serialization.json.Json

object AppConfigLoader {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private var cached: AppConfig? = null

    fun get(): AppConfig {
        return cached ?: decodeConfig(readConfigJson()).also { cached = it }
    }

    fun reload(): AppConfig {
        cached = null
        return get()
    }

    internal fun decodeConfig(raw: String): AppConfig = json.decodeFromString(raw)
}

expect fun readConfigJson(): String
