import Foundation

enum CommunityServicesCatalog {
    static var all: [CommunityServiceRecord] {
        let seedCount = AppConfigRoot.shared.servicesCatalog.seedCount
        let streetNames = ["Main St", "Market St", "Broadway", "Pine St", "Oak Ave", "Sunset Blvd"]
        let cities = ["San Francisco", "San Jose", "Oakland", "Berkeley"]
        let categories = ServiceCategory.allCases

        return (1...seedCount).map { index in
            let category = categories[(index - 1) % categories.count]
            let streetNumber = 100 + index
            let streetName = streetNames[(index - 1) % streetNames.count]
            let city = cities[(index - 1) % cities.count]
            let zip = String(94100 + (index % 80))
            let address = "\(streetNumber) \(streetName), \(city), CA \(zip)"

            let name: String
            switch category {
            case .healthcare: name = "Health Clinic \(index)"
            case .mentalHealth: name = "Counseling Center \(index)"
            case .financialAssistance: name = "Financial Help Desk \(index)"
            case .education: name = "Education Program \(index)"
            case .housing: name = "Housing Support \(index)"
            case .foodAssistance: name = "Food Pantry \(index)"
            case .legal: name = "Legal Aid \(index)"
            case .other: name = "Community Resource \(index)"
            }

            let categoryLabel = category.rawValue.replacingOccurrences(of: "_", with: " ").lowercased()
            return CommunityServiceRecord(
                id: "seed_service_\(index)",
                name: name,
                description: "Demo service entry #\(index) for \(categoryLabel).",
                category: category,
                location: address,
                contactInfo: "(555) 010-\(String(format: "%04d", 1000 + index))",
                website: "https://example.org/service/\(index)"
            )
        }
    }

    static func search(query: String) -> [CommunityServiceRecord] {
        let trimmed = query.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !trimmed.isEmpty else { return [] }

        return all.filter { service in
            service.name.localizedCaseInsensitiveContains(trimmed) ||
                service.description.localizedCaseInsensitiveContains(trimmed)
        }.sorted { $0.name < $1.name }
    }
}
