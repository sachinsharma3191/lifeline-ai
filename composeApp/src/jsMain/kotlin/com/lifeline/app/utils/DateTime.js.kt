package com.lifeline.app.utils

import kotlin.js.Date

actual fun currentTimestamp(): Long {
    return Date().getTime().toLong()
}
