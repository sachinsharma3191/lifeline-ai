package com.lifeline.app.domain.services

data class CommunityService(
    val id: String,
    val name: String,
    val description: String,
    val category: ServiceCategory,
    val location: String?,
    val contactInfo: String?,
    val website: String?,
    val available: Boolean = true
)

enum class ServiceCategory {
    HEALTHCARE,
    MENTAL_HEALTH,
    FINANCIAL_ASSISTANCE,
    EDUCATION,
    HOUSING,
    FOOD_ASSISTANCE,
    LEGAL,
    OTHER
}

