import SwiftUI

struct FinanceView: View {
    @Bindable var store: AppStore
    @State private var aiPrompt = ""
    @State private var showAddTransaction = false
    @State private var showAddGoal = false
    @State private var editingTransaction: TransactionRecord?
    @State private var editingGoal: FinancialGoalRecord?

    private let aiSuggestions = [
        AiSuggestion("Finance summary"),
        AiSuggestion("Top category", prompt: "Top expense category"),
        AiSuggestion("Budget", prompt: "Budget advice")
    ]

    var body: some View {
        NavigationStack {
            ZStack(alignment: .bottomTrailing) {
                ScrollView {
                    VStack(alignment: .leading, spacing: 16) {
                        AiCoachBlock(
                            prompt: $aiPrompt,
                            response: store.financeAiResponse,
                            suggestions: aiSuggestions
                        ) { store.askFinanceAi($0) }

                        SectionHeader(title: "Financial Goals")

                        if store.financialGoals.isEmpty {
                            Text("No goals yet.")
                                .foregroundStyle(.secondary)
                        } else {
                            ForEach(store.financialGoals) { goal in
                                goalCard(goal)
                            }
                        }

                        SectionHeader(title: "Recent Transactions")

                        if store.transactions.isEmpty {
                            Text("No transactions yet.")
                                .foregroundStyle(.secondary)
                        } else {
                            ForEach(store.transactions.prefix(10)) { transaction in
                                transactionCard(transaction)
                            }
                        }
                    }
                    .padding(16)
                    .padding(.bottom, 88)
                }

                DualFloatingActions(
                    onAddGoal: { showAddGoal = true },
                    onAddTransaction: { showAddTransaction = true }
                )
                .padding(16)
            }
            .lifelineScreenTitle("Finance")
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

    private func goalCard(_ goal: FinancialGoalRecord) -> some View {
        LifelineCard {
            VStack(alignment: .leading, spacing: 8) {
                HStack {
                    Text(goal.name).font(.headline)
                    Spacer()
                    Button {
                        editingGoal = goal
                    } label: {
                        Image(systemName: "pencil")
                    }
                }
                ProgressView(value: min(goal.currentAmount / max(goal.targetAmount, 1), 1))
                Text("$\(FormatUtils.formatAmount(goal.currentAmount)) / $\(FormatUtils.formatAmount(goal.targetAmount))")
                    .font(.caption)
                    .foregroundStyle(.secondary)
            }
        }
    }

    private func transactionCard(_ transaction: TransactionRecord) -> some View {
        LifelineCard {
            HStack {
                VStack(alignment: .leading) {
                    Text(transaction.category).font(.headline)
                    if let description = transaction.description {
                        Text(description).font(.caption).foregroundStyle(.secondary)
                    }
                }
                Spacer()
                TransactionAmountText(amount: transaction.amount, type: transaction.type)
                Button {
                    editingTransaction = transaction
                } label: {
                    Image(systemName: "pencil")
                }
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
    private var existingRecord: TransactionRecord?

    init(mode: Mode, onSave: @escaping (TransactionRecord) -> Void) {
        self.mode = mode
        self.onSave = onSave
        if case .edit(let record) = mode {
            _amount = State(initialValue: String(record.amount))
            _category = State(initialValue: record.category)
            _description = State(initialValue: record.description ?? "")
            existingRecord = record
        }
    }

    var body: some View {
        NavigationStack {
            Form {
                TextField("Amount", text: $amount)
                    .keyboardType(.decimalPad)
                TextField("Category", text: $category)
                TextField("Description", text: $description)
            }
            .navigationTitle(modeTitle)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) { Button("Cancel") { dismiss() } }
                ToolbarItem(placement: .confirmationAction) {
                    Button(modeButtonTitle) {
                        guard let value = Double(amount), !category.isEmpty else { return }
                        let record = TransactionRecord(
                            id: existingRecord?.id ?? UUID().uuidString,
                            amount: value,
                            type: existingRecord?.type ?? .expense,
                            category: category,
                            timestamp: existingRecord?.timestamp ?? Date(),
                            description: description.isEmpty ? nil : description
                        )
                        onSave(record)
                        dismiss()
                    }
                }
            }
        }
        .presentationDetents([.medium])
    }

    private var modeTitle: String {
        if case .edit = mode { return "Edit Transaction" }
        return "Add Transaction"
    }

    private var modeButtonTitle: String {
        if case .edit = mode { return "Save" }
        return "Add"
    }
}

private struct GoalForm: View {
    enum Mode { case add, edit(FinancialGoalRecord) }

    let mode: Mode
    let onSave: (FinancialGoalRecord) -> Void
    @Environment(\.dismiss) private var dismiss

    @State private var name = ""
    @State private var targetAmount = ""
    @State private var category = ""
    private var existingGoal: FinancialGoalRecord?

    init(mode: Mode, onSave: @escaping (FinancialGoalRecord) -> Void) {
        self.mode = mode
        self.onSave = onSave
        if case .edit(let goal) = mode {
            _name = State(initialValue: goal.name)
            _targetAmount = State(initialValue: String(goal.targetAmount))
            _category = State(initialValue: goal.category)
            existingGoal = goal
        }
    }

    var body: some View {
        NavigationStack {
            Form {
                TextField("Goal Name", text: $name)
                TextField("Target Amount", text: $targetAmount)
                    .keyboardType(.decimalPad)
                TextField("Category", text: $category)
            }
            .navigationTitle(modeTitle)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) { Button("Cancel") { dismiss() } }
                ToolbarItem(placement: .confirmationAction) {
                    Button(modeButtonTitle) {
                        guard let target = Double(targetAmount), !name.isEmpty else { return }
                        let record = FinancialGoalRecord(
                            id: existingGoal?.id ?? UUID().uuidString,
                            name: name,
                            targetAmount: target,
                            currentAmount: existingGoal?.currentAmount ?? 0,
                            category: category
                        )
                        onSave(record)
                        dismiss()
                    }
                }
            }
        }
        .presentationDetents([.medium])
    }

    private var modeTitle: String {
        if case .edit = mode { return "Edit Financial Goal" }
        return "Add Financial Goal"
    }

    private var modeButtonTitle: String {
        if case .edit = mode { return "Save" }
        return "Add"
    }
}
