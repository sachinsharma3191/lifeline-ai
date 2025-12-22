package com.lifeline.app.repository

import com.lifeline.app.domain.health.HealthTimelineEntry
import com.lifeline.app.domain.health.Symptom
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

interface HealthRepository {
    suspend fun addSymptom(symptom: Symptom)
    suspend fun getSymptoms(startDate: Instant?, endDate: Instant?): Flow<List<Symptom>>
    suspend fun addTimelineEntry(entry: HealthTimelineEntry)
    suspend fun getTimelineEntries(startDate: Instant?, endDate: Instant?): Flow<List<HealthTimelineEntry>>
    suspend fun deleteSymptom(id: String)
    suspend fun deleteTimelineEntry(id: String)
}
