package com.lifeline.app

import com.lifeline.app.database.DatabaseDriverFactory

class AppContainerJvmTest : AppContainerTest() {
    override fun createDatabaseDriverFactory(): DatabaseDriverFactory {
        // Use the actual JVM DatabaseDriverFactory which already creates an in-memory database
        return DatabaseDriverFactory()
    }
}

