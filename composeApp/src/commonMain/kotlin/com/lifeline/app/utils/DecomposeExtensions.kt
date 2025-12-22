package com.lifeline.app.utils

import androidx.compose.runtime.*
import com.arkivanov.decompose.value.Value

/**
 * Expect function to observe a Decompose Value as Compose State
 * Platform-specific implementations will use decompose-compose where available
 * Note: Using type erasure to avoid type constraint issues with wildcards
 */
@Composable
expect fun Value<*>.subscribeAsState(): State<Any?>

