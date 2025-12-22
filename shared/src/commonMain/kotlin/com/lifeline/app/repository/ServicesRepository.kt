package com.lifeline.app.repository

import com.lifeline.app.domain.services.CommunityService
import kotlinx.coroutines.flow.Flow

interface ServicesRepository {
    suspend fun getServices(category: String? = null): Flow<List<CommunityService>>
    suspend fun searchServices(query: String): Flow<List<CommunityService>>
    suspend fun addService(service: CommunityService)
    suspend fun updateService(service: CommunityService)
    suspend fun deleteService(id: String)
}

