import SwiftUI

struct AiCoachSection: View {
    @Binding var prompt: String
    let response: String?
    let suggestions: [AiSuggestion]
    let onAsk: (String) -> Void

    var body: some View {
        AiCoachBlock(prompt: $prompt, response: response, suggestions: suggestions, onAsk: onAsk)
    }
}
