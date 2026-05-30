import SwiftUI

struct LearningView: View {
    @Bindable var store: AppStore
    @State private var aiPrompt = ""
    @State private var showAddGoal = false
    @State private var editingGoal: LearningGoalRecord?

    var body: some View {
        NavigationStack {
            List {
                Section("Offline AI Coach") {
                    AiCoachSection(
                        prompt: $aiPrompt,
                        response: store.learningAiResponse,
                        suggestions: ["Learning progress", "Study plan"]
                    ) { store.askLearningAi($0) }
                }

                Section("Learning Goals") {
                    ForEach(store.learningGoals) { goal in
                        VStack(alignment: .leading, spacing: 6) {
                            HStack {
                                Text(goal.title).font(.headline)
                                Spacer()
                                Button("Edit") { editingGoal = goal }
                                    .font(.caption)
                            }
                            Text(goal.description).font(.subheadline).foregroundStyle(.secondary)
                            ProgressView(value: goal.progress)
                            Text("\(Int(goal.progress * 100))% · \(goal.status.rawValue.replacingOccurrences(of: "_", with: " ").capitalized)")
                                .font(.caption)
                                .foregroundStyle(.secondary)
                        }
                    }
                }

                Section("Modules") {
                    ForEach(store.learningModules) { module in
                        HStack(alignment: .top) {
                            VStack(alignment: .leading, spacing: 4) {
                                Text(module.title).font(.headline)
                                Text(module.description).font(.caption).foregroundStyle(.secondary)
                                Text("\(module.estimatedDuration) min").font(.caption2).foregroundStyle(.secondary)
                            }
                            Spacer()
                            if module.completed {
                                Text("Done").font(.caption.bold()).foregroundStyle(.green)
                            } else {
                                Button("Complete") { store.completeModule(id: module.id) }
                                    .font(.caption)
                            }
                        }
                    }
                }
            }
            .navigationTitle("Learning")
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button { showAddGoal = true } label: { Image(systemName: "plus") }
                }
            }
            .sheet(isPresented: $showAddGoal) {
                LearningGoalForm(mode: .add) { store.addLearningGoal($0) }
            }
            .sheet(item: $editingGoal) { goal in
                LearningGoalForm(mode: .edit(goal)) { store.updateLearningGoal($0) }
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
    @State private var progress = 0.0
    @State private var status: GoalStatus = .notStarted
    private var existingId: String?

    init(mode: Mode, onSave: @escaping (LearningGoalRecord) -> Void) {
        self.mode = mode
        self.onSave = onSave
        if case .edit(let goal) = mode {
            _title = State(initialValue: goal.title)
            _description = State(initialValue: goal.description)
            _progress = State(initialValue: goal.progress)
            _status = State(initialValue: goal.status)
            existingId = goal.id
        }
    }

    var body: some View {
        NavigationStack {
            Form {
                TextField("Title", text: $title)
                TextField("Description", text: $description, axis: .vertical).lineLimit(3...5)
                Picker("Status", selection: $status) {
                    ForEach(GoalStatus.allCases, id: \.self) { Text($0.rawValue.replacingOccurrences(of: "_", with: " ").capitalized).tag($0) }
                }
                VStack(alignment: .leading) {
                    Text("Progress: \(Int(progress * 100))%")
                    Slider(value: $progress, in: 0...1)
                }
            }
            .navigationTitle(modeTitle)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) { Button("Cancel") { dismiss() } }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Save") {
                        guard !title.isEmpty, !description.isEmpty else { return }
                        let record = LearningGoalRecord(
                            id: existingId ?? UUID().uuidString,
                            title: title,
                            description: description,
                            progress: progress,
                            status: status
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
