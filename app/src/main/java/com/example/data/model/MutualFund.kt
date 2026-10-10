package com.example.data.model

enum class FundCategory(val displayName: String, val benchmark: String) {
    FLEXI_CAP("Flexi Cap", "NIFTY 500 TRI"),
    LARGE_CAP("Large Cap", "NIFTY 100 TRI"),
    MID_CAP("Mid Cap", "NIFTY Midcap 150 TRI"),
    SMALL_CAP("Small Cap", "NIFTY Smallcap 250 TRI"),
    LARGE_AND_MID("Large & Mid Cap", "NIFTY LargeMidcap 250 TRI"),
    ELSS("ELSS Tax Saver", "NIFTY 500 TRI"),
    BALANCED_ADVANTAGE("Balanced Advantage", "NIFTY 50 Hybrid Composite")
}

data class MarketCapAlloc(
    val largeCap: Double,
    val midCap: Double,
    val smallCap: Double
)

data class StockHolding(
    val name: String,
    val ticker: String,
    val sector: String,
    val weight: Double // percentage e.g. 9.5
)

data class NavPoint(
    val yearLabel: String,
    val value: Double // Growth indexed from ₹10,000 base
)

data class MutualFund(
    val id: String,
    val name: String,
    val amc: String,
    val category: FundCategory,
    val nav: Double,
    val peRatio: Double,
    val categoryAvgPe: Double,
    val return1Y: Double,
    val return3Y: Double,
    val return5Y: Double,
    val alpha: Double,
    val beta: Double,
    val standardDeviation: Double,
    val sharpeRatio: Double,
    val expenseRatio: Double,
    val aumCr: Double,
    val fundManager: String,
    val ratingStars: Int,
    val marketCapAlloc: MarketCapAlloc,
    val topHoldings: List<StockHolding>,
    val navHistory5Y: List<NavPoint>
)

data class PortfolioStockExposure(
    val stockName: String,
    val ticker: String,
    val sector: String,
    val aggregatePercent: Double,
    val fundCount: Int,
    val fundBreakdown: List<FundShare>
)

data class FundShare(
    val fundName: String,
    val individualHoldingPercent: Double,
    val effectiveContributionPercent: Double
)

data class PairwiseOverlap(
    val fund1Name: String,
    val fund2Name: String,
    val overlapPercent: Double,
    val commonStocksCount: Int,
    val topCommonStocks: List<String>
)

data class PortfolioSummary(
    val totalFunds: Int,
    val weightedReturn3Y: Double,
    val weightedReturn5Y: Double,
    val weightedPeRatio: Double,
    val weightedCategoryPe: Double,
    val weightedAlpha: Double,
    val weightedStdDev: Double,
    val aggregateLargeCap: Double,
    val aggregateMidCap: Double,
    val aggregateSmallCap: Double,
    val topConsolidatedHoldings: List<PortfolioStockExposure>,
    val pairwiseOverlaps: List<PairwiseOverlap>
)
