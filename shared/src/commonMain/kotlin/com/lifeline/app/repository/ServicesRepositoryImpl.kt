package com.lifeline.app.repository

import com.lifeline.app.database.LifelineDatabase
import com.lifeline.app.domain.services.CommunityService
import com.lifeline.app.domain.services.ServiceCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ServicesRepositoryImpl(
    private val database: LifelineDatabase
) : ServicesRepository {
    
    override suspend fun getServices(category: String?): Flow<List<CommunityService>> = flow {
        val rows = database.servicesQueries.getAllCommunityServices(category).executeAsList()
        val services = rows.map { row ->
            CommunityService(
                id = row.id,
                name = row.name,
                description = row.description,
                category = ServiceCategory.valueOf(row.category),
                location = row.location,
                contactInfo = row.contact_info,
                website = row.website,
                available = row.available == 1L
            )
        }
        emit(services)
    }
    
    override suspend fun searchServices(query: String): Flow<List<CommunityService>> = flow {
        val rows = database.servicesQueries.searchCommunityServices(query).executeAsList()
        val services = rows.map { row ->
            CommunityService(
                id = row.id,
                name = row.name,
                description = row.description,
                category = ServiceCategory.valueOf(row.category),
                location = row.location,
                contactInfo = row.contact_info,
                website = row.website,
                available = row.available == 1L
            )
        }
        emit(services)
    }
    
    override suspend fun addService(service: CommunityService) {
        database.servicesQueries.insertCommunityService(
            id = service.id,
            name = service.name,
            description = service.description,
            category = service.category.name,
            location = service.location,
            contact_info = service.contactInfo,
            website = service.website,
            available = if (service.available) 1L else 0L
        )
    }
    
    override suspend fun updateService(service: CommunityService) {
        database.servicesQueries.updateCommunityService(
            id = service.id,
            name = service.name,
            description = service.description,
            category = service.category.name,
            location = service.location,
            contact_info = service.contactInfo,
            website = service.website,
            available = if (service.available) 1L else 0L
        )
    }
    
    override suspend fun deleteService(id: String) {
        database.servicesQueries.deleteCommunityService(id)
    }
}
