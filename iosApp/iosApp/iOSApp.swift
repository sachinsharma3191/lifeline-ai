import SwiftUI

@main
struct iOSApp: App {
    @State private var store = AppStore()

    var body: some Scene {
        WindowGroup {
            ContentView(store: store)
        }
    }
}
