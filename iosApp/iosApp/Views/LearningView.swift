import SwiftUI

struct LearningView: View {
    @Bindable var store: AppStore
    @State private var aiPrompt = ""
    @State private var showAddGoal = false
    @State private var editingGoal: LearningGoalRecord?

    private let aiSuggestions = [
        AiSuggestion("Progress", prompt: "Learning progress"),
        AiSuggestion("Study plan")
    ]

    var body: some View {
        NavigationStack {
            ZStack(alignment: .bottomTrailing) {
                ScrollView {
                    VStack(alignment: .leading, spacing: 16) {
                        AiCoachBlock(
                            prompt: $aiPrompt,
                            response: store.learningAiResponse,
                            suggestions: aiSuggestions
                        ) { store.askLearningAi($0) }

                        SectionHeader(title: "Learning Goals")

                        if store.learningGoals.isEmpty {
                            Text("No learning goals yet.")
                                .foregroundStyle(.secondary)
                        } else {
                            ForEach(store.learningGoals) { goal in
                                learningGoalCard(goal)
                            }
                        }

                        SectionHeader(title: "Modules")

                        if store.learningModules.isEmpty {
                            Text("No modules yet.")
                                .foregroundStyle(.secondary)
                        } else {
                            ForEach(store.learningModules) { module in
                                moduleCard(module)
                            }
                        }
                    }
                    .padding(16)
                    .padding(.bottom, 88)
                }

                SingleFloatingAction { showAddGoal = true }
                    .padding(16)
            }
            .lifelineScreenTitle("Learning")
            .sheet(isPresented: $showAddGoal) {
                LearningGoalForm(mode: .add) { store.addLearningGoal($0) }
            }
            .sheet(item: $editingGoal) { goal in
                LearningGoalForm(mode: .edit(goal)) { store.updateLearningGoal($0) }
            }
        }
    }

    private func learningGoalCard(_ goal: LearningGoalRecord) -> some View {
        LifelineCard {
            VStack(alignment: .leading, spacing: 8) {
                HStack {
                    Text(goal.title).font(.headline)
                    Spacer()
                    Button {
                        editingGoal = goal
                    } label: {
                        Image(systemName: "pencil")
                    }
                }
                Text(goal.description).font(.body)
                ProgressView(value: goal.progress)
                Text("\(Int(goal.progress * 100))% - \(goal.status.rawValue)")
                    .font(.caption)
                    .foregroundStyle(.secondary)
            }
        }
    }

    private func moduleCard(_ module: LearningModuleRecord) -> some View {
        LifelineCard {
            HStack(alignment: .top) {
                VStack(alignment: .leading, spacing: 4) {
                    Text(module.title).font(.headline)
                    Text(module.description).font(.caption).foregroundStyle(.secondary)
                    Text("\(module.estimatedDuration) min")
                        .font(.caption2)
                        .foregroundStyle(.secondary)
                }
                Spacer()
                if module.completed {
                    Text("✓ Completed")
                        .font(.caption.bold())
                        .foregroundStyle(Color.accentColor)
                } else {
                    Button("Complete") {
                        store.completeModule(id: module.id)
                    }
                    .buttonStyle(.borderedProminent)
                    .controlSize(.small)
                }
            }
        }
    }
}

private struct LearningGoalForm: View {
    enum Mode { case add, edit(LearningGoalRecord) }

    let mode: Mode
    let onSave: (LearningGoalRecord) -> Void
    @Environment(\.dismiss) private var dismiss

    @State private var title = ""
    @State private var description = ""
    private var existingGoal: LearningGoalRecord?

    init(mode: Mode, onSave: @escaping (LearningGoalRecord) -> Void) {
        self.mode = mode
        self.onSave = onSave
        if case .edit(let goal) = mode {
            _title = State(initialValue: goal.title)
            _description = State(initialValue: goal.description)
            existingGoal = goal
        }
    }

    var body: some View {
        NavigationStack {
            Form {
                TextField("Title", text: $title)
                TextField("Description", text: $description, axis: .vertical)
                    .lineLimit(3...5)
            }
            .navigationTitle(modeTitle)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) { Button("Cancel") { dismiss() } }
                ToolbarItem(placement: .confirmationAction) {
                    Button(modeButtonTitle) {
                        guard !title.isEmpty, !description.isEmpty else { return }
                        let record = LearningGoalRecord(
                            id: existingGoal?.id ?? UUID().uuidString,
                            title: title,
                            description: description,
                            progress: existingGoal?.progress ?? 0,
                            status: existingGoal?.status ?? .notStarted
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
        if case .edit = mode { return "Edit Learning Goal" }
        return "Add Learning Goal"
    }

    private var modeButtonTitle: String {
        if case .edit = mode { return "Save" }
        return "Add"
    }
}
