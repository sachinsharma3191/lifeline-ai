import SwiftUI

enum AppTheme {
    static let primaryContainer = Color.accentColor.opacity(0.18)
    static let secondaryContainer = Color(.secondarySystemBackground)
}

extension View {
    func lifelineScreenTitle(_ title: String) -> some View {
        navigationTitle(title)
            .navigationBarTitleDisplayMode(.inline)
            .toolbarBackground(AppTheme.primaryContainer, for: .navigationBar)
            .toolbarBackground(.visible, for: .navigationBar)
    }
}

struct SectionHeader: View {
    let title: String

    var body: some View {
        Text(title)
            .font(.title2.bold())
            .frame(maxWidth: .infinity, alignment: .leading)
    }
}

struct LifelineCard<Content: View>: View {
    @ViewBuilder var content: () -> Content

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            content()
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(16)
        .background(Color(.systemBackground))
        .clipShape(RoundedRectangle(cornerRadius: 12))
        .shadow(color: .black.opacity(0.06), radius: 2, y: 1)
    }
}

struct AiSuggestion: Identifiable {
    let id = UUID()
    let label: String
    let prompt: String

    init(_ label: String, prompt: String? = nil) {
        self.label = label
        self.prompt = prompt ?? label
    }
}

struct AiCoachBlock: View {
    @Binding var prompt: String
    let response: String?
    let suggestions: [AiSuggestion]
    let onAsk: (String) -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: 8) {
                    ForEach(suggestions) { suggestion in
                        Button(suggestion.label) {
                            prompt = suggestion.prompt
                            onAsk(suggestion.prompt)
                        }
                        .buttonStyle(.plain)
                        .foregroundStyle(Color.accentColor)
                    }
                }
            }

            TextField("Ask AI (offline)", text: $prompt)
                .textFieldStyle(.roundedBorder)
                .submitLabel(.done)
                .onSubmit(submit)

            HStack {
                Spacer()
                Button("Ask", action: submit)
                    .buttonStyle(.borderedProminent)
                    .disabled(prompt.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty)
            }

            if let response {
                Text(response)
                    .padding(16)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .background(AppTheme.secondaryContainer)
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
        Text(FormatUtils.formatAmount(value))
            .foregroundStyle(color)
            .fontWeight(.semibold)
    }
}

struct TransactionAmountText: View {
    let amount: Double
    let type: TransactionType

    var body: some View {
        let prefix = type == .expense ? "-" : "+"
        Text("\(prefix)$\(FormatUtils.formatAmount(amount))")
            .fontWeight(.semibold)
            .foregroundStyle(type == .expense ? .red : Color.accentColor)
    }
}

enum FormatUtils {
    static func formatAmount(_ value: Double) -> String {
        if value == value.rounded() && abs(value) < 1_000_000_000 {
            return String(format: "%.0f", value)
        }
        return String(format: "%.2f", value)
    }
}

struct DualFloatingActions: View {
    let onAddGoal: () -> Void
    let onAddTransaction: () -> Void

    var body: some View {
        HStack(spacing: 8) {
            Button(action: onAddGoal) {
                Image(systemName: "plus")
                    .font(.title3.bold())
                    .frame(width: 48, height: 48)
                    .background(Color.accentColor)
                    .foregroundStyle(.white)
                    .clipShape(Circle())
            }
            Button(action: onAddTransaction) {
                Image(systemName: "plus")
                    .font(.title2.bold())
                    .frame(width: 56, height: 56)
                    .background(Color.accentColor)
                    .foregroundStyle(.white)
                    .clipShape(Circle())
            }
        }
        .shadow(radius: 4, y: 2)
    }
}

struct SingleFloatingAction: View {
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Image(systemName: "plus")
                .font(.title2.bold())
                .frame(width: 56, height: 56)
                .background(Color.accentColor)
                .foregroundStyle(.white)
                .clipShape(Circle())
        }
        .shadow(radius: 4, y: 2)
    }
}
