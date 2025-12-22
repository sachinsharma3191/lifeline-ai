package com.lifeline.app.utils

// JS doesn't have native UUID, use a simple implementation
actual fun randomUUID(): String {
    return "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(Regex("[xy]")) { matchResult ->
        // Use JavaScript's Math.random() via dynamic call
        val randomValue = js("Math.random()") as Double
        val r = (randomValue * 16.0).toInt()
        val v = if (matchResult.value == "x") {
            r
        } else {
            (r and 0x3) or 0x8
        }
        v.toString(16)
    }
}

