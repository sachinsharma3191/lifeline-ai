import SwiftUI

struct HomeView: View {
    @Bindable var store: AppStore
    private let config = AppConfigRoot.shared
    private var home: AppConfigHomeScreen { config.screens.home }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: AppTheme.sectionGap) {
                Text(config.app.name)
                    .font(.largeTitle.bold())

                Text(config.app.tagline)
                    .font(.body)
                    .foregroundStyle(.secondary)

                VStack(alignment: .leading, spacing: 8) {
                    Text(home.pilotAreasTitle)
                        .font(.headline.bold())
                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: AppTheme.chipGap) {
                            ForEach(home.pilotAreas, id: \.self) { area in
                                Label(area, systemImage: ConfigUiHelpers.iconSystemName(for: "services"))
                                    .font(.subheadline)
                                    .padding(.horizontal, 12)
                                    .padding(.vertical, 8)
                                    .background(AppTheme.primaryContainer)
                                    .clipShape(Capsule())
                            }
                        }
                    }
                }
                .padding(AppTheme.cardPadding)
                .frame(maxWidth: .infinity, alignment: .leading)
                .background(AppTheme.primaryContainer)
                .clipShape(RoundedRectangle(cornerRadius: 12))

                SectionHeader(title: home.financialSnapshotTitle)

                HStack(spacing: AppTheme.chipGap) {
                    ForEach(home.metrics) { metric in
                        metricCard(
                            label: metric.label,
                            value: metricValue(for: metric.id),
                            highlight: metric.id != "net" || store.netTotal >= 0
                        )
                    }
                }

                Text(home.northStarText)
                    .font(.caption)
                    .foregroundStyle(.secondary)

                SectionHeader(title: home.dashboardTitle)

                HStack(spacing: AppTheme.chipGap) {
                    ForEach(home.dashboardCards.filter { $0.id != "learning" }) { card in
                        dashboardCard(card)
                            .frame(maxWidth: .infinity)
                    }
                }

                if let learningCard = home.dashboardCards.first(where: { $0.id == "learning" }) {
                    dashboardCard(learningCard)
                }

                LifelineCard {
                    VStack(alignment: .leading, spacing: 8) {
                        Text(home.valuePropositionTitle)
                            .font(.headline.bold())
                        Text(home.valuePropositionItems.map { "• \($0)" }.joined(separator: "\n"))
                            .font(.body)
                    }
                }
            }
            .padding(AppTheme.screenPadding)
        }
    }

    private func metricValue(for id: String) -> Double {
        switch id {
        case "income": return store.incomeTotal
        case "expenses": return store.expenseTotal
        case "net": return store.netTotal
        default: return 0
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

    private func dashboardCard(_ card: AppConfigDashboardCard) -> some View {
        LifelineCard {
            HStack(spacing: 12) {
                Image(systemName: ConfigUiHelpers.iconSystemName(for: card.icon))
                    .font(.title2)
                    .foregroundStyle(Color.accentColor)
                VStack(alignment: .leading) {
                    Text(card.title).font(.headline)
                    Text(
                        ConfigUiHelpers.templateSubtitle(
                            card.subtitleTemplate,
                            transactionCount: store.transactions.count,
                            goalCount: store.financialGoals.count,
                            symptomCount: store.symptoms.count,
                            learningGoalCount: store.learningGoals.count
                        )
                    )
                    .font(.caption)
                    .foregroundStyle(.secondary)
                }
            }
        }
    }
}
