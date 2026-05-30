import SwiftUI

struct HomeView: View {
    @Bindable var store: AppStore

    private let pilotAreas = ["Westcliff University", "UCI Irvine", "LA / Bay Area"]

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    Text("Your AI-powered lifestyle coach for students and relocators.")
                        .foregroundStyle(.secondary)

                    VStack(alignment: .leading, spacing: 8) {
                        Text("Pilot launch areas")
                            .font(.headline)
                        ScrollView(.horizontal, showsIndicators: false) {
                            HStack {
                                ForEach(pilotAreas, id: \.self) { area in
                                    Label(area, systemImage: "mappin.and.ellipse")
                                        .font(.caption)
                                        .padding(.horizontal, 10)
                                        .padding(.vertical, 6)
                                        .background(Color.accentColor.opacity(0.15))
                                        .clipShape(Capsule())
                                }
                            }
                        }
                    }
                    .padding()
                    .background(Color(.secondarySystemBackground))
                    .clipShape(RoundedRectangle(cornerRadius: 12))

                    Text("Financial wellness snapshot")
                        .font(.title3.bold())

                    HStack(spacing: 8) {
                        metricCard(title: "Income", value: store.incomeTotal, color: .green)
                        metricCard(title: "Expenses", value: store.expenseTotal, color: .red)
                        metricCard(title: "Net", value: store.netTotal, color: store.netTotal >= 0 ? .green : .red)
                    }

                    Text("North Star: track monthly active use and cost savings.")
                        .font(.caption)
                        .foregroundStyle(.secondary)

                    dashboardRow(
                        title: "Finance",
                        subtitle: "\(store.transactions.count) transactions · \(store.financialGoals.count) goals",
                        systemImage: "dollarsign.circle"
                    )
                    dashboardRow(
                        title: "Health",
                        subtitle: "\(store.symptoms.count) symptoms logged",
                        systemImage: "heart.fill"
                    )
                    dashboardRow(
                        title: "Learning",
                        subtitle: "\(store.learningGoals.count) goals · offline study coach",
                        systemImage: "graduationcap.fill"
                    )

                    VStack(alignment: .leading, spacing: 8) {
                        Text("Value proposition")
                            .font(.headline)
                        Text("• Personalized cost-saving insights from local data\n• Lifestyle support beyond budgeting\n• Offline AI coach — no internet required\n• Localized resources for new campuses and cities")
                            .font(.subheadline)
                            .foregroundStyle(.secondary)
                    }
                    .padding()
                    .background(Color(.secondarySystemBackground))
                    .clipShape(RoundedRectangle(cornerRadius: 12))
                }
                .padding()
            }
            .navigationTitle("Lifeline AI")
        }
    }

    private func metricCard(title: String, value: Double, color: Color) -> some View {
        VStack(spacing: 4) {
            Text(title)
                .font(.caption)
                .foregroundStyle(.secondary)
            CurrencyText(value: value, color: color)
        }
        .frame(maxWidth: .infinity)
        .padding(.vertical, 12)
        .background(Color(.secondarySystemBackground))
        .clipShape(RoundedRectangle(cornerRadius: 10))
    }

    private func dashboardRow(title: String, subtitle: String, systemImage: String) -> some View {
        HStack(spacing: 12) {
            Image(systemName: systemImage)
                .font(.title2)
                .foregroundStyle(Color.accentColor)
            VStack(alignment: .leading) {
                Text(title).font(.headline)
                Text(subtitle).font(.caption).foregroundStyle(.secondary)
            }
            Spacer()
        }
        .padding()
        .background(Color(.secondarySystemBackground))
        .clipShape(RoundedRectangle(cornerRadius: 12))
    }
}
