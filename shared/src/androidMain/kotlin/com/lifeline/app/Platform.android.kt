package com.lifeline.app

import android.os.Build
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.Android

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual fun createHttpClientEngine(): HttpClientEngine = Android.create()