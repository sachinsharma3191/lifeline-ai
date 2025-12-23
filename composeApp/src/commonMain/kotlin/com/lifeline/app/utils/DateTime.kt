package com.lifeline.app.utils

/**
 * Multiplatform function to get current timestamp
 * Returns a Long timestamp (milliseconds since epoch) to avoid kotlinx.datetime dependency issues
 */
expect fun currentTimestamp(): Long
