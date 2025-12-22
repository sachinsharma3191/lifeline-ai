package com.lifeline.app.utils

import kotlinx.datetime.Instant

actual fun currentTimestamp(): Instant {
    // Use JavaScript Date for JS target
    return Instant.fromEpochMilliseconds(kotlin.js.Date.now().toLong())
}

