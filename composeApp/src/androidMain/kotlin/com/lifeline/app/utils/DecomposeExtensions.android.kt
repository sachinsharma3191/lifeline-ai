package com.lifeline.app.utils

import androidx.compose.runtime.*
import com.arkivanov.decompose.value.Value

@Composable
actual fun Value<*>.subscribeAsState(): State<Any?> {
    // For Android, use manual observation since decompose-compose is not available
    val state = remember { mutableStateOf<Any?>(value) }
    
    DisposableEffect(this) {
        val subscription = subscribe { newValue ->
            state.value = newValue
        }
        onDispose {
            subscription.cancel()
        }
    }
    
    return state
}

