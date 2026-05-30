import SwiftUI

struct HealthView: View {
    @Bindable var store: AppStore
    @State private var aiPrompt = ""
    @State private var showAddSymptom = false
    @State private var editingSymptom: SymptomRecord?

    var body: some View {
        NavigationStack {
            List {
                Section("Offline AI Coach") {
                    AiCoachSection(
                        prompt: $aiPrompt,
                        response: store.healthAiResponse,
                        suggestions: ["Health trends", "Symptom summary"]
                    ) { store.askHealthAi($0) }
                }

                Section("Recent Symptoms") {
                    if store.symptoms.isEmpty {
                        Text("No symptoms logged yet.")
                            .foregroundStyle(.secondary)
                    } else {
                        ForEach(store.symptoms.prefix(20)) { symptom in
                            VStack(alignment: .leading, spacing: 4) {
                                HStack {
                                    Text(symptom.name).font(.headline)
                                    Spacer()
                                    Text("Severity \(symptom.severity)/10")
                                        .font(.caption)
                                        .foregroundStyle(.secondary)
                                    Button("Edit") { editingSymptom = symptom }
                                        .font(.caption)
                                }
                                Text(symptom.category.rawValue.capitalized)
                                    .font(.caption)
                                    .foregroundStyle(.secondary)
                                if let notes = symptom.notes {
                                    Text(notes).font(.subheadline)
                                }
                            }
                        }
                    }
                }
            }
            .navigationTitle("Health")
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button { showAddSymptom = true } label: { Image(systemName: "plus") }
                }
            }
            .sheet(isPresented: $showAddSymptom) {
                SymptomForm(mode: .add) { store.addSymptom($0) }
            }
            .sheet(item: $editingSymptom) { symptom in
                SymptomForm(mode: .edit(symptom)) { store.updateSymptom($0) }
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
    @State private var category: SymptomCategory = .other
    private var existingId: String?

    init(mode: Mode, onSave: @escaping (SymptomRecord) -> Void) {
        self.mode = mode
        self.onSave = onSave
        if case .edit(let symptom) = mode {
            _name = State(initialValue: symptom.name)
            _severity = State(initialValue: Double(symptom.severity))
            _notes = State(initialValue: symptom.notes ?? "")
            _category = State(initialValue: symptom.category)
            existingId = symptom.id
        }
    }

    var body: some View {
        NavigationStack {
            Form {
                TextField("Symptom name", text: $name)
                Picker("Category", selection: $category) {
                    ForEach(SymptomCategory.allCases, id: \.self) { Text($0.rawValue.capitalized).tag($0) }
                }
                VStack(alignment: .leading) {
                    Text("Severity: \(Int(severity))")
                    Slider(value: $severity, in: 1...10, step: 1)
                }
                TextField("Notes (optional)", text: $notes, axis: .vertical)
                    .lineLimit(3...5)
            }
            .navigationTitle(modeTitle)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) { Button("Cancel") { dismiss() } }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Save") {
                        guard !name.isEmpty else { return }
                        let record = SymptomRecord(
                            id: existingId ?? UUID().uuidString,
                            name: name,
                            severity: Int(severity),
                            timestamp: Date(),
                            notes: notes.isEmpty ? nil : notes,
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
        if case .edit = mode { return "Edit Symptom" }
        return "Add Symptom"
    }
}
