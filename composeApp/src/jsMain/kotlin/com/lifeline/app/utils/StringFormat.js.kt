package com.lifeline.app.utils

actual fun formatDouble(value: Double, decimals: Int): String {
    // JS doesn't have String.format, use manual formatting with toFixed-like behavior
    val multiplier = kotlin.math.pow(10.0, decimals.toDouble())
    val rounded = kotlin.math.round(value * multiplier) / multiplier
    return buildString {
        val parts = rounded.toString().split('.')
        append(parts[0])
        if (parts.size > 1) {
            append('.')
            append(parts[1].padEnd(decimals, '0').take(decimals))
        } else {
            append('.')
            repeat(decimals) { append('0') }
        }
    }
}

