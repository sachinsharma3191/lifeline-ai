package com.lifeline.app.utils

import kotlin.js.Date

actual fun getCurrentTimestamp(): Long {
    return Date().getTime().toLong()
}
