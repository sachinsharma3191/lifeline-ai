import SwiftUI

struct HomeView: View {
    @Bindable var store: AppStore

    private let pilotAreas = ["Westcliff University", "UCI Irvine", "LA / Bay Area"]

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                Text("Lifeline AI")
                    .font(.largeTitle.bold())

                Text("Your AI-powered lifestyle coach for students and relocators — finance, health, learning, and localized community services in one place.")
                    .font(.body)
                    .foregroundStyle(.secondary)

                VStack(alignment: .leading, spacing: 8) {
                    Text("Pilot launch areas")
                        .font(.headline.bold())
                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: 8) {
                            ForEach(pilotAreas, id: \.self) { area in
                                Label(area, systemImage: "mappin.and.ellipse")
                                    .font(.subheadline)
                                    .padding(.horizontal, 12)
                                    .padding(.vertical, 8)
                                    .background(AppTheme.primaryContainer)
                                    .clipShape(Capsule())
                            }
                        }
                    }
                }
                .padding(16)
                .frame(maxWidth: .infinity, alignment: .leading)
                .background(AppTheme.primaryContainer)
                .clipShape(RoundedRectangle(cornerRadius: 12))

                SectionHeader(title: "Financial wellness snapshot")

                HStack(spacing: 8) {
                    metricCard(label: "Income", value: store.incomeTotal, highlight: true)
                    metricCard(label: "Expenses", value: store.expenseTotal, highlight: true)
                    metricCard(label: "Net", value: store.netTotal, highlight: store.netTotal >= 0)
                }

                Text("North Star: track monthly active use and cost savings as you build healthier money habits.")
                    .font(.caption)
                    .foregroundStyle(.secondary)

                SectionHeader(title: "Your dashboard")

                HStack(spacing: 8) {
                    dashboardCard(
                        title: "Finance",
                        subtitle: "\(store.transactions.count) transactions · \(store.financialGoals.count) goals",
                        systemImage: "building.columns"
                    )
                    .frame(maxWidth: .infinity)

                    dashboardCard(
                        title: "Health",
                        subtitle: "\(store.symptoms.count) symptoms logged",
                        systemImage: "heart.fill"
                    )
                    .frame(maxWidth: .infinity)
                }

                dashboardCard(
                    title: "Learning",
                    subtitle: "\(store.learningGoals.count) goals · offline AI study coach",
                    systemImage: "graduationcap.fill"
                )

                LifelineCard {
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Value proposition")
                            .font(.headline.bold())
                        Text(
                            "• Personalized cost-saving insights from your local data\n" +
                            "• Lifestyle support beyond budgeting (health, learning, services)\n" +
                            "• Offline AI coach — no API keys or internet required\n" +
                            "• Localized community resources for new campuses and cities"
                        )
                        .font(.body)
                    }
                }
            }
            .padding(16)
        }
    }

    private func metricCard(label: String, value: Double, highlight: Bool) -> some View {
        LifelineCard {
            VStack(spacing: 4) {
                Text(label)
                    .font(.caption)
                    .foregroundStyle(.secondary)
                Text("$\(FormatUtils.formatAmount(value))")
                    .font(.headline.bold())
                    .foregroundStyle(highlight ? Color.accentColor : .red)
            }
            .frame(maxWidth: .infinity)
        }
    }

    private func dashboardCard(title: String, subtitle: String, systemImage: String) -> some View {
        LifelineCard {
            HStack(spacing: 12) {
                Image(systemName: systemImage)
                    .font(.title2)
                    .foregroundStyle(Color.accentColor)
                VStack(alignment: .leading) {
                    Text(title).font(.headline)
                    Text(subtitle).font(.caption).foregroundStyle(.secondary)
                }
            }
        }
    }
}
