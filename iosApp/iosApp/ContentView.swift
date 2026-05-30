import SwiftUI

struct ContentView: View {
    @Bindable var store: AppStore
    private let config = AppConfigRoot.shared

    var body: some View {
        TabView {
            ForEach(config.tabs) { tab in
                tabView(for: tab)
                    .tabItem {
                        Label(tab.label, systemImage: ConfigUiHelpers.iconSystemName(for: tab.icon))
                    }
            }
        }
    }

    @ViewBuilder
    private func tabView(for tab: AppConfigTab) -> some View {
        switch tab.id {
        case "home": HomeView(store: store)
        case "health": HealthView(store: store)
        case "finance": FinanceView(store: store)
        case "learning": LearningView(store: store)
        case "services": ServicesView(store: store)
        default: HomeView(store: store)
        }
    }
}

#Preview {
    ContentView(store: AppStore())
}
