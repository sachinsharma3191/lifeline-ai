package com.lifeline.app.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

actual fun currentTimestamp(): Instant = Clock.System.now()

