package com.example

import com.example.data.model.FundCategory
import com.example.data.model.RebalanceAction
import com.example.data.model.RebalanceAsset
import com.example.data.repository.MutualFundRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ExampleUnitTest {

    private lateinit var repository: MutualFundRepository

    @Before
    fun setUp() {
        repository = MutualFundRepository()
    }

    @Test
    fun testFundSearchAndAutoFetch() {
        val allFunds = repository.getAllFunds()
        assertTrue(allFunds.isNotEmpty())

        // 3-letter autocomplete matching
        val suggestions = repository.getAutoSuggestions("par")
        assertTrue(suggestions.isNotEmpty())
        assertTrue(suggestions.any { it.name.contains("Parag Parikh", ignoreCase = true) })

        val hdfcMatch = repository.searchFunds("hdf")
        assertTrue(hdfcMatch.any { it.name.contains("HDFC", ignoreCase = true) })
    }

    @Test
    fun testPortfolioStockExposureCalculation() {
        val all = repository.getAllFunds()
        val selectedFunds = all.take(5) // e.g. 5 funds

        val exposures = repository.calculatePortfolioStockExposure(selectedFunds)
        assertTrue(exposures.isNotEmpty())

        // ICICI Bank or HDFC Bank should be present across multiple funds
        val iciciExposure = exposures.find { it.stockName.contains("ICICI Bank", ignoreCase = true) }
        assertNotNull("ICICI Bank exposure should be computed across funds", iciciExposure)
        assertTrue("ICICI Bank should have positive aggregate percent", iciciExposure!!.aggregatePercent > 0.0)
        assertTrue("ICICI Bank should be held by at least 2 funds", iciciExposure.fundCount >= 2)
    }

    @Test
    fun testPairwiseFundOverlap() {
        val funds = repository.getAllFunds().take(3)
        val overlaps = repository.calculatePairwiseOverlaps(funds)
        assertTrue(overlaps.isNotEmpty())
        for (overlap in overlaps) {
            assertTrue("Overlap percent should be >= 0", overlap.overlapPercent >= 0.0)
            assertTrue("Common stocks count should be >= 0", overlap.commonStocksCount >= 0)
        }
    }

    @Test
    fun testSipCalculator() {
        val res = repository.calculateSip(
            monthlyAmount = 10000.0,
            tenureYears = 10,
            expectedReturnPercent = 12.0,
            stepUpPercent = 0.0
        )
        // 10,000 * 120 months = 1,200,000 invested
        assertEquals(1200000.0, res.totalInvested, 100.0)
        assertTrue("Maturity value must be greater than invested amount", res.totalMaturityAmount > res.totalInvested)
        assertTrue("Estimated returns must be positive", res.totalReturns > 0)
    }

    @Test
    fun testEducationCalculator() {
        val res = repository.calculateChildrenEducation(
            currentAge = 4,
            collegeAge = 18,
            currentCost = 2000000.0,
            inflationRatePercent = 8.0,
            expectedReturnPercent = 12.0
        )
        assertEquals(14, res.yearsRemaining)
        assertTrue("Future cost must exceed present cost due to inflation", res.futureCost > 2000000.0)
        assertTrue("Monthly SIP required must be positive", res.monthlySipRequired > 0.0)
    }

    @Test
    fun testRetirementCalculator() {
        val res = repository.calculateRetirement(
            currentAge = 30,
            retireAge = 60,
            lifeExpectancyAge = 85,
            currentMonthlyExpense = 50000.0,
            inflationPercent = 6.0,
            postRetirementReturnPercent = 8.0,
            preRetirementReturnPercent = 12.0
        )
        assertTrue("Corpus needed must be positive", res.requiredCorpus > 0.0)
        assertTrue("Monthly SIP must be positive", res.monthlySipRequired > 0.0)
    }

    @Test
    fun testTaxOptimizedRebalancing() {
        val assets = listOf(
            RebalanceAsset("Large Cap", 3000000.0, 50.0, 2000000.0, 24),
            RebalanceAsset("Mid Cap", 1000000.0, 50.0, 800000.0, 18)
        )
        val rep = repository.calculateTaxOptimizedRebalance(assets)
        assertEquals(4000000.0, rep.totalPortfolioValue, 100.0)
        assertTrue(rep.recommendations.any { it.action == RebalanceAction.SELL })
        assertTrue(rep.recommendations.any { it.action == RebalanceAction.BUY })
        assertNotNull(rep.smartTaxFreeStrategy)
    }
}
