import SwiftUI

struct ServicesView: View {
    @Bindable var store: AppStore
    @State private var aiPrompt = ""
    @State private var selectedService: CommunityServiceRecord?

    private let aiSuggestions = [
        AiSuggestion("Services help"),
        AiSuggestion("Community", prompt: "Community help")
    ]

    private var searchResults: [CommunityServiceRecord] {
        CommunityServicesCatalog.search(query: store.serviceSearchQuery)
    }

    var body: some View {
        Group {
            if let selectedService {
                ServiceDetailView(service: selectedService) {
                    self.selectedService = nil
                }
            } else {
                NavigationStack {
                    ScrollView {
                        VStack(alignment: .leading, spacing: 16) {
                            TextField("Search services...", text: $store.serviceSearchQuery)
                                .textFieldStyle(.roundedBorder)
                                .textInputAutocapitalization(.never)
                                .autocorrectionDisabled()

                            AiCoachBlock(
                                prompt: $aiPrompt,
                                response: store.servicesAiResponse,
                                suggestions: aiSuggestions
                            ) { store.askServicesAi($0) }

                            if store.serviceSearchQuery.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
                                Text("Search to see services")
                                    .foregroundStyle(.secondary)
                            } else {
                                ForEach(searchResults) { service in
                                    serviceCard(service)
                                }
                            }
                        }
                        .padding(16)
                    }
                    .lifelineScreenTitle("Community Services")
                }
            }
        }
    }

    private func serviceCard(_ service: CommunityServiceRecord) -> some View {
        Button {
            selectedService = service
        } label: {
            LifelineCard {
                VStack(alignment: .leading, spacing: 4) {
                    Text(service.name)
                        .font(.headline)
                        .foregroundStyle(.primary)
                    Text(service.description)
                        .font(.body)
                        .foregroundStyle(.primary)
                    Text(service.category.rawValue)
                        .font(.caption)
                        .foregroundStyle(Color.accentColor)
                    if let location = service.location {
                        Text("📍 \(location)")
                            .font(.caption)
                            .foregroundStyle(.secondary)
                    }
                }
            }
        }
        .buttonStyle(.plain)
    }
}

struct ServiceDetailView: View {
    let service: CommunityServiceRecord
    let onBack: () -> Void

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 12) {
                    Text(service.description)
                        .font(.body)

                    Text("Category: \(service.category.rawValue)")
                        .font(.caption)
                        .foregroundStyle(.secondary)

                    if let location = service.location {
                        Button {
                            openGoogleMaps(for: location)
                        } label: {
                            Text(location)
                                .font(.body)
                                .underline()
                                .foregroundStyle(Color.accentColor)
                        }
                    }

                    if let contact = service.contactInfo {
                        Text("Contact: \(contact)")
                            .font(.caption)
                    }

                    if let website = service.website {
                        Text("Website: \(website)")
                            .font(.caption)
                            .foregroundStyle(.secondary)
                    }

                    LifelineCard {
                        VStack(alignment: .leading, spacing: 8) {
                            Text("Map (demo)")
                                .font(.subheadline.bold())
                            Text("[Dummy Google Map Placeholder]")
                                .foregroundStyle(.secondary)
                                .frame(maxWidth: .infinity, minHeight: 180, alignment: .topLeading)
                        }
                    }
                }
                .padding(16)
            }
            .lifelineScreenTitle(service.name)
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    Button("Back", action: onBack)
                }
            }
        }
    }

    private func openGoogleMaps(for address: String) {
        let query = address.replacingOccurrences(of: " ", with: "+")
        if let url = URL(string: "https://www.google.com/maps/search/?api=1&query=\(query)") {
            UIApplication.shared.open(url)
        }
    }
}
