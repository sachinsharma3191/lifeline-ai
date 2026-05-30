import SwiftUI

struct FinanceView: View {
    @Bindable var store: AppStore
    private let screen = AppConfigRoot.shared.screens.finance
    private var goalsSection: AppConfigSection { screen.sections["goals"]! }
    private var transactionsSection: AppConfigSection { screen.sections["transactions"]! }

    @State private var aiPrompt = ""
    @State private var showAddTransaction = false
    @State private var showAddGoal = false
    @State private var editingTransaction: TransactionRecord?
    @State private var editingGoal: FinancialGoalRecord?

    private var aiSuggestions: [AiSuggestionItem] {
        screen.aiSuggestions.map(AiSuggestionItem.init)
    }

    var body: some View {
        NavigationStack {
            ZStack(alignment: .bottomTrailing) {
                ScrollView {
                    VStack(alignment: .leading, spacing: AppTheme.sectionGap) {
                        AiCoachBlock(
                            prompt: $aiPrompt,
                            response: store.financeAiResponse,
                            suggestions: aiSuggestions,
                            coachLabel: screen.aiCoachLabel
                        ) { store.askFinanceAi($0) }

                        SectionHeader(title: goalsSection.title)
                        if store.financialGoals.isEmpty {
                            Text(goalsSection.emptyText).foregroundStyle(.secondary)
                        } else {
                            ForEach(store.financialGoals) { goalCard($0) }
                        }

                        SectionHeader(title: transactionsSection.title)
                        if store.transactions.isEmpty {
                            Text(transactionsSection.emptyText).foregroundStyle(.secondary)
                        } else {
                            ForEach(store.transactions.prefix(transactionsSection.listLimit ?? 10)) { transactionCard($0) }
                        }
                    }
                    .padding(AppTheme.screenPadding)
                    .padding(.bottom, 88)
                }

                DualFloatingActions(
                    onAddGoal: { showAddGoal = true },
                    onAddTransaction: { showAddTransaction = true }
                )
                .padding(AppTheme.screenPadding)
            }
            .lifelineScreenTitle(screen.title)
            .sheet(isPresented: $showAddTransaction) {
                TransactionForm(
                    title: screen.forms["addTransactionTitle"] ?? "Add Transaction",
                    confirmLabel: "Add",
                    mode: .add
                ) { store.addTransaction($0) }
            }
            .sheet(item: $editingTransaction) { transaction in
                TransactionForm(
                    title: screen.forms["editTransactionTitle"] ?? "Edit Transaction",
                    confirmLabel: "Save",
                    mode: .edit(transaction)
                ) { store.updateTransaction($0) }
            }
            .sheet(isPresented: $showAddGoal) {
                GoalForm(
                    title: screen.forms["addGoalTitle"] ?? "Add Financial Goal",
                    confirmLabel: "Add",
                    mode: .add
                ) { store.addFinancialGoal($0) }
            }
            .sheet(item: $editingGoal) { goal in
                GoalForm(
                    title: screen.forms["editGoalTitle"] ?? "Edit Financial Goal",
                    confirmLabel: "Save",
                    mode: .edit(goal)
                ) { store.updateFinancialGoal($0) }
            }
        }
    }

    private func goalCard(_ goal: FinancialGoalRecord) -> some View {
        LifelineCard {
            VStack(alignment: .leading, spacing: 8) {
                HStack {
                    Text(goal.name).font(.headline)
                    Spacer()
                    Button { editingGoal = goal } label: { Image(systemName: "pencil") }
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
                Button { editingTransaction = transaction } label: { Image(systemName: "pencil") }
            }
        }
    }
}

private struct TransactionForm: View {
    enum Mode { case add, edit(TransactionRecord) }

    let title: String
    let confirmLabel: String
    let mode: Mode
    let onSave: (TransactionRecord) -> Void
    @Environment(\.dismiss) private var dismiss

    @State private var amount = ""
    @State private var category = ""
    @State private var description = ""
    private var existingRecord: TransactionRecord?

    init(title: String, confirmLabel: String, mode: Mode, onSave: @escaping (TransactionRecord) -> Void) {
        self.title = title
        self.confirmLabel = confirmLabel
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
                TextField("Amount", text: $amount).keyboardType(.decimalPad)
                TextField("Category", text: $category)
                TextField("Description", text: $description)
            }
            .navigationTitle(title)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) { Button("Cancel") { dismiss() } }
                ToolbarItem(placement: .confirmationAction) {
                    Button(confirmLabel) {
                        guard let value = Double(amount), !category.isEmpty else { return }
                        onSave(TransactionRecord(
                            id: existingRecord?.id ?? UUID().uuidString,
                            amount: value,
                            type: existingRecord?.type ?? .expense,
                            category: category,
                            timestamp: existingRecord?.timestamp ?? Date(),
                            description: description.isEmpty ? nil : description
                        ))
                        dismiss()
                    }
                }
            }
        }
        .presentationDetents([.medium])
    }
}

private struct GoalForm: View {
    enum Mode { case add, edit(FinancialGoalRecord) }

    let title: String
    let confirmLabel: String
    let mode: Mode
    let onSave: (FinancialGoalRecord) -> Void
    @Environment(\.dismiss) private var dismiss

    @State private var name = ""
    @State private var targetAmount = ""
    @State private var category = ""
    private var existingGoal: FinancialGoalRecord?

    init(title: String, confirmLabel: String, mode: Mode, onSave: @escaping (FinancialGoalRecord) -> Void) {
        self.title = title
        self.confirmLabel = confirmLabel
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
                TextField("Target Amount", text: $targetAmount).keyboardType(.decimalPad)
                TextField("Category", text: $category)
            }
            .navigationTitle(title)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) { Button("Cancel") { dismiss() } }
                ToolbarItem(placement: .confirmationAction) {
                    Button(confirmLabel) {
                        guard let target = Double(targetAmount), !name.isEmpty else { return }
                        onSave(FinancialGoalRecord(
                            id: existingGoal?.id ?? UUID().uuidString,
                            name: name,
                            targetAmount: target,
                            currentAmount: existingGoal?.currentAmount ?? 0,
                            category: category
                        ))
                        dismiss()
                    }
                }
            }
        }
        .presentationDetents([.medium])
    }
}
