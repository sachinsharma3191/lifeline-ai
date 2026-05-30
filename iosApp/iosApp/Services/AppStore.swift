import Foundation
import Observation

@Observable
final class AppStore {
    var transactions: [TransactionRecord] = []
    var financialGoals: [FinancialGoalRecord] = []
    var symptoms: [SymptomRecord] = []
    var learningGoals: [LearningGoalRecord] = []
    var learningModules: [LearningModuleRecord] = []
    var serviceSearchQuery: String = ""

    var financeAiResponse: String?
    var healthAiResponse: String?
    var learningAiResponse: String?
    var servicesAiResponse: String?

    private let fileURL: URL

    init() {
        let documents = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first!
        fileURL = documents.appendingPathComponent("lifeline_ai_data.json")
        load()
        if transactions.isEmpty && learningModules.isEmpty {
            seedDemoData()
            save()
        }
    }

    var incomeTotal: Double {
        transactions.filter { $0.type == .income }.reduce(0) { $0 + $1.amount }
    }

    var expenseTotal: Double {
        transactions.filter { $0.type == .expense }.reduce(0) { $0 + $1.amount }
    }

    var netTotal: Double { incomeTotal - expenseTotal }

    func addTransaction(_ transaction: TransactionRecord) {
        transactions.insert(transaction, at: 0)
        save()
    }

    func updateTransaction(_ transaction: TransactionRecord) {
        guard let index = transactions.firstIndex(where: { $0.id == transaction.id }) else { return }
        transactions[index] = transaction
        save()
    }

    func addFinancialGoal(_ goal: FinancialGoalRecord) {
        financialGoals.insert(goal, at: 0)
        save()
    }

    func updateFinancialGoal(_ goal: FinancialGoalRecord) {
        guard let index = financialGoals.firstIndex(where: { $0.id == goal.id }) else { return }
        financialGoals[index] = goal
        save()
    }

    func addSymptom(_ symptom: SymptomRecord) {
        symptoms.insert(symptom, at: 0)
        save()
    }

    func updateSymptom(_ symptom: SymptomRecord) {
        guard let index = symptoms.firstIndex(where: { $0.id == symptom.id }) else { return }
        symptoms[index] = symptom
        save()
    }

    func addLearningGoal(_ goal: LearningGoalRecord) {
        learningGoals.insert(goal, at: 0)
        save()
    }

    func updateLearningGoal(_ goal: LearningGoalRecord) {
        guard let index = learningGoals.firstIndex(where: { $0.id == goal.id }) else { return }
        learningGoals[index] = goal
        save()
    }

    func completeModule(id: String) {
        guard let index = learningModules.firstIndex(where: { $0.id == id }) else { return }
        learningModules[index].completed = true
        save()
    }

    func askFinanceAi(_ prompt: String) {
        financeAiResponse = OfflineAiCoach.respond(prompt: prompt, store: self)
    }

    func askHealthAi(_ prompt: String) {
        healthAiResponse = OfflineAiCoach.respond(prompt: prompt, store: self)
    }

    func askLearningAi(_ prompt: String) {
        learningAiResponse = OfflineAiCoach.respond(prompt: prompt, store: self)
    }

    func askServicesAi(_ prompt: String) {
        servicesAiResponse = OfflineAiCoach.respond(prompt: prompt, store: self)
    }

    private func load() {
        guard let data = try? Data(contentsOf: fileURL),
              let snapshot = try? JSONDecoder().decode(AppSnapshot.self, from: data) else { return }
        transactions = snapshot.transactions
        financialGoals = snapshot.financialGoals
        symptoms = snapshot.symptoms
        learningGoals = snapshot.learningGoals
        learningModules = snapshot.learningModules
    }

    private func save() {
        let snapshot = AppSnapshot(
            transactions: transactions,
            financialGoals: financialGoals,
            symptoms: symptoms,
            learningGoals: learningGoals,
            learningModules: learningModules
        )
        guard let data = try? JSONEncoder().encode(snapshot) else { return }
        try? data.write(to: fileURL, options: .atomic)
    }

    private func seedDemoData() {
        transactions = [
            TransactionRecord(id: UUID().uuidString, amount: 1200, type: .income, category: "Part-time Job", timestamp: Date(), description: "Campus work study"),
            TransactionRecord(id: UUID().uuidString, amount: 850, type: .expense, category: "Rent", timestamp: Date(), description: "Shared apartment near Westcliff"),
            TransactionRecord(id: UUID().uuidString, amount: 120, type: .expense, category: "Groceries", timestamp: Date(), description: "Weekly shop"),
            TransactionRecord(id: UUID().uuidString, amount: 45, type: .expense, category: "Transit", timestamp: Date(), description: "Monthly pass top-up")
        ]

        financialGoals = [
            FinancialGoalRecord(id: UUID().uuidString, name: "Emergency fund", targetAmount: 1000, currentAmount: 250, category: "Savings"),
            FinancialGoalRecord(id: UUID().uuidString, name: "Relocation deposit", targetAmount: 2000, currentAmount: 600, category: "Housing")
        ]

        learningModules = [
            LearningModuleRecord(id: "mod_1", title: "Budgeting 101", description: "Basics for student finances", estimatedDuration: 20, completed: false),
            LearningModuleRecord(id: "mod_2", title: "Campus Resources", description: "Find aid and services quickly", estimatedDuration: 15, completed: false),
            LearningModuleRecord(id: "mod_3", title: "Relocation Checklist", description: "Settling into a new city", estimatedDuration: 25, completed: true)
        ]

        learningGoals = [
            LearningGoalRecord(id: UUID().uuidString, title: "Finish midterm prep", description: "Complete two practice sets", progress: 0.4, status: .inProgress)
        ]
    }
}
