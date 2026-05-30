import SwiftUI

struct ContentView: View {
    @Bindable var store: AppStore

    var body: some View {
        TabView {
            HomeView(store: store)
                .tabItem { Label("Home", systemImage: "house.fill") }

            HealthView(store: store)
                .tabItem { Label("Health", systemImage: "heart.fill") }

            FinanceView(store: store)
                .tabItem { Label("Finance", systemImage: "dollarsign.circle.fill") }

            LearningView(store: store)
                .tabItem { Label("Learning", systemImage: "graduationcap.fill") }

            ServicesView(store: store)
                .tabItem { Label("Services", systemImage: "mappin.and.ellipse") }
        }
    }
}

#Preview {
    ContentView(store: AppStore())
}
