package com.lifeline.app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform