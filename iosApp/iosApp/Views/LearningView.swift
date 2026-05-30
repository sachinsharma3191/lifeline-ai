import SwiftUI

struct LearningView: View {
    @Bindable var store: AppStore
    private let screen = AppConfigRoot.shared.screens.learning
    private var goalsSection: AppConfigLearningSection { screen.sections["goals"]! }
    private var modulesSection: AppConfigLearningSection { screen.sections["modules"]! }

    @State private var aiPrompt = ""
    @State private var showAddGoal = false
    @State private var editingGoal: LearningGoalRecord?

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
                            response: store.learningAiResponse,
                            suggestions: aiSuggestions,
                            coachLabel: screen.aiCoachLabel
                        ) { store.askLearningAi($0) }

                        SectionHeader(title: goalsSection.title)
                        if store.learningGoals.isEmpty {
                            Text(goalsSection.emptyText).foregroundStyle(.secondary)
                        } else {
                            ForEach(store.learningGoals) { learningGoalCard($0) }
                        }

                        SectionHeader(title: modulesSection.title)
                        if store.learningModules.isEmpty {
                            Text(modulesSection.emptyText).foregroundStyle(.secondary)
                        } else {
                            ForEach(store.learningModules) { moduleCard($0) }
                        }
                    }
                    .padding(AppTheme.screenPadding)
                    .padding(.bottom, 88)
                }

                SingleFloatingAction { showAddGoal = true }
                    .padding(AppTheme.screenPadding)
            }
            .lifelineScreenTitle(screen.title)
            .sheet(isPresented: $showAddGoal) {
                LearningGoalForm(
                    title: screen.forms["addGoalTitle"] ?? "Add Learning Goal",
                    confirmLabel: "Add",
                    mode: .add
                ) { store.addLearningGoal($0) }
            }
            .sheet(item: $editingGoal) { goal in
                LearningGoalForm(
                    title: screen.forms["editGoalTitle"] ?? "Edit Learning Goal",
                    confirmLabel: "Save",
                    mode: .edit(goal)
                ) { store.updateLearningGoal($0) }
            }
        }
    }

    private func learningGoalCard(_ goal: LearningGoalRecord) -> some View {
        LifelineCard {
            VStack(alignment: .leading, spacing: 8) {
                HStack {
                    Text(goal.title).font(.headline)
                    Spacer()
                    Button { editingGoal = goal } label: { Image(systemName: "pencil") }
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
                    Text(modulesSection.completedLabel ?? "✓ Completed")
                        .font(.caption.bold())
                        .foregroundStyle(Color.accentColor)
                } else {
                    Button(modulesSection.completeButtonLabel ?? "Complete") {
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

    let title: String
    let confirmLabel: String
    let mode: Mode
    let onSave: (LearningGoalRecord) -> Void
    @Environment(\.dismiss) private var dismiss

    @State private var goalTitle = ""
    @State private var description = ""
    private var existingGoal: LearningGoalRecord?

    init(title: String, confirmLabel: String, mode: Mode, onSave: @escaping (LearningGoalRecord) -> Void) {
        self.title = title
        self.confirmLabel = confirmLabel
        self.mode = mode
        self.onSave = onSave
        if case .edit(let goal) = mode {
            _goalTitle = State(initialValue: goal.title)
            _description = State(initialValue: goal.description)
            existingGoal = goal
        }
    }

    var body: some View {
        NavigationStack {
            Form {
                TextField("Title", text: $goalTitle)
                TextField("Description", text: $description, axis: .vertical)
                    .lineLimit(3...5)
            }
            .navigationTitle(title)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) { Button("Cancel") { dismiss() } }
                ToolbarItem(placement: .confirmationAction) {
                    Button(confirmLabel) {
                        guard !goalTitle.isEmpty, !description.isEmpty else { return }
                        onSave(LearningGoalRecord(
                            id: existingGoal?.id ?? UUID().uuidString,
                            title: goalTitle,
                            description: description,
                            progress: existingGoal?.progress ?? 0,
                            status: existingGoal?.status ?? .notStarted
                        ))
                        dismiss()
                    }
                }
            }
        }
        .presentationDetents([.medium])
    }
}
