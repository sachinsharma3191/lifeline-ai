package com.lifeline.app.integration

import com.lifeline.app.database.DatabaseDriverFactory

class EndToEndJvmTest : EndToEndTest() {
    override fun createDatabaseDriverFactory(): DatabaseDriverFactory {
        // Use the actual JVM DatabaseDriverFactory which already creates an in-memory database
        return DatabaseDriverFactory()
    }
}

