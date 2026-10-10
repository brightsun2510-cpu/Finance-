package com.example.data.model

data class EducationCalcResult(
    val yearsRemaining: Int,
    val futureCost: Double,
    val monthlySipRequired: Double,
    val totalInvestment: Double,
    val estimatedGains: Double
)

data class RetirementCalcResult(
    val yearsToRetire: Int,
    val monthlyExpenseAtRetirement: Double,
    val requiredCorpus: Double,
    val monthlySipRequired: Double,
    val totalInvested: Double,
    val wealthCreated: Double
)

data class FutureValueResult(
    val presentValue: Double,
    val futureValue: Double,
    val totalGains: Double,
    val growthMultiplier: Double
)

data class InflationPurchasingPowerResult(
    val initialAmount: Double,
    val futureNominalValue: Double, // if invested
    val futureCostOfSameGoods: Double, // purchasing power needed
    val realPurchasingPower: Double, // inflation-adjusted real value
    val purchasingPowerLossPercent: Double
)

data class SipCalcResult(
    val totalInvested: Double,
    val totalReturns: Double,
    val totalMaturityAmount: Double,
    val growthPercentage: Double,
    val yearlyBreakdown: List<YearlyMilestone>
)

data class YearlyMilestone(
    val year: Int,
    val cumulativeInvested: Double,
    val portfolioValue: Double
)

data class RebalanceAsset(
    val name: String,
    val currentAmount: Double,
    val targetPercent: Double,
    val purchaseCost: Double, // to compute capital gains
    val holdingPeriodMonths: Int // > 12 months = LTCG
)

data class RebalanceRecommendation(
    val assetName: String,
    val currentAmount: Double,
    val currentPercent: Double,
    val targetPercent: Double,
    val targetAmount: Double,
    val differenceAmount: Double, // positive = buy/underweight, negative = sell/overweight
    val action: RebalanceAction,
    val estimatedGainOnSale: Double,
    val estimatedTaxLiability: Double
)

enum class RebalanceAction {
    BUY,
    SELL,
    HOLD
}

data class TaxRebalanceReport(
    val totalPortfolioValue: Double,
    val recommendations: List<RebalanceRecommendation>,
    val totalSaleRequired: Double,
    val totalBuyRequired: Double,
    val totalLtcgRealized: Double,
    val ltcgExemptionUsed: Double,
    val taxableLtcg: Double,
    val ltcgTaxAmount: Double, // 12.5% on gains above 1.25L
    val stcgTaxAmount: Double, // 20% on short-term gains
    val totalTaxLiability: Double,
    val smartTaxFreeStrategy: String // Recommendation to rebalance via new SIPs
)
