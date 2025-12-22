package com.lifeline.app.utils

import platform.Foundation.NSUUID

actual fun randomUUID(): String {
    return NSUUID().UUIDString()
}

