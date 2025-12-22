package com.lifeline.app.utils

import androidx.compose.runtime.*
import com.arkivanov.decompose.value.Value

@Composable
actual fun Value<*>.subscribeAsState(): State<Any?> {
    // For iOS, use manual observation
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

