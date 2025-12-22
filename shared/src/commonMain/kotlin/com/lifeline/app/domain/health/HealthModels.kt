package com.lifeline.app.domain.health

import kotlinx.datetime.Instant

data class Symptom(
    val id: String,
    val name: String,
    val severity: Int, // 1-10
    val timestamp: Instant,
    val notes: String? = null,
    val category: SymptomCategory
)

enum class SymptomCategory {
    PAIN,
    FATIGUE,
    MOOD,
    SLEEP,
    DIGESTIVE,
    RESPIRATORY,
    OTHER
}

data class HealthTimelineEntry(
    val id: String,
    val timestamp: Instant,
    val symptoms: List<Symptom>,
    val notes: String? = null
)
