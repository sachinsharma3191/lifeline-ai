import Foundation

enum TransactionType: String, Codable, CaseIterable {
    case income = "INCOME"
    case expense = "EXPENSE"
    case transfer = "TRANSFER"
}

struct TransactionRecord: Identifiable, Codable, Equatable {
    var id: String
    var amount: Double
    var type: TransactionType
    var category: String
    var timestamp: Date
    var description: String?
}

struct FinancialGoalRecord: Identifiable, Codable, Equatable {
    var id: String
    var name: String
    var targetAmount: Double
    var currentAmount: Double
    var category: String
}

enum SymptomCategory: String, Codable, CaseIterable {
    case pain = "PAIN"
    case fatigue = "FATIGUE"
    case mood = "MOOD"
    case sleep = "SLEEP"
    case digestive = "DIGESTIVE"
    case respiratory = "RESPIRATORY"
    case other = "OTHER"
}

struct SymptomRecord: Identifiable, Codable, Equatable {
    var id: String
    var name: String
    var severity: Int
    var timestamp: Date
    var notes: String?
    var category: SymptomCategory
}

enum GoalStatus: String, Codable, CaseIterable {
    case notStarted = "NOT_STARTED"
    case inProgress = "IN_PROGRESS"
    case completed = "COMPLETED"
    case paused = "PAUSED"
}

struct LearningGoalRecord: Identifiable, Codable, Equatable {
    var id: String
    var title: String
    var description: String
    var progress: Double
    var status: GoalStatus
}

struct LearningModuleRecord: Identifiable, Codable, Equatable {
    var id: String
    var title: String
    var description: String
    var estimatedDuration: Int
    var completed: Bool
}

enum ServiceCategory: String, Codable, CaseIterable {
    case healthcare = "HEALTHCARE"
    case mentalHealth = "MENTAL_HEALTH"
    case financialAssistance = "FINANCIAL_ASSISTANCE"
    case education = "EDUCATION"
    case housing = "HOUSING"
    case foodAssistance = "FOOD_ASSISTANCE"
    case legal = "LEGAL"
    case other = "OTHER"

    var displayName: String {
        rawValue.replacingOccurrences(of: "_", with: " ").capitalized
    }
}

struct CommunityServiceRecord: Identifiable, Codable, Equatable {
    var id: String
    var name: String
    var description: String
    var category: ServiceCategory
    var location: String?
    var contactInfo: String?
    var website: String?
}

struct AppSnapshot: Codable {
    var transactions: [TransactionRecord]
    var financialGoals: [FinancialGoalRecord]
    var symptoms: [SymptomRecord]
    var learningGoals: [LearningGoalRecord]
    var learningModules: [LearningModuleRecord]
}
