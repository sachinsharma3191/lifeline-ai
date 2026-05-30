import SwiftUI

struct AiCoachSection: View {
    @Binding var prompt: String
    let response: String?
    let suggestions: [String]
    let onAsk: (String) -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: 8) {
                    ForEach(suggestions, id: \.self) { suggestion in
                        Button(suggestion) {
                            prompt = suggestion
                            onAsk(suggestion)
                        }
                        .buttonStyle(.bordered)
                        .controlSize(.small)
                    }
                }
            }

            TextField("Ask AI (offline)", text: $prompt)
                .textFieldStyle(.roundedBorder)
                .submitLabel(.done)
                .onSubmit { submit() }

            HStack {
                Spacer()
                Button("Ask", action: submit)
                    .buttonStyle(.borderedProminent)
                    .disabled(prompt.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty)
            }

            if let response {
                Text(response)
                    .padding(12)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .background(Color(.secondarySystemBackground))
                    .clipShape(RoundedRectangle(cornerRadius: 12))
            }
        }
    }

    private func submit() {
        let trimmed = prompt.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !trimmed.isEmpty else { return }
        onAsk(trimmed)
    }
}

struct CurrencyText: View {
    let value: Double
    var color: Color = .primary

    var body: some View {
        Text(value, format: .currency(code: "USD"))
            .foregroundStyle(color)
            .fontWeight(.semibold)
    }
}
