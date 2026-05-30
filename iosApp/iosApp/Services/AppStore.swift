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
}
