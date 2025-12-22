package com.lifeline.app.utils

actual fun formatDouble(value: Double, decimals: Int): String {
    // JS doesn't have String.format, use manual formatting
    // Calculate multiplier manually (10^decimals)
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10.0 }
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

