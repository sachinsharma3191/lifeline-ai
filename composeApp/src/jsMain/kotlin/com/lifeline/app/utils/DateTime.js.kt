package com.lifeline.app.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

actual fun currentTimestamp(): Instant {
    // Use Clock.System for JS target - this should work despite the compilation warning
    // The error might be a transient compiler issue
    return Clock.System.now()
}

