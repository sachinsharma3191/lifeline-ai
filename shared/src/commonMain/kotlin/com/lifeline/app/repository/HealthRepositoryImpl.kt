package com.lifeline.app.repository

import com.lifeline.app.database.LifelineDatabase
import com.lifeline.app.domain.health.HealthTimelineEntry
import com.lifeline.app.domain.health.Symptom
import com.lifeline.app.domain.health.SymptomCategory
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant

class HealthRepositoryImpl(
    private val database: LifelineDatabase
) : HealthRepository {
    
    override suspend fun addSymptom(symptom: Symptom) {
        database.healthQueries.insertSymptom(
            id = symptom.id,
            name = symptom.name,
            severity = symptom.severity.toLong(),
            timestamp = symptom.timestamp.toEpochMilliseconds(),
            notes = symptom.notes,
            category = symptom.category.name
        )
    }

    override suspend fun updateSymptom(symptom: Symptom) {
        database.healthQueries.updateSymptom(
            name = symptom.name,
            severity = symptom.severity.toLong(),
            timestamp = symptom.timestamp.toEpochMilliseconds(),
            notes = symptom.notes,
            category = symptom.category.name,
            id = symptom.id
        )
    }
    
    override suspend fun getSymptoms(startDate: Instant?, endDate: Instant?): Flow<List<Symptom>> {
        val startTimestamp = startDate?.toEpochMilliseconds()
        val endTimestamp = endDate?.toEpochMilliseconds()

        return database.healthQueries
            .getAllSymptoms(startTimestamp, endTimestamp)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { rows ->
                rows.map { row ->
                    Symptom(
                        id = row.id,
                        name = row.name,
                        severity = row.severity.toInt(),
                        timestamp = Instant.fromEpochMilliseconds(row.timestamp),
                        notes = row.notes,
                        category = SymptomCategory.valueOf(row.category)
                    )
                }
            }
    }
    
    override suspend fun addTimelineEntry(entry: HealthTimelineEntry) {
        database.healthQueries.insertTimelineEntry(
            id = entry.id,
            timestamp = entry.timestamp.toEpochMilliseconds(),
            notes = entry.notes
        )
        
        // Link symptoms to timeline entry
        entry.symptoms.forEach { symptom ->
            database.healthQueries.insertSymptomTimelineEntry(
                symptom_id = symptom.id,
                timeline_entry_id = entry.id
            )
        }
    }
    
    override suspend fun getTimelineEntries(startDate: Instant?, endDate: Instant?): Flow<List<HealthTimelineEntry>> {
        val startTimestamp = startDate?.toEpochMilliseconds()
        val endTimestamp = endDate?.toEpochMilliseconds()

        return database.healthQueries
            .getAllTimelineEntries(startTimestamp, endTimestamp)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { rows ->
                rows.map { row ->
                    val symptomRows = database.healthQueries
                        .getSymptomsForTimelineEntry(row.id)
                        .executeAsList()
                    val symptoms = symptomRows.map { symptomRow ->
                        Symptom(
                            id = symptomRow.id,
                            name = symptomRow.name,
                            severity = symptomRow.severity.toInt(),
                            timestamp = Instant.fromEpochMilliseconds(symptomRow.timestamp),
                            notes = symptomRow.notes,
                            category = SymptomCategory.valueOf(symptomRow.category)
                        )
                    }

                    HealthTimelineEntry(
                        id = row.id,
                        timestamp = Instant.fromEpochMilliseconds(row.timestamp),
                        symptoms = symptoms,
                        notes = row.notes
                    )
                }
            }
    }
    
    override suspend fun deleteSymptom(id: String) {
        database.healthQueries.deleteSymptom(id)
    }
    
    override suspend fun deleteTimelineEntry(id: String) {
        database.healthQueries.deleteTimelineEntry(id)
    }
}
