import Foundation

enum CommunityServicesCatalog {
    static let all: [CommunityServiceRecord] = {
        let pilotCities = [
            ("Irvine", "CA", "92612"),
            ("Fremont", "CA", "94536"),
            ("Berkeley", "CA", "94704"),
            ("San Francisco", "CA", "94102")
        ]

        let categories = ServiceCategory.allCases
        return (1...40).map { index in
            let category = categories[(index - 1) % categories.count]
            let city = pilotCities[(index - 1) % pilotCities.count]
            let street = 100 + index

            let name: String
            switch category {
            case .healthcare: name = "Campus Health Clinic \(index)"
            case .mentalHealth: name = "Student Counseling Center \(index)"
            case .financialAssistance: name = "Financial Aid Desk \(index)"
            case .education: name = "Academic Success Program \(index)"
            case .housing: name = "Housing Support Office \(index)"
            case .foodAssistance: name = "Community Food Pantry \(index)"
            case .legal: name = "Legal Aid Clinic \(index)"
            case .other: name = "Community Resource \(index)"
            }

            return CommunityServiceRecord(
                id: "service_\(index)",
                name: name,
                description: "Localized \(category.displayName.lowercased()) resource for students and relocators (#\(index)).",
                category: category,
                location: "\(street) University Ave, \(city.0), \(city.1) \(city.2)",
                contactInfo: "(555) 010-\(String(format: "%04d", 1000 + index))",
                website: "https://example.org/lifeline/service/\(index)"
            )
        }
    }()

    static func search(query: String) -> [CommunityServiceRecord] {
        let trimmed = query.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !trimmed.isEmpty else { return [] }

        let tokens = trimmed.lowercased().split(separator: " ").map(String.init)
        return all.filter { service in
            let haystack = [
                service.name,
                service.description,
                service.category.displayName,
                service.location ?? ""
            ].joined(separator: " ").lowercased()

            return tokens.allSatisfy { haystack.contains($0) }
        }
    }
}
