import SwiftUI

struct ServicesView: View {
    @Bindable var store: AppStore
    private let config = AppConfigRoot.shared
    private var screen: AppConfigServicesScreen { config.screens.services }

    @State private var aiPrompt = ""
    @State private var selectedService: CommunityServiceRecord?

    private var aiSuggestions: [AiSuggestionItem] {
        screen.aiSuggestions.map(AiSuggestionItem.init)
    }

    private var searchResults: [CommunityServiceRecord] {
        CommunityServicesCatalog.search(query: store.serviceSearchQuery)
    }

    var body: some View {
        Group {
            if let selectedService {
                ServiceDetailView(service: selectedService, detail: screen.detail) {
                    self.selectedService = nil
                }
            } else {
                NavigationStack {
                    ScrollView {
                        VStack(alignment: .leading, spacing: AppTheme.sectionGap) {
                            TextField(screen.searchPlaceholder, text: $store.serviceSearchQuery)
                                .textFieldStyle(.roundedBorder)
                                .textInputAutocapitalization(.never)
                                .autocorrectionDisabled()

                            AiCoachBlock(
                                prompt: $aiPrompt,
                                response: store.servicesAiResponse,
                                suggestions: aiSuggestions,
                                coachLabel: screen.aiCoachLabel
                            ) { store.askServicesAi($0) }

                            if store.serviceSearchQuery.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
                                Text(screen.searchEmptyText)
                                    .foregroundStyle(.secondary)
                            } else {
                                ForEach(searchResults) { serviceCard($0) }
                            }
                        }
                        .padding(AppTheme.screenPadding)
                    }
                    .lifelineScreenTitle(screen.title)
                }
            }
        }
    }

    private func serviceCard(_ service: CommunityServiceRecord) -> some View {
        Button { selectedService = service } label: {
            LifelineCard {
                VStack(alignment: .leading, spacing: 4) {
                    Text(service.name).font(.headline).foregroundStyle(.primary)
                    Text(service.description).font(.body).foregroundStyle(.primary)
                    Text(service.category.rawValue).font(.caption).foregroundStyle(Color.accentColor)
                    if let location = service.location {
                        Text("📍 \(location)").font(.caption).foregroundStyle(.secondary)
                    }
                }
            }
        }
        .buttonStyle(.plain)
    }
}

struct ServiceDetailView: View {
    let service: CommunityServiceRecord
    let detail: AppConfigServicesDetail
    let onBack: () -> Void

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 12) {
                    Text(service.description).font(.body)
                    Text("\(detail.categoryPrefix)\(service.category.rawValue)")
                        .font(.caption)
                        .foregroundStyle(.secondary)

                    if let location = service.location {
                        Button {
                            if let url = ConfigUiHelpers.mapsUrl(
                                template: AppConfigRoot.shared.servicesCatalog.mapsUrlTemplate,
                                address: location
                            ) {
                                UIApplication.shared.open(url)
                            }
                        } label: {
                            Text(location)
                                .font(.body)
                                .underline()
                                .foregroundStyle(Color.accentColor)
                        }
                    }

                    if let contact = service.contactInfo {
                        Text("\(detail.contactPrefix)\(contact)").font(.caption)
                    }
                    if let website = service.website {
                        Text("\(detail.websitePrefix)\(website)").font(.caption).foregroundStyle(.secondary)
                    }

                    LifelineCard {
                        VStack(alignment: .leading, spacing: 8) {
                            Text(detail.mapDemoTitle).font(.subheadline.bold())
                            Text(detail.mapPlaceholder)
                                .foregroundStyle(.secondary)
                                .frame(maxWidth: .infinity, minHeight: 180, alignment: .topLeading)
                        }
                    }
                }
                .padding(AppTheme.screenPadding)
            }
            .lifelineScreenTitle(service.name)
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    Button(detail.backLabel, action: onBack)
                }
            }
        }
    }
}
