package com.lifeline.app.datetime

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

// This module is a thin wrapper around kotlinx.datetime
// By using 'api' dependency, it re-exports kotlinx.datetime types
// This ensures kotlinx.datetime is only compiled once for JS (in this module)
// Both shared and composeApp depend on this module instead of directly on kotlinx.datetime
// 
// The types are still available as kotlinx.datetime.Instant, etc.
// because api dependencies are transitively exported

/**
 * Helper function to get current timestamp using Clock.System
 * This ensures Clock.System is properly accessible across all platforms
 */
fun currentSystemTime(): Instant = Clock.System.now()

