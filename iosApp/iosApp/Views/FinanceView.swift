import SwiftUI

struct FinanceView: View {
    @Bindable var store: AppStore
    @State private var aiPrompt = ""
    @State private var showAddTransaction = false
    @State private var showAddGoal = false
    @State private var editingTransaction: TransactionRecord?
    @State private var editingGoal: FinancialGoalRecord?

    var body: some View {
        NavigationStack {
            List {
                Section("Offline AI Coach") {
                    AiCoachSection(
                        prompt: $aiPrompt,
                        response: store.financeAiResponse,
                        suggestions: ["Finance summary", "Top expense category", "Budget advice"]
                    ) { store.askFinanceAi($0) }
                }

                Section("Financial Goals") {
                    ForEach(store.financialGoals) { goal in
                        VStack(alignment: .leading, spacing: 6) {
                            HStack {
                                Text(goal.name).font(.headline)
                                Spacer()
                                Button("Edit") { editingGoal = goal }
                                    .font(.caption)
                            }
                            ProgressView(value: min(goal.currentAmount / max(goal.targetAmount, 1), 1))
                            Text("\(goal.currentAmount, format: .currency(code: "USD")) / \(goal.targetAmount, format: .currency(code: "USD"))")
                                .font(.caption)
                                .foregroundStyle(.secondary)
                        }
                    }
                }

                Section("Recent Transactions") {
                    ForEach(store.transactions.prefix(20)) { transaction in
                        HStack {
                            VStack(alignment: .leading) {
                                Text(transaction.category).font(.headline)
                                if let description = transaction.description {
                                    Text(description).font(.caption).foregroundStyle(.secondary)
                                }
                            }
                            Spacer()
                            Text(transaction.amount, format: .currency(code: "USD"))
                                .foregroundStyle(transaction.type == .expense ? .red : .green)
                            Button("Edit") { editingTransaction = transaction }
                                .font(.caption)
                        }
                    }
                }
            }
            .navigationTitle("Finance")
            .toolbar {
                ToolbarItemGroup(placement: .topBarTrailing) {
                    Button { showAddGoal = true } label: { Image(systemName: "target") }
                    Button { showAddTransaction = true } label: { Image(systemName: "plus") }
                }
            }
            .sheet(isPresented: $showAddTransaction) {
                TransactionForm(mode: .add) { store.addTransaction($0) }
            }
            .sheet(item: $editingTransaction) { transaction in
                TransactionForm(mode: .edit(transaction)) { store.updateTransaction($0) }
            }
            .sheet(isPresented: $showAddGoal) {
                GoalForm(mode: .add) { store.addFinancialGoal($0) }
            }
            .sheet(item: $editingGoal) { goal in
                GoalForm(mode: .edit(goal)) { store.updateFinancialGoal($0) }
            }
        }
    }
}

private struct TransactionForm: View {
    enum Mode { case add, edit(TransactionRecord) }

    let mode: Mode
    let onSave: (TransactionRecord) -> Void
    @Environment(\.dismiss) private var dismiss

    @State private var amount = ""
    @State private var category = ""
    @State private var description = ""
    @State private var type: TransactionType = .expense
    private var existingId: String?

    init(mode: Mode, onSave: @escaping (TransactionRecord) -> Void) {
        self.mode = mode
        self.onSave = onSave
        if case .edit(let record) = mode {
            _amount = State(initialValue: String(record.amount))
            _category = State(initialValue: record.category)
            _description = State(initialValue: record.description ?? "")
            _type = State(initialValue: record.type)
            existingId = record.id
        }
    }

    var body: some View {
        NavigationStack {
            Form {
                Picker("Type", selection: $type) {
                    ForEach(TransactionType.allCases, id: \.self) { Text($0.rawValue.capitalized).tag($0) }
                }
                TextField("Amount", text: $amount).keyboardType(.decimalPad)
                TextField("Category", text: $category)
                TextField("Description", text: $description)
            }
            .navigationTitle(modeTitle)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) { Button("Cancel") { dismiss() } }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Save") {
                        guard let value = Double(amount), !category.isEmpty else { return }
                        let record = TransactionRecord(
                            id: existingId ?? UUID().uuidString,
                            amount: value,
                            type: type,
                            category: category,
                            timestamp: Date(),
                            description: description.isEmpty ? nil : description
                        )
                        onSave(record)
                        dismiss()
                    }
                }
            }
        }
    }

    private var modeTitle: String {
        if case .edit = mode { return "Edit Transaction" }
        return "Add Transaction"
    }
}

private struct GoalForm: View {
    enum Mode { case add, edit(FinancialGoalRecord) }

    let mode: Mode
    let onSave: (FinancialGoalRecord) -> Void
    @Environment(\.dismiss) private var dismiss

    @State private var name = ""
    @State private var targetAmount = ""
    @State private var currentAmount = "0"
    @State private var category = ""
    private var existingId: String?

    init(mode: Mode, onSave: @escaping (FinancialGoalRecord) -> Void) {
        self.mode = mode
        self.onSave = onSave
        if case .edit(let goal) = mode {
            _name = State(initialValue: goal.name)
            _targetAmount = State(initialValue: String(goal.targetAmount))
            _currentAmount = State(initialValue: String(goal.currentAmount))
            _category = State(initialValue: goal.category)
            existingId = goal.id
        }
    }

    var body: some View {
        NavigationStack {
            Form {
                TextField("Goal name", text: $name)
                TextField("Target amount", text: $targetAmount).keyboardType(.decimalPad)
                TextField("Current amount", text: $currentAmount).keyboardType(.decimalPad)
                TextField("Category", text: $category)
            }
            .navigationTitle(modeTitle)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) { Button("Cancel") { dismiss() } }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Save") {
                        guard let target = Double(targetAmount), !name.isEmpty else { return }
                        let record = FinancialGoalRecord(
                            id: existingId ?? UUID().uuidString,
                            name: name,
                            targetAmount: target,
                            currentAmount: Double(currentAmount) ?? 0,
                            category: category
                        )
                        onSave(record)
                        dismiss()
                    }
                }
            }
        }
    }

    private var modeTitle: String {
        if case .edit = mode { return "Edit Goal" }
        return "Add Goal"
    }
}
