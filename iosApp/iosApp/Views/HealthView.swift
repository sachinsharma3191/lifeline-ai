import SwiftUI

struct HealthView: View {
    @Bindable var store: AppStore
    @State private var aiPrompt = ""
    @State private var showAddSymptom = false
    @State private var editingSymptom: SymptomRecord?

    private let aiSuggestions = [
        AiSuggestion("Health trends"),
        AiSuggestion("Symptoms", prompt: "Symptom summary")
    ]

    var body: some View {
        NavigationStack {
            ZStack(alignment: .bottomTrailing) {
                ScrollView {
                    VStack(alignment: .leading, spacing: 16) {
                        AiCoachBlock(
                            prompt: $aiPrompt,
                            response: store.healthAiResponse,
                            suggestions: aiSuggestions
                        ) { store.askHealthAi($0) }

                        SectionHeader(title: "Recent Symptoms")

                        if store.symptoms.isEmpty {
                            Text("No symptoms logged yet.")
                                .foregroundStyle(.secondary)
                        } else {
                            ForEach(store.symptoms.prefix(10)) { symptom in
                                symptomCard(symptom)
                            }
                        }
                    }
                    .padding(16)
                    .padding(.bottom, 88)
                }

                SingleFloatingAction { showAddSymptom = true }
                    .padding(16)
            }
            .lifelineScreenTitle("Health")
            .sheet(isPresented: $showAddSymptom) {
                SymptomForm(mode: .add) { store.addSymptom($0) }
            }
            .sheet(item: $editingSymptom) { symptom in
                SymptomForm(mode: .edit(symptom)) { store.updateSymptom($0) }
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
                    Button {
                        editingSymptom = symptom
                    } label: {
                        Image(systemName: "pencil")
                    }
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

    let mode: Mode
    let onSave: (SymptomRecord) -> Void
    @Environment(\.dismiss) private var dismiss

    @State private var name = ""
    @State private var severity = 5.0
    @State private var notes = ""
    private var existingSymptom: SymptomRecord?

    init(mode: Mode, onSave: @escaping (SymptomRecord) -> Void) {
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
            .navigationTitle(modeTitle)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) { Button("Cancel") { dismiss() } }
                ToolbarItem(placement: .confirmationAction) {
                    Button(modeButtonTitle) {
                        guard !name.isEmpty else { return }
                        let record = SymptomRecord(
                            id: existingSymptom?.id ?? UUID().uuidString,
                            name: name,
                            severity: Int(severity),
                            timestamp: existingSymptom?.timestamp ?? Date(),
                            notes: notes.isEmpty ? nil : notes,
                            category: existingSymptom?.category ?? .other
                        )
                        onSave(record)
                        dismiss()
                    }
                }
            }
        }
        .presentationDetents([.medium, .large])
    }

    private var modeTitle: String {
        if case .edit = mode { return "Edit Symptom" }
        return "Add Symptom"
    }

    private var modeButtonTitle: String {
        if case .edit = mode { return "Save" }
        return "Add"
    }
}
