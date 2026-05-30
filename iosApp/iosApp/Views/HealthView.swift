import SwiftUI

struct HealthView: View {
    @Bindable var store: AppStore
    private let screen = AppConfigRoot.shared.screens.health
    private var symptomsSection: AppConfigSection { screen.sections["symptoms"]! }

    @State private var aiPrompt = ""
    @State private var showAddSymptom = false
    @State private var editingSymptom: SymptomRecord?

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
                            response: store.healthAiResponse,
                            suggestions: aiSuggestions,
                            coachLabel: screen.aiCoachLabel
                        ) { store.askHealthAi($0) }

                        SectionHeader(title: symptomsSection.title)
                        if store.symptoms.isEmpty {
                            Text(symptomsSection.emptyText).foregroundStyle(.secondary)
                        } else {
                            ForEach(store.symptoms.prefix(symptomsSection.listLimit ?? 10)) { symptomCard($0) }
                        }
                    }
                    .padding(AppTheme.screenPadding)
                    .padding(.bottom, 88)
                }

                SingleFloatingAction { showAddSymptom = true }
                    .padding(AppTheme.screenPadding)
            }
            .lifelineScreenTitle(screen.title)
            .sheet(isPresented: $showAddSymptom) {
                SymptomForm(
                    title: screen.forms["addSymptomTitle"] ?? "Add Symptom",
                    confirmLabel: "Add",
                    mode: .add
                ) { store.addSymptom($0) }
            }
            .sheet(item: $editingSymptom) { symptom in
                SymptomForm(
                    title: screen.forms["editSymptomTitle"] ?? "Edit Symptom",
                    confirmLabel: "Save",
                    mode: .edit(symptom)
                ) { store.updateSymptom($0) }
            }
        }
    }

    private func symptomCard(_ symptom: SymptomRecord) -> some View {
        LifelineCard {
            VStack(alignment: .leading, spacing: 4) {
                HStack {
                    Text(symptom.name).font(.headline)
                    Spacer()
                    Text("Severity: \(symptom.severity)/10")
                        .font(.caption)
                        .foregroundStyle(Color.accentColor)
                    Button { editingSymptom = symptom } label: { Image(systemName: "pencil") }
                }
                Text(symptom.category.rawValue)
                    .font(.caption)
                    .foregroundStyle(.secondary)
                if let notes = symptom.notes {
                    Text(notes).font(.body)
                }
            }
        }
    }
}

private struct SymptomForm: View {
    enum Mode { case add, edit(SymptomRecord) }

    let title: String
    let confirmLabel: String
    let mode: Mode
    let onSave: (SymptomRecord) -> Void
    @Environment(\.dismiss) private var dismiss

    @State private var name = ""
    @State private var severity = 5.0
    @State private var notes = ""
    private var existingSymptom: SymptomRecord?

    init(title: String, confirmLabel: String, mode: Mode, onSave: @escaping (SymptomRecord) -> Void) {
        self.title = title
        self.confirmLabel = confirmLabel
        self.mode = mode
        self.onSave = onSave
        if case .edit(let symptom) = mode {
            _name = State(initialValue: symptom.name)
            _severity = State(initialValue: Double(symptom.severity))
            _notes = State(initialValue: symptom.notes ?? "")
            existingSymptom = symptom
        }
    }

    var body: some View {
        NavigationStack {
            Form {
                TextField("Symptom Name", text: $name)
                Text("Severity: \(Int(severity))")
                Slider(value: $severity, in: 1...10, step: 1)
                TextField("Notes (optional)", text: $notes, axis: .vertical)
                    .lineLimit(3...5)
            }
            .navigationTitle(title)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) { Button("Cancel") { dismiss() } }
                ToolbarItem(placement: .confirmationAction) {
                    Button(confirmLabel) {
                        guard !name.isEmpty else { return }
                        onSave(SymptomRecord(
                            id: existingSymptom?.id ?? UUID().uuidString,
                            name: name,
                            severity: Int(severity),
                            timestamp: existingSymptom?.timestamp ?? Date(),
                            notes: notes.isEmpty ? nil : notes,
                            category: existingSymptom?.category ?? .other
                        ))
                        dismiss()
                    }
                }
            }
        }
        .presentationDetents([.medium, .large])
    }
}
