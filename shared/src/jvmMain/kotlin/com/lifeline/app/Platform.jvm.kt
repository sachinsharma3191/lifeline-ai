package com.lifeline.app

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.java.Java

actual fun createHttpClientEngine(): HttpClientEngine = Java.create()