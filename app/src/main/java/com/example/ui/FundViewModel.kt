package com.example.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.MutualFundRepository
import com.example.pdf.PdfReportGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

enum class AppNavTab(val title: String) {
    FUNDS("Funds & Search"),
    COMPARE("Compare & Overlap"),
    CALCULATORS("Calculators"),
    PDF_REPORT("Client PDF Report")
}

enum class CalculatorType(val title: String) {
    SIP("SIP & Step-Up"),
    EDUCATION("Children Education"),
    RETIREMENT("Retirement Plan"),
    FUTURE_VALUE("Future Value"),
    INFLATION("Inflation & Purchasing"),
    TAX_REBALANCE("Tax Rebalancing")
}

data class FundUiState(
    val currentTab: AppNavTab = AppNavTab.FUNDS,
    val searchQuery: String = "",
    val autoSuggestions: List<MutualFund> = emptyList(),
    val selectedCategory: FundCategory? = null,
    val allFunds: List<MutualFund> = emptyList(),
    val filteredFunds: List<MutualFund> = emptyList(),
    val inspectedFund: MutualFund? = null,

    // Multi-fund comparison set
    val selectedComparisonFunds: List<MutualFund> = emptyList(),
    val portfolioSummary: PortfolioSummary = PortfolioSummary(
        totalFunds = 0,
        weightedReturn3Y = 0.0,
        weightedReturn5Y = 0.0,
        weightedPeRatio = 0.0,
        weightedCategoryPe = 0.0,
        weightedAlpha = 0.0,
        weightedStdDev = 0.0,
        aggregateLargeCap = 0.0,
        aggregateMidCap = 0.0,
        aggregateSmallCap = 0.0,
        topConsolidatedHoldings = emptyList(),
        pairwiseOverlaps = emptyList()
    ),

    // PDF Report
    val clientName: String = "Dr. Rajesh & Sunita Verma",
    val advisorName: String = "FundIntel Wealth Advisory",
    val isGeneratingPdf: Boolean = false,
    val lastGeneratedPdf: File? = null,
    val pdfStatusMessage: String? = null,

    // Calculator tab selection
    val activeCalculator: CalculatorType = CalculatorType.SIP,

    // Education inputs & result
    val eduChildAge: Int = 4,
    val eduCollegeAge: Int = 18,
    val eduCurrentCost: Double = 2500000.0, // 25 Lakhs
    val eduInflation: Double = 10.0,
    val eduReturn: Double = 12.0,
    val eduResult: EducationCalcResult? = null,

    // Retirement inputs & result
    val retCurrentAge: Int = 32,
    val retRetireAge: Int = 58,
    val retLifeExpAge: Int = 85,
    val retMonthlyExpense: Double = 65000.0,
    val retInflation: Double = 6.0,
    val retPostReturn: Double = 8.0,
    val retPreReturn: Double = 12.5,
    val retResult: RetirementCalcResult? = null,

    // Future Value inputs & result
    val fvPrincipal: Double = 500000.0,
    val fvYears: Int = 10,
    val fvReturn: Double = 14.0,
    val fvResult: FutureValueResult? = null,

    // Inflation Purchasing Power inputs & result
    val infPrincipal: Double = 1000000.0,
    val infYears: Int = 15,
    val infInflationRate: Double = 7.0,
    val infReturnRate: Double = 12.0,
    val infResult: InflationPurchasingPowerResult? = null,

    // SIP inputs & result
    val sipAmount: Double = 20000.0,
    val sipYears: Int = 15,
    val sipExpectedReturn: Double = 13.5,
    val sipStepUpPercent: Double = 10.0,
    val sipResult: SipCalcResult? = null,

    // Tax Rebalancing assets & result
    val rebalanceAssets: List<RebalanceAsset> = listOf(
        RebalanceAsset("Large Cap Equity Fund", 2800000.0, 50.0, 1800000.0, 24),
        RebalanceAsset("Mid & Small Cap Funds", 1900000.0, 30.0, 1100000.0, 18),
        RebalanceAsset("Short Duration Debt / Arbitrage", 500000.0, 20.0, 480000.0, 14)
    ),
    val rebalanceReport: TaxRebalanceReport? = null
)

class FundViewModel(
    private val repository: MutualFundRepository = MutualFundRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(FundUiState())
    val uiState: StateFlow<FundUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
        recalculateAllCalculators()
    }

    private fun loadInitialData() {
        val all = repository.getAllFunds()
        // Pre-select 4 popular funds so the user immediately sees rich multi-fund comparison, stock overlap, and PDF readiness
        val initialComparison = all.take(4)
        val initialSummary = repository.getPortfolioSummary(initialComparison)

        _uiState.update {
            it.copy(
                allFunds = all,
                filteredFunds = all,
                inspectedFund = all.firstOrNull(),
                selectedComparisonFunds = initialComparison,
                portfolioSummary = initialSummary
            )
        }
    }

    fun selectTab(tab: AppNavTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun onSearchQueryChanged(query: String) {
        val suggestions = repository.getAutoSuggestions(query)
        val filtered = repository.searchFunds(query).let { list ->
            val cat = _uiState.value.selectedCategory
            if (cat != null) list.filter { it.category == cat } else list
        }

        _uiState.update {
            it.copy(
                searchQuery = query,
                autoSuggestions = suggestions,
                filteredFunds = filtered
            )
        }
    }

    fun selectCategory(category: FundCategory?) {
        val filtered = repository.searchFunds(_uiState.value.searchQuery).let { list ->
            if (category != null) list.filter { it.category == category } else list
        }
        _uiState.update {
            it.copy(
                selectedCategory = category,
                filteredFunds = filtered
            )
        }
    }

    fun inspectFund(fund: MutualFund) {
        _uiState.update { it.copy(inspectedFund = fund) }
    }

    fun toggleComparisonFund(fund: MutualFund) {
        val current = _uiState.value.selectedComparisonFunds.toMutableList()
        val exists = current.any { it.id == fund.id }

        if (exists) {
            current.removeAll { it.id == fund.id }
        } else {
            if (current.size < 6) {
                current.add(fund)
            }
        }

        val summary = repository.getPortfolioSummary(current)
        _uiState.update {
            it.copy(
                selectedComparisonFunds = current,
                portfolioSummary = summary
            )
        }
    }

    fun clearComparisonFunds() {
        val summary = repository.getPortfolioSummary(emptyList())
        _uiState.update {
            it.copy(
                selectedComparisonFunds = emptyList(),
                portfolioSummary = summary
            )
        }
    }

    fun selectPresetPortfolio(funds: List<MutualFund>) {
        val summary = repository.getPortfolioSummary(funds)
        _uiState.update {
            it.copy(
                selectedComparisonFunds = funds,
                portfolioSummary = summary
            )
        }
    }

    fun updateClientName(name: String) {
        _uiState.update { it.copy(clientName = name) }
    }

    fun updateAdvisorName(name: String) {
        _uiState.update { it.copy(advisorName = name) }
    }

    fun generatePdfReport(context: Context, onComplete: ((File) -> Unit)? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isGeneratingPdf = true, pdfStatusMessage = "Compiling report & charts...") }
            try {
                val generator = PdfReportGenerator(context)
                val pdfFile = generator.generateReport(
                    clientName = _uiState.value.clientName,
                    advisorName = _uiState.value.advisorName,
                    selectedFunds = _uiState.value.selectedComparisonFunds.ifEmpty { _uiState.value.allFunds.take(3) },
                    summary = _uiState.value.portfolioSummary
                )
                _uiState.update {
                    it.copy(
                        isGeneratingPdf = false,
                        lastGeneratedPdf = pdfFile,
                        pdfStatusMessage = "Report generated successfully: ${pdfFile.name}"
                    )
                }
                onComplete?.invoke(pdfFile)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isGeneratingPdf = false,
                        pdfStatusMessage = "Error creating PDF: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    // ----------------------------------------------------
    // CALCULATOR EVENTS
    // ----------------------------------------------------
    fun selectCalculator(calc: CalculatorType) {
        _uiState.update { it.copy(activeCalculator = calc) }
    }

    fun updateEducationInputs(
        childAge: Int = _uiState.value.eduChildAge,
        collegeAge: Int = _uiState.value.eduCollegeAge,
        currentCost: Double = _uiState.value.eduCurrentCost,
        inflation: Double = _uiState.value.eduInflation,
        expectedReturn: Double = _uiState.value.eduReturn
    ) {
        val res = repository.calculateChildrenEducation(childAge, collegeAge, currentCost, inflation, expectedReturn)
        _uiState.update {
            it.copy(
                eduChildAge = childAge,
                eduCollegeAge = collegeAge,
                eduCurrentCost = currentCost,
                eduInflation = inflation,
                eduReturn = expectedReturn,
                eduResult = res
            )
        }
    }

    fun updateRetirementInputs(
        currentAge: Int = _uiState.value.retCurrentAge,
        retireAge: Int = _uiState.value.retRetireAge,
        lifeExpAge: Int = _uiState.value.retLifeExpAge,
        monthlyExp: Double = _uiState.value.retMonthlyExpense,
        inflation: Double = _uiState.value.retInflation,
        postRetReturn: Double = _uiState.value.retPostReturn,
        preRetReturn: Double = _uiState.value.retPreReturn
    ) {
        val res = repository.calculateRetirement(
            currentAge, retireAge, lifeExpAge, monthlyExp, inflation, postRetReturn, preRetReturn
        )
        _uiState.update {
            it.copy(
                retCurrentAge = currentAge,
                retRetireAge = retireAge,
                retLifeExpAge = lifeExpAge,
                retMonthlyExpense = monthlyExp,
                retInflation = inflation,
                retPostReturn = postRetReturn,
                retPreReturn = preRetReturn,
                retResult = res
            )
        }
    }

    fun updateFvInputs(
        principal: Double = _uiState.value.fvPrincipal,
        years: Int = _uiState.value.fvYears,
        rate: Double = _uiState.value.fvReturn
    ) {
        val res = repository.calculateFutureValueOfFund(principal, years, rate)
        _uiState.update {
            it.copy(
                fvPrincipal = principal,
                fvYears = years,
                fvReturn = rate,
                fvResult = res
            )
        }
    }

    fun updateInflationInputs(
        principal: Double = _uiState.value.infPrincipal,
        years: Int = _uiState.value.infYears,
        inflation: Double = _uiState.value.infInflationRate,
        expectedReturn: Double = _uiState.value.infReturnRate
    ) {
        val res = repository.calculateFutureValueWithInflation(principal, years, inflation, expectedReturn)
        _uiState.update {
            it.copy(
                infPrincipal = principal,
                infYears = years,
                infInflationRate = inflation,
                infReturnRate = expectedReturn,
                infResult = res
            )
        }
    }

    fun updateSipInputs(
        monthly: Double = _uiState.value.sipAmount,
        years: Int = _uiState.value.sipYears,
        rate: Double = _uiState.value.sipExpectedReturn,
        stepUp: Double = _uiState.value.sipStepUpPercent
    ) {
        val res = repository.calculateSip(monthly, years, rate, stepUp)
        _uiState.update {
            it.copy(
                sipAmount = monthly,
                sipYears = years,
                sipExpectedReturn = rate,
                sipStepUpPercent = stepUp,
                sipResult = res
            )
        }
    }

    fun updateRebalanceAsset(index: Int, newAmount: Double, newTargetPercent: Double) {
        val list = _uiState.value.rebalanceAssets.toMutableList()
        if (index in list.indices) {
            val old = list[index]
            list[index] = old.copy(currentAmount = newAmount, targetPercent = newTargetPercent)
            val rep = repository.calculateTaxOptimizedRebalance(list)
            _uiState.update {
                it.copy(
                    rebalanceAssets = list,
                    rebalanceReport = rep
                )
            }
        }
    }

    private fun recalculateAllCalculators() {
        val s = _uiState.value
        val edu = repository.calculateChildrenEducation(s.eduChildAge, s.eduCollegeAge, s.eduCurrentCost, s.eduInflation, s.eduReturn)
        val ret = repository.calculateRetirement(s.retCurrentAge, s.retRetireAge, s.retLifeExpAge, s.retMonthlyExpense, s.retInflation, s.retPostReturn, s.retPreReturn)
        val fv = repository.calculateFutureValueOfFund(s.fvPrincipal, s.fvYears, s.fvReturn)
        val inf = repository.calculateFutureValueWithInflation(s.infPrincipal, s.infYears, s.infInflationRate, s.infReturnRate)
        val sip = repository.calculateSip(s.sipAmount, s.sipYears, s.sipExpectedReturn, s.sipStepUpPercent)
        val reb = repository.calculateTaxOptimizedRebalance(s.rebalanceAssets)

        _uiState.update {
            it.copy(
                eduResult = edu,
                retResult = ret,
                fvResult = fv,
                infResult = inf,
                sipResult = sip,
                rebalanceReport = reb
            )
        }
    }
}
