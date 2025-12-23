package com.lifeline.app.utils

import kotlinx.datetime.Instant

/**
 * Multiplatform function to get current timestamp
 */
expect fun getCurrentTimestamp(): Long

/**
 * Helper function to convert Long timestamp to Instant
 * This allows composeApp to avoid direct kotlinx.datetime usage
 */
fun longToInstant(timestamp: Long): Instant = Instant.fromEpochMilliseconds(timestamp)
