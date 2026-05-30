import Foundation

enum OfflineAiCoach {
    static func respond(prompt: String, store: AppStore) -> String {
        let normalized = prompt.trimmingCharacters(in: .whitespacesAndNewlines).lowercased()

        if normalized.contains("finance") || normalized.contains("money") ||
            normalized.contains("budget") || normalized.contains("expense") ||
            (normalized.contains("summary") && !store.transactions.isEmpty) {
            return financeInsight(prompt: normalized, store: store)
        }

        if normalized.contains("health") || normalized.contains("symptom") {
            return healthInsight(store: store)
        }

        if normalized.contains("learn") || normalized.contains("study") || normalized.contains("progress") {
            return learningInsight(store: store)
        }

        if normalized.contains("service") || normalized.contains("community") || normalized.contains("help") {
            return servicesInsight(prompt: normalized, store: store)
        }

        return "Lifeline AI coach (offline). Built for students & relocators. Try: Finance summary, Health trends, Learning progress, or Services help."
    }

    private static func financeInsight(prompt: String, store: AppStore) -> String {
        guard !store.transactions.isEmpty else {
            return "No transactions yet. Track rent, groceries, and transit to unlock savings tips for students and relocators."
        }

        let income = store.transactions.filter { $0.type == .income }.reduce(0) { $0 + $1.amount }
        let expenses = store.transactions.filter { $0.type == .expense }.reduce(0) { $0 + $1.amount }
        let net = income - expenses

        let topCategory = Dictionary(grouping: store.transactions.filter { $0.type == .expense }, by: \.category)
            .max(by: { $0.value.reduce(0) { $0 + $1.amount } < $1.value.reduce(0) { $0 + $1.amount } })?
            .key

        var lines = [
            "Finance snapshot (offline):",
            "• Income: \(currency(income)) | Expenses: \(currency(expenses)) | Net: \(currency(net))"
        ]

        if let topCategory {
            lines.append("• Top expense category: \(topCategory)")
            if prompt.contains("top") || prompt.contains("category") {
                lines.append("• Focus on \(topCategory) to improve monthly savings.")
            }
        }

        if !store.financialGoals.isEmpty {
            let avg = store.financialGoals.map { $0.currentAmount / max($0.targetAmount, 1) }.reduce(0, +) / Double(store.financialGoals.count)
            lines.append("• Goal progress: \(Int(avg * 100))% average across \(store.financialGoals.count) goal(s).")
        }

        lines.append("Tip: compare campus meal plans and transit passes in your pilot area.")
        return lines.joined(separator: "\n")
    }

    private static func healthInsight(store: AppStore) -> String {
        guard !store.symptoms.isEmpty else {
            return "No symptoms logged. Track sleep, stress, and fatigue during relocation or exam weeks."
        }

        let avg = Double(store.symptoms.map(\.severity).reduce(0, +)) / Double(store.symptoms.count)
        let frequent = Dictionary(grouping: store.symptoms, by: \.name).max(by: { $0.value.count < $1.value.count })?.key
        let highSeverity = store.symptoms.filter { $0.severity >= 7 }.prefix(3).map(\.name)

        var lines = [
            "Health trends (offline):",
            "• \(store.symptoms.count) entries | Average severity: \(String(format: "%.1f", avg))/10"
        ]
        if let frequent { lines.append("• Most frequent: \(frequent)") }
        if !highSeverity.isEmpty { lines.append("• Recent high-severity: \(highSeverity.joined(separator: ", "))") }
        lines.append("Log daily notes to spot patterns before visiting campus health resources.")
        return lines.joined(separator: "\n")
    }

    private static func learningInsight(store: AppStore) -> String {
        let completed = store.learningModules.filter(\.completed).count
        let inProgress = store.learningGoals.filter { $0.status == .inProgress }.count
        let avg = store.learningGoals.isEmpty ? 0 :
            store.learningGoals.map(\.progress).reduce(0, +) / Double(store.learningGoals.count)

        var lines = [
            "Learning progress (offline):",
            "• \(store.learningGoals.count) goal(s) | \(completed)/\(store.learningModules.count) modules completed"
        ]
        if !store.learningGoals.isEmpty {
            lines.append("• Average goal progress: \(Int(avg * 100))%")
        }
        if inProgress > 0 {
            lines.append("• \(inProgress) goal(s) in progress — try 25-minute focus blocks.")
        }
        lines.append("Complete one module this week to build momentum.")
        return lines.joined(separator: "\n")
    }

    private static func servicesInsight(prompt: String, store: AppStore) -> String {
        let query = store.serviceSearchQuery.trimmingCharacters(in: .whitespacesAndNewlines)
        let results = CommunityServicesCatalog.search(query: query)

        guard !results.isEmpty else {
            return "Search for Clinic, Food, Legal, or Housing near Westcliff, UCI, or the Bay Area."
        }

        let categories = Array(Set(results.map(\.category.displayName))).prefix(4)
        var lines = [
            "Services help (offline):",
            "• Found \(results.count) result(s)\(query.isEmpty ? "" : " for \"\(query)\"")",
            "• Categories: \(categories.joined(separator: ", "))"
        ]
        if let location = results.first?.location {
            lines.append("• Nearest match: \(location)")
        }
        lines.append("Tap an address to open Apple Maps and verify hours.")
        return lines.joined(separator: "\n")
    }

    private static func currency(_ value: Double) -> String {
        let formatter = NumberFormatter()
        formatter.numberStyle = .currency
        formatter.currencyCode = "USD"
        return formatter.string(from: NSNumber(value: value)) ?? "$\(value)"
    }
}
