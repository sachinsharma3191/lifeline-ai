package com.lifeline.app.repository

import com.lifeline.app.domain.learning.LearningGoal
import com.lifeline.app.domain.learning.LearningModule
import kotlinx.coroutines.flow.Flow

interface LearningRepository {
    suspend fun addGoal(goal: LearningGoal)
    suspend fun getGoals(): Flow<List<LearningGoal>>
    suspend fun updateGoal(goal: LearningGoal)
    suspend fun deleteGoal(id: String)
    suspend fun addModule(module: LearningModule)
    suspend fun getModules(): Flow<List<LearningModule>>
    suspend fun completeModule(id: String)
}

