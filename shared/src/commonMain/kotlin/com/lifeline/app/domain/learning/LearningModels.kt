package com.lifeline.app.domain.learning

import kotlinx.datetime.Instant

data class LearningGoal(
    val id: String,
    val title: String,
    val description: String,
    val targetDate: Instant?,
    val progress: Float, // 0.0 to 1.0
    val status: GoalStatus
)

enum class GoalStatus {
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED,
    PAUSED
}

data class LearningModule(
    val id: String,
    val title: String,
    val description: String,
    val content: String,
    val estimatedDuration: Int, // minutes
    val completed: Boolean = false,
    val completedAt: Instant? = null
)
