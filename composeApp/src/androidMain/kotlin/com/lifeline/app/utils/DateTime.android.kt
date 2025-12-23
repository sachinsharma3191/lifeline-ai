package com.lifeline.app.utils

import java.util.Date

actual fun currentTimestamp(): Long {
    return Date().time
}
