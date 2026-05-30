import SwiftUI

enum AppTheme {
    static var config: AppConfigRoot { AppConfigRoot.shared }

    static var primaryContainer: Color {
        ConfigUiHelpers.color(from: config.theme.colors.primaryContainer)
    }

    static var secondaryContainer: Color {
        ConfigUiHelpers.color(from: config.theme.colors.secondaryContainer)
    }

    static var screenPadding: CGFloat {
        CGFloat(config.theme.spacing.screenPadding)
    }

    static var cardPadding: CGFloat {
        CGFloat(config.theme.spacing.cardPadding)
    }

    static var sectionGap: CGFloat {
        CGFloat(config.theme.spacing.sectionGap)
    }

    static var chipGap: CGFloat {
        CGFloat(config.theme.spacing.chipGap)
    }
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
        .padding(AppTheme.cardPadding)
        .background(Color(.systemBackground))
        .clipShape(RoundedRectangle(cornerRadius: 12))
        .shadow(color: .black.opacity(0.06), radius: 2, y: 1)
    }
}

struct AiSuggestionItem: Identifiable {
    let id: String
    let label: String
    let prompt: String

    init(_ config: AppConfigAiSuggestion) {
        id = config.label
        label = config.label
        prompt = config.prompt
    }
}

struct AiCoachBlock: View {
    @Binding var prompt: String
    let response: String?
    let suggestions: [AiSuggestionItem]
    let coachLabel: String
    let onAsk: (String) -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: AppTheme.chipGap) {
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

            TextField(coachLabel, text: $prompt)
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
                    .padding(AppTheme.cardPadding)
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

struct AiCoachSection: View {
    @Binding var prompt: String
    let response: String?
    let suggestions: [AiSuggestionItem]
    let coachLabel: String
    let onAsk: (String) -> Void

    var body: some View {
        AiCoachBlock(
            prompt: $prompt,
            response: response,
            suggestions: suggestions,
            coachLabel: coachLabel,
            onAsk: onAsk
        )
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
