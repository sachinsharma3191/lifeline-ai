package com.lifeline.app

import com.lifeline.app.database.DatabaseDriverFactory

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
expect fun createDatabaseDriverFactory(): DatabaseDriverFactory