import SwiftUI

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
