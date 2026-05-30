import SwiftUI

struct AppConfigRoot: Codable {
    let version: Int
    let app: AppConfigAppInfo
    let theme: AppConfigTheme
    let tabs: [AppConfigTab]
    let screens: AppConfigScreens
    let servicesCatalog: AppConfigServicesCatalog
}

struct AppConfigAppInfo: Codable {
    let name: String
    let tagline: String
}

struct AppConfigTheme: Codable {
    let colors: AppConfigColors
    let spacing: AppConfigSpacing
}

struct AppConfigColors: Codable {
    let primary: String
    let primaryContainer: String
    let secondaryContainer: String
    let error: String
    let onSurfaceVariant: String
}

struct AppConfigSpacing: Codable {
    let screenPadding: Int
    let cardPadding: Int
    let sectionGap: Int
    let chipGap: Int
}

struct AppConfigTab: Codable, Identifiable {
    let id: String
    let label: String
    let icon: String
}

struct AppConfigScreens: Codable {
    let home: AppConfigHomeScreen
    let finance: AppConfigFeatureScreen
    let health: AppConfigFeatureScreen
    let learning: AppConfigLearningScreen
    let services: AppConfigServicesScreen
}

struct AppConfigHomeScreen: Codable {
    let pilotAreasTitle: String
    let pilotAreas: [String]
    let financialSnapshotTitle: String
    let metrics: [AppConfigMetric]
    let northStarText: String
    let dashboardTitle: String
    let dashboardCards: [AppConfigDashboardCard]
    let valuePropositionTitle: String
    let valuePropositionItems: [String]
}

struct AppConfigMetric: Codable, Identifiable {
    let id: String
    let label: String
}

struct AppConfigDashboardCard: Codable, Identifiable {
    let id: String
    let title: String
    let subtitleTemplate: String
    let icon: String
}

struct AppConfigFeatureScreen: Codable {
    let title: String
    let aiCoachLabel: String
    let aiSuggestions: [AppConfigAiSuggestion]
    let sections: [String: AppConfigSection]
    let forms: [String: String]
}

struct AppConfigLearningScreen: Codable {
    let title: String
    let aiCoachLabel: String
    let aiSuggestions: [AppConfigAiSuggestion]
    let sections: [String: AppConfigLearningSection]
    let forms: [String: String]
}

struct AppConfigLearningSection: Codable {
    let title: String
    let emptyText: String
    let completedLabel: String?
    let completeButtonLabel: String?
    let listLimit: Int?
}

struct AppConfigServicesScreen: Codable {
    let title: String
    let searchPlaceholder: String
    let searchEmptyText: String
    let aiCoachLabel: String
    let aiSuggestions: [AppConfigAiSuggestion]
    let detail: AppConfigServicesDetail
}

struct AppConfigServicesDetail: Codable {
    let backLabel: String
    let mapDemoTitle: String
    let mapPlaceholder: String
    let categoryPrefix: String
    let contactPrefix: String
    let websitePrefix: String
}

struct AppConfigAiSuggestion: Codable, Identifiable {
    var id: String { label }
    let label: String
    let prompt: String
}

struct AppConfigSection: Codable {
    let title: String
    let emptyText: String
    let listLimit: Int?
}

struct AppConfigServicesCatalog: Codable {
    let seedCount: Int
    let mapsUrlTemplate: String
}

enum AppConfigLoader {
    private static var cached: AppConfigRoot?

    static var current: AppConfigRoot {
        if let cached { return cached }
        let loaded = load()
        cached = loaded
        return loaded
    }

    private static func load() -> AppConfigRoot {
        let url = Bundle.main.url(forResource: "app-config", withExtension: "json", subdirectory: "Config")
            ?? Bundle.main.url(forResource: "app-config", withExtension: "json")
        guard let url,
              let data = try? Data(contentsOf: url),
              let config = try? JSONDecoder().decode(AppConfigRoot.self, from: data) else {
            fatalError("Missing or invalid app-config.json in app bundle")
        }
        return config
    }
}

enum ConfigUiHelpers {
    static func iconSystemName(for key: String) -> String {
        switch key.lowercased() {
        case "home": return "house.fill"
        case "heart", "health": return "heart.fill"
        case "finance": return "building.columns.fill"
        case "learning": return "graduationcap.fill"
        case "services": return "mappin.and.ellipse"
        default: return "house.fill"
        }
    }

    static func templateSubtitle(
        _ template: String,
        transactionCount: Int = 0,
        goalCount: Int = 0,
        symptomCount: Int = 0,
        learningGoalCount: Int = 0
    ) -> String {
        template
            .replacingOccurrences(of: "{transactionCount}", with: String(transactionCount))
            .replacingOccurrences(of: "{goalCount}", with: String(goalCount))
            .replacingOccurrences(of: "{symptomCount}", with: String(symptomCount))
            .replacingOccurrences(of: "{learningGoalCount}", with: String(learningGoalCount))
    }

    static func color(from hex: String) -> Color {
        Color(hex: hex)
    }

    static func mapsUrl(template: String, address: String) -> URL? {
        let query = address.replacingOccurrences(of: " ", with: "+")
        let urlString = template.replacingOccurrences(of: "{query}", with: query)
        return URL(string: urlString)
    }
}

private extension Color {
    init(hex: String) {
        let cleaned = hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        var value: UInt64 = 0
        Scanner(string: cleaned).scanHexInt64(&value)
        let r = Double((value >> 16) & 0xFF) / 255
        let g = Double((value >> 8) & 0xFF) / 255
        let b = Double(value & 0xFF) / 255
        self.init(red: r, green: g, blue: b)
    }
}

extension AppConfigRoot {
    static var shared: AppConfigRoot { AppConfigLoader.current }
}
