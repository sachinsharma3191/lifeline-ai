import SwiftUI

struct ServicesView: View {
    @Bindable var store: AppStore
    @State private var aiPrompt = ""
    @State private var selectedService: CommunityServiceRecord?

    private var searchResults: [CommunityServiceRecord] {
        CommunityServicesCatalog.search(query: store.serviceSearchQuery)
    }

    var body: some View {
        NavigationStack {
            Group {
                if let selectedService {
                    ServiceDetailView(service: selectedService) {
                        self.selectedService = nil
                    }
                } else {
                    servicesList
                }
            }
            .navigationTitle("Services")
        }
    }

    private var servicesList: some View {
        List {
            Section {
                TextField("Search services...", text: $store.serviceSearchQuery)
                    .textInputAutocapitalization(.never)
                    .autocorrectionDisabled()
            }

            Section("Offline AI Coach") {
                AiCoachSection(
                    prompt: $aiPrompt,
                    response: store.servicesAiResponse,
                    suggestions: ["Services help", "Community help"]
                ) { store.askServicesAi($0) }
            }

            if store.serviceSearchQuery.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
                Section {
                    Text("Search to see services near pilot campuses.")
                        .foregroundStyle(.secondary)
                }
            } else {
                Section("Results") {
                    ForEach(searchResults) { service in
                        Button {
                            selectedService = service
                        } label: {
                            VStack(alignment: .leading, spacing: 4) {
                                Text(service.name).font(.headline).foregroundStyle(.primary)
                                Text(service.description).font(.subheadline).foregroundStyle(.secondary)
                                Text(service.category.displayName).font(.caption).foregroundStyle(.tint)
                                if let location = service.location {
                                    Label(location, systemImage: "mappin")
                                        .font(.caption)
                                        .foregroundStyle(.secondary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

struct ServiceDetailView: View {
    let service: CommunityServiceRecord
    let onBack: () -> Void

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 12) {
                Button("Back", action: onBack)

                Text(service.description)
                Text("Category: \(service.category.displayName)")
                    .font(.caption)
                    .foregroundStyle(.secondary)

                if let location = service.location {
                    Button {
                        openMaps(for: location)
                    } label: {
                        Text(location)
                            .underline()
                            .foregroundStyle(.tint)
                    }
                }

                if let contact = service.contactInfo {
                    Text("Contact: \(contact)").font(.caption)
                }
                if let website = service.website {
                    Text("Website: \(website)").font(.caption).foregroundStyle(.secondary)
                }

                VStack(alignment: .leading, spacing: 8) {
                    Text("Map (demo)").font(.headline)
                    RoundedRectangle(cornerRadius: 12)
                        .fill(Color(.secondarySystemBackground))
                        .frame(height: 180)
                        .overlay {
                            Text("[Map placeholder — tap address for Apple Maps]")
                                .font(.caption)
                                .foregroundStyle(.secondary)
                                .padding()
                        }
                }
            }
            .padding()
        }
    }

    private func openMaps(for address: String) {
        let encoded = address.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? address
        if let url = URL(string: "http://maps.apple.com/?q=\(encoded)") {
            UIApplication.shared.open(url)
        }
    }
}
