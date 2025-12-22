package com.lifeline.app.repository

import com.lifeline.app.database.LifelineDatabase
import com.lifeline.app.domain.learning.GoalStatus
import com.lifeline.app.domain.learning.LearningGoal
import com.lifeline.app.domain.learning.LearningModule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class LearningRepositoryImpl(
    private val database: LifelineDatabase
) : LearningRepository {
    
    override suspend fun addGoal(goal: LearningGoal) {
        database.learningQueries.insertLearningGoal(
            id = goal.id,
            title = goal.title,
            description = goal.description,
            target_date = goal.targetDate?.toEpochMilliseconds(),
            progress = goal.progress.toDouble(),
            status = goal.status.name
        )
    }
    
    override suspend fun getGoals(): Flow<List<LearningGoal>> = flow {
        val rows = database.learningQueries.getAllLearningGoals().executeAsList()
        val goals = rows.map { row ->
            LearningGoal(
                id = row.id,
                title = row.title,
                description = row.description,
                targetDate = row.target_date?.let { Instant.fromEpochMilliseconds(it) },
                progress = row.progress.toFloat(),
                status = GoalStatus.valueOf(row.status)
            )
        }
        emit(goals)
    }
    
    override suspend fun updateGoal(goal: LearningGoal) {
        database.learningQueries.updateLearningGoal(
            id = goal.id,
            title = goal.title,
            description = goal.description,
            target_date = goal.targetDate?.toEpochMilliseconds(),
            progress = goal.progress.toDouble(),
            status = goal.status.name
        )
    }
    
    override suspend fun deleteGoal(id: String) {
        database.learningQueries.deleteLearningGoal(id)
    }
    
    override suspend fun addModule(module: LearningModule) {
        database.learningQueries.insertLearningModule(
            id = module.id,
            title = module.title,
            description = module.description,
            content = module.content,
            estimated_duration = module.estimatedDuration.toLong(),
            completed = if (module.completed) 1L else 0L,
            completed_at = module.completedAt?.toEpochMilliseconds()
        )
    }
    
    override suspend fun getModules(): Flow<List<LearningModule>> = flow {
        val rows = database.learningQueries.getAllLearningModules().executeAsList()
        val modules = rows.map { row ->
            LearningModule(
                id = row.id,
                title = row.title,
                description = row.description,
                content = row.content,
                estimatedDuration = row.estimated_duration.toInt(),
                completed = row.completed == 1L,
                completedAt = row.completed_at?.let { Instant.fromEpochMilliseconds(it) }
            )
        }
        emit(modules)
    }
    
    override suspend fun completeModule(id: String) {
        database.learningQueries.completeLearningModule(
            completed_at = Clock.System.now().toEpochMilliseconds(),
            id = id
        )
    }
}
