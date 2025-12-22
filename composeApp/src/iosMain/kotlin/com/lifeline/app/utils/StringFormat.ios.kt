package com.lifeline.app.utils

actual fun formatDouble(value: Double, decimals: Int): String {
    // iOS doesn't have String.format, use manual formatting
    val multiplier = kotlin.math.pow(10.0, decimals.toDouble())
    val rounded = (value * multiplier).roundToInt().toDouble() / multiplier
    return "%.${decimals}f".format(rounded)
}

