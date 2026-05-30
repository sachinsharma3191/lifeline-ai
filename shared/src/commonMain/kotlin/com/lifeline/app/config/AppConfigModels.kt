package com.lifeline.app.config

import kotlinx.serialization.Serializable

@Serializable
data class AppConfig(
    val version: Int,
    val app: AppInfo,
    val theme: ThemeConfig,
    val tabs: List<TabConfig>,
    val screens: ScreensConfig,
    val servicesCatalog: ServicesCatalogConfig
)

@Serializable
data class AppInfo(
    val name: String,
    val tagline: String
)

@Serializable
data class ThemeConfig(
    val colors: ThemeColors,
    val spacing: ThemeSpacing
)

@Serializable
data class ThemeColors(
    val primary: String,
    val primaryContainer: String,
    val secondaryContainer: String,
    val error: String,
    val onSurfaceVariant: String
)

@Serializable
data class ThemeSpacing(
    val screenPadding: Int,
    val cardPadding: Int,
    val sectionGap: Int,
    val chipGap: Int
)

@Serializable
data class TabConfig(
    val id: String,
    val label: String,
    val icon: String
)

@Serializable
data class ScreensConfig(
    val home: HomeScreenConfig,
    val finance: FeatureScreenConfig,
    val health: FeatureScreenConfig,
    val learning: LearningScreenConfig,
    val services: ServicesScreenConfig
)

@Serializable
data class HomeScreenConfig(
    val pilotAreasTitle: String,
    val pilotAreas: List<String>,
    val financialSnapshotTitle: String,
    val metrics: List<MetricConfig>,
    val northStarText: String,
    val dashboardTitle: String,
    val dashboardCards: List<DashboardCardConfig>,
    val valuePropositionTitle: String,
    val valuePropositionItems: List<String>
)

@Serializable
data class MetricConfig(
    val id: String,
    val label: String
)

@Serializable
data class DashboardCardConfig(
    val id: String,
    val title: String,
    val subtitleTemplate: String,
    val icon: String
)

@Serializable
data class FeatureScreenConfig(
    val title: String,
    val aiCoachLabel: String,
    val aiSuggestions: List<AiSuggestionConfig>,
    val sections: Map<String, SectionConfig>,
    val forms: Map<String, String>
)

@Serializable
data class LearningScreenConfig(
    val title: String,
    val aiCoachLabel: String,
    val aiSuggestions: List<AiSuggestionConfig>,
    val sections: Map<String, LearningSectionConfig>,
    val forms: Map<String, String>
)

@Serializable
data class LearningSectionConfig(
    val title: String,
    val emptyText: String,
    val completedLabel: String? = null,
    val completeButtonLabel: String? = null,
    val listLimit: Int? = null
)

@Serializable
data class ServicesScreenConfig(
    val title: String,
    val searchPlaceholder: String,
    val searchEmptyText: String,
    val aiCoachLabel: String,
    val aiSuggestions: List<AiSuggestionConfig>,
    val detail: ServicesDetailConfig
)

@Serializable
data class ServicesDetailConfig(
    val backLabel: String,
    val mapDemoTitle: String,
    val mapPlaceholder: String,
    val categoryPrefix: String,
    val contactPrefix: String,
    val websitePrefix: String
)

@Serializable
data class AiSuggestionConfig(
    val label: String,
    val prompt: String
)

@Serializable
data class SectionConfig(
    val title: String,
    val emptyText: String,
    val listLimit: Int? = null
)

@Serializable
data class ServicesCatalogConfig(
    val seedCount: Int,
    val mapsUrlTemplate: String
)
