package com.lifeline.app.repository

import com.lifeline.app.database.LifelineDatabase
import com.lifeline.app.domain.services.CommunityService
import com.lifeline.app.domain.services.ServiceCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ServicesRepositoryImpl(
    private val database: LifelineDatabase
) : ServicesRepository {

    private fun seedIfEmpty() {
        val count = database.servicesQueries.countServices().executeAsOne()
        if (count > 0L) return

        val categories = ServiceCategory.values()
        for (i in 1..100) {
            val category = categories[(i - 1) % categories.size]
            val id = "seed_service_$i"

            val streetNumber = 100 + i
            val streetName = when (i % 6) {
                0 -> "Main St"
                1 -> "Market St"
                2 -> "Broadway"
                3 -> "Pine St"
                4 -> "Oak Ave"
                else -> "Sunset Blvd"
            }
            val city = when (i % 4) {
                0 -> "San Francisco"
                1 -> "San Jose"
                2 -> "Oakland"
                else -> "Berkeley"
            }
            val state = "CA"
            val zip = (94100 + (i % 80)).toString()
            val address = "$streetNumber $streetName, $city, $state $zip"

            database.servicesQueries.insertCommunityService(
                id = id,
                name = when (category) {
                    ServiceCategory.HEALTHCARE -> "Health Clinic $i"
                    ServiceCategory.MENTAL_HEALTH -> "Counseling Center $i"
                    ServiceCategory.FINANCIAL_ASSISTANCE -> "Financial Help Desk $i"
                    ServiceCategory.EDUCATION -> "Education Program $i"
                    ServiceCategory.HOUSING -> "Housing Support $i"
                    ServiceCategory.FOOD_ASSISTANCE -> "Food Pantry $i"
                    ServiceCategory.LEGAL -> "Legal Aid $i"
                    ServiceCategory.OTHER -> "Community Resource $i"
                },
                description = "Demo service entry #$i for ${category.name.replace('_', ' ').lowercase()}.",
                category = category.name,
                location = address,
                contact_info = "(555) 010-${(1000 + i).toString().takeLast(4)}",
                website = "https://example.org/service/$i",
                available = 1L
            )
        }
    }
    
    override suspend fun getServices(category: String?): Flow<List<CommunityService>> = flow {
        seedIfEmpty()
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
        seedIfEmpty()
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
