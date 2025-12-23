package com.lifeline.app.utils

import java.util.Date

actual fun getCurrentTimestamp(): Long {
    return Date().time
}
