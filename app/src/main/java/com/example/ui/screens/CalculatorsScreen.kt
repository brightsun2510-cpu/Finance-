package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.model.*
import com.example.data.repository.toCurrencyString
import com.example.ui.CalculatorType
import com.example.ui.FundUiState
import com.example.ui.FundViewModel
import com.example.ui.theme.*

@Composable
fun CalculatorsScreen(
    viewModel: FundViewModel,
    uiState: FundUiState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Calculator Type Selector
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            val scrollState = rememberScrollState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalculatorType.values().forEach { type ->
                    val isSelected = uiState.activeCalculator == type
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectCalculator(type) },
                        label = {
                            Text(
                                text = type.title,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricBlue,
                            selectedLabelColor = Color.White,
                            containerColor = Slate100,
                            labelColor = Slate700
                        )
                    )
                }
            }
        }

        // Calculator Body
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (uiState.activeCalculator) {
                CalculatorType.SIP -> {
                    item { SipCalculatorSection(viewModel, uiState) }
                }
                CalculatorType.EDUCATION -> {
                    item { ChildrenEducationSection(viewModel, uiState) }
                }
                CalculatorType.RETIREMENT -> {
                    item { RetirementSection(viewModel, uiState) }
                }
                CalculatorType.FUTURE_VALUE -> {
                    item { FutureValueSection(viewModel, uiState) }
                }
                CalculatorType.INFLATION -> {
                    item { InflationPurchasingSection(viewModel, uiState) }
                }
                CalculatorType.TAX_REBALANCE -> {
                    item { TaxRebalancingSection(viewModel, uiState) }
                }
            }
        }
    }
}

// ---------------------------------------------------------
// 1. SIP CALCULATOR WITH STEP-UP
// ---------------------------------------------------------
@Composable
private fun SipCalculatorSection(viewModel: FundViewModel, uiState: FundUiState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("sip_calculator_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Systematic Investment Plan (SIP) Calculator",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Slate900
            )
            Text(
                text = "Calculate wealth creation with annual Step-Up SIP",
                style = MaterialTheme.typography.bodySmall,
                color = Slate500
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Inputs
            CalcSliderRow(
                label = "Monthly SIP Amount",
                valueText = "₹${uiState.sipAmount.toCurrencyString()}",
                sliderValue = uiState.sipAmount.toFloat(),
                valueRange = 1000f..200000f,
                onValueChange = { viewModel.updateSipInputs(monthly = it.toDouble()) }
            )

            CalcSliderRow(
                label = "Investment Tenure",
                valueText = "${uiState.sipYears} Years",
                sliderValue = uiState.sipYears.toFloat(),
                valueRange = 1f..35f,
                onValueChange = { viewModel.updateSipInputs(years = it.toInt()) }
            )

            CalcSliderRow(
                label = "Expected Annual Return (CAGR)",
                valueText = "${uiState.sipExpectedReturn}%",
                sliderValue = uiState.sipExpectedReturn.toFloat(),
                valueRange = 6f..25f,
                onValueChange = { viewModel.updateSipInputs(rate = (it * 10).toInt() / 10.0) }
            )

            CalcSliderRow(
                label = "Annual Step-Up (% Increase / Year)",
                valueText = "${uiState.sipStepUpPercent}%",
                sliderValue = uiState.sipStepUpPercent.toFloat(),
                valueRange = 0f..25f,
                onValueChange = { viewModel.updateSipInputs(stepUp = (it * 10).toInt() / 10.0) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Results
            uiState.sipResult?.let { res ->
                ResultHighlightCard(
                    primaryTitle = "TOTAL MATURITY VALUE",
                    primaryValue = "₹${res.totalMaturityAmount.toCurrencyString()}",
                    metrics = listOf(
                        Pair("Total Invested", "₹${res.totalInvested.toCurrencyString()}"),
                        Pair("Wealth Gain", "₹${res.totalReturns.toCurrencyString()}"),
                        Pair("Gain %", "+${res.growthPercentage}%")
                    )
                )
            }
        }
    }
}

// ---------------------------------------------------------
// 2. CHILDREN EDUCATION CALCULATOR
// ---------------------------------------------------------
@Composable
private fun ChildrenEducationSection(viewModel: FundViewModel, uiState: FundUiState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("education_calculator_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Children Higher Education Corpus Calculator",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Slate900
            )
            Text(
                text = "Plan college funds with education inflation (typically 8-10%)",
                style = MaterialTheme.typography.bodySmall,
                color = Slate500
            )

            Spacer(modifier = Modifier.height(16.dp))

            CalcSliderRow(
                label = "Child's Current Age",
                valueText = "${uiState.eduChildAge} Years",
                sliderValue = uiState.eduChildAge.toFloat(),
                valueRange = 0f..17f,
                onValueChange = { viewModel.updateEducationInputs(childAge = it.toInt()) }
            )

            CalcSliderRow(
                label = "College Start Age",
                valueText = "${uiState.eduCollegeAge} Years",
                sliderValue = uiState.eduCollegeAge.toFloat(),
                valueRange = 18f..25f,
                onValueChange = { viewModel.updateEducationInputs(collegeAge = it.toInt()) }
            )

            CalcSliderRow(
                label = "Today's Course Cost",
                valueText = "₹${uiState.eduCurrentCost.toCurrencyString()}",
                sliderValue = uiState.eduCurrentCost.toFloat(),
                valueRange = 500000f..10000000f,
                onValueChange = { viewModel.updateEducationInputs(currentCost = it.toDouble()) }
            )

            CalcSliderRow(
                label = "Education Inflation Rate",
                valueText = "${uiState.eduInflation}%",
                sliderValue = uiState.eduInflation.toFloat(),
                valueRange = 4f..15f,
                onValueChange = { viewModel.updateEducationInputs(inflation = (it * 10).toInt() / 10.0) }
            )

            CalcSliderRow(
                label = "Expected Mutual Fund Return",
                valueText = "${uiState.eduReturn}%",
                sliderValue = uiState.eduReturn.toFloat(),
                valueRange = 8f..18f,
                onValueChange = { viewModel.updateEducationInputs(expectedReturn = (it * 10).toInt() / 10.0) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            uiState.eduResult?.let { res ->
                ResultHighlightCard(
                    primaryTitle = "REQUIRED MONTHLY SIP",
                    primaryValue = "₹${res.monthlySipRequired.toCurrencyString()} / mo",
                    metrics = listOf(
                        Pair("Years to College", "${res.yearsRemaining} Years"),
                        Pair("Future College Cost", "₹${res.futureCost.toCurrencyString()}"),
                        Pair("Estimated Gains", "₹${res.estimatedGains.toCurrencyString()}")
                    ),
                    highlightColor = SapphireBlue
                )
            }
        }
    }
}

// ---------------------------------------------------------
// 3. RETIREMENT PLANNING CALCULATOR
// ---------------------------------------------------------
@Composable
private fun RetirementSection(viewModel: FundViewModel, uiState: FundUiState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("retirement_calculator_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Retirement Freedom Calculator",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Slate900
            )
            Text(
                text = "Inflation-adjusted post-retirement corpus & required monthly SIP",
                style = MaterialTheme.typography.bodySmall,
                color = Slate500
            )

            Spacer(modifier = Modifier.height(16.dp))

            CalcSliderRow(
                label = "Current Age",
                valueText = "${uiState.retCurrentAge} Yrs",
                sliderValue = uiState.retCurrentAge.toFloat(),
                valueRange = 20f..55f,
                onValueChange = { viewModel.updateRetirementInputs(currentAge = it.toInt()) }
            )

            CalcSliderRow(
                label = "Desired Retirement Age",
                valueText = "${uiState.retRetireAge} Yrs",
                sliderValue = uiState.retRetireAge.toFloat(),
                valueRange = 45f..70f,
                onValueChange = { viewModel.updateRetirementInputs(retireAge = it.toInt()) }
            )

            CalcSliderRow(
                label = "Current Monthly Expenses",
                valueText = "₹${uiState.retMonthlyExpense.toCurrencyString()}",
                sliderValue = uiState.retMonthlyExpense.toFloat(),
                valueRange = 20000f..300000f,
                onValueChange = { viewModel.updateRetirementInputs(monthlyExp = it.toDouble()) }
            )

            CalcSliderRow(
                label = "Expected Inflation Rate",
                valueText = "${uiState.retInflation}%",
                sliderValue = uiState.retInflation.toFloat(),
                valueRange = 4f..10f,
                onValueChange = { viewModel.updateRetirementInputs(inflation = (it * 10).toInt() / 10.0) }
            )

            CalcSliderRow(
                label = "Pre-Retirement Fund Return",
                valueText = "${uiState.retPreReturn}%",
                sliderValue = uiState.retPreReturn.toFloat(),
                valueRange = 8f..18f,
                onValueChange = { viewModel.updateRetirementInputs(preRetReturn = (it * 10).toInt() / 10.0) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            uiState.retResult?.let { res ->
                ResultHighlightCard(
                    primaryTitle = "TARGET RETIREMENT CORPUS",
                    primaryValue = "₹${res.requiredCorpus.toCurrencyString()}",
                    metrics = listOf(
                        Pair("Monthly SIP Required", "₹${res.monthlySipRequired.toCurrencyString()}"),
                        Pair("Expense at Retirement", "₹${res.monthlyExpenseAtRetirement.toCurrencyString()} /mo"),
                        Pair("Years to Retire", "${res.yearsToRetire} Years")
                    ),
                    highlightColor = EmeraldGrowth
                )
            }
        }
    }
}

// ---------------------------------------------------------
// 4. FUTURE VALUE OF FUND (LUMP SUM)
// ---------------------------------------------------------
@Composable
private fun FutureValueSection(viewModel: FundViewModel, uiState: FundUiState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("future_value_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Future Value of Mutual Fund (Lump Sum)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Slate900
            )
            Text(
                text = "One-time investment compounding growth calculator",
                style = MaterialTheme.typography.bodySmall,
                color = Slate500
            )

            Spacer(modifier = Modifier.height(16.dp))

            CalcSliderRow(
                label = "Lump Sum Investment Amount",
                valueText = "₹${uiState.fvPrincipal.toCurrencyString()}",
                sliderValue = uiState.fvPrincipal.toFloat(),
                valueRange = 10000f..5000000f,
                onValueChange = { viewModel.updateFvInputs(principal = it.toDouble()) }
            )

            CalcSliderRow(
                label = "Duration (Years)",
                valueText = "${uiState.fvYears} Years",
                sliderValue = uiState.fvYears.toFloat(),
                valueRange = 1f..30f,
                onValueChange = { viewModel.updateFvInputs(years = it.toInt()) }
            )

            CalcSliderRow(
                label = "Expected Return Rate (CAGR)",
                valueText = "${uiState.fvReturn}%",
                sliderValue = uiState.fvReturn.toFloat(),
                valueRange = 6f..25f,
                onValueChange = { viewModel.updateFvInputs(rate = (it * 10).toInt() / 10.0) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            uiState.fvResult?.let { res ->
                ResultHighlightCard(
                    primaryTitle = "ESTIMATED FUTURE VALUE",
                    primaryValue = "₹${res.futureValue.toCurrencyString()}",
                    metrics = listOf(
                        Pair("Invested Amount", "₹${res.presentValue.toCurrencyString()}"),
                        Pair("Wealth Gained", "₹${res.totalGains.toCurrencyString()}"),
                        Pair("Growth Multiplier", "${res.growthMultiplier}x")
                    ),
                    highlightColor = AmberGold
                )
            }
        }
    }
}

// ---------------------------------------------------------
// 5. INFLATION & PURCHASING POWER CALCULATOR
// ---------------------------------------------------------
@Composable
private fun InflationPurchasingSection(viewModel: FundViewModel, uiState: FundUiState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("inflation_calculator_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Future Value of Money with Inflation",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Slate900
            )
            Text(
                text = "See how inflation erodes cash vs how mutual fund investments beat inflation",
                style = MaterialTheme.typography.bodySmall,
                color = Slate500
            )

            Spacer(modifier = Modifier.height(16.dp))

            CalcSliderRow(
                label = "Today's Amount",
                valueText = "₹${uiState.infPrincipal.toCurrencyString()}",
                sliderValue = uiState.infPrincipal.toFloat(),
                valueRange = 50000f..5000000f,
                onValueChange = { viewModel.updateInflationInputs(principal = it.toDouble()) }
            )

            CalcSliderRow(
                label = "Time Horizon (Years)",
                valueText = "${uiState.infYears} Years",
                sliderValue = uiState.infYears.toFloat(),
                valueRange = 1f..30f,
                onValueChange = { viewModel.updateInflationInputs(years = it.toInt()) }
            )

            CalcSliderRow(
                label = "Inflation Rate (%)",
                valueText = "${uiState.infInflationRate}%",
                sliderValue = uiState.infInflationRate.toFloat(),
                valueRange = 3f..12f,
                onValueChange = { viewModel.updateInflationInputs(inflation = (it * 10).toInt() / 10.0) }
            )

            CalcSliderRow(
                label = "Mutual Fund Return Rate (%)",
                valueText = "${uiState.infReturnRate}%",
                sliderValue = uiState.infReturnRate.toFloat(),
                valueRange = 6f..20f,
                onValueChange = { viewModel.updateInflationInputs(expectedReturn = (it * 10).toInt() / 10.0) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            uiState.infResult?.let { res ->
                ResultHighlightCard(
                    primaryTitle = "IF INVESTED IN MUTUAL FUNDS",
                    primaryValue = "₹${res.futureNominalValue.toCurrencyString()}",
                    metrics = listOf(
                        Pair("Cost of Same Goods in Future", "₹${res.futureCostOfSameGoods.toCurrencyString()}"),
                        Pair("Real Purchasing Power", "₹${res.realPurchasingPower.toCurrencyString()}"),
                        Pair("Cash Value Lost if Not Invested", "-${res.purchasingPowerLossPercent}%")
                    ),
                    highlightColor = EmeraldGrowth
                )
            }
        }
    }
}

// ---------------------------------------------------------
// 6. PORTFOLIO REBALANCING FOR TAX OPTIMIZATION
// ---------------------------------------------------------
@Composable
private fun TaxRebalancingSection(viewModel: FundViewModel, uiState: FundUiState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("tax_rebalancing_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Tax-Optimized Portfolio Rebalancing Tool",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Slate900
            )
            Text(
                text = "Rebalance asset weights while legally minimizing LTCG & STCG tax liabilities",
                style = MaterialTheme.typography.bodySmall,
                color = Slate500
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Assets table
            Text(
                text = "Current Portfolio Assets vs Target Allocation:",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Slate700
            )
            Spacer(modifier = Modifier.height(8.dp))

            uiState.rebalanceAssets.forEachIndexed { index, asset ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Slate100,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = asset.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                            Text(
                                text = "Current: ₹${asset.currentAmount.toCurrencyString()} (Target: ${asset.targetPercent}%)",
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate600
                            )
                        }

                        // Quick target selector
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TextButton(
                                onClick = {
                                    val newTgt = (asset.targetPercent - 5.0).coerceAtLeast(5.0)
                                    viewModel.updateRebalanceAsset(index, asset.currentAmount, newTgt)
                                }
                            ) { Text("-5%") }
                            TextButton(
                                onClick = {
                                    val newTgt = (asset.targetPercent + 5.0).coerceAtMost(80.0)
                                    viewModel.updateRebalanceAsset(index, asset.currentAmount, newTgt)
                                }
                            ) { Text("+5%") }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Rebalance Report & Tax Analysis
            uiState.rebalanceReport?.let { rep ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NavyPrimary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "PORTFOLIO VALUE: ₹${rep.totalPortfolioValue.toCurrencyString()}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Slate300
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Annual LTCG Exemption Used",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Slate300
                                )
                                Text(
                                    text = "₹${rep.ltcgExemptionUsed.toCurrencyString()} / ₹1,25,000",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldGrowth
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Estimated Tax if Sold Today",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Slate300
                                )
                                Text(
                                    text = "₹${rep.totalTaxLiability.toCurrencyString()}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (rep.totalTaxLiability > 0) CrimsonRisk else EmeraldGrowth
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = Slate600, thickness = 0.5.dp)
                        Spacer(modifier = Modifier.height(10.dp))

                        // Smart Tax Optimization Strategy
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = AmberGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = rep.smartTaxFreeStrategy,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White,
                                lineHeight = MaterialTheme.typography.bodySmall.lineHeight
                            )
                        }
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------
// REUSABLE CALCULATOR COMPONENTS
// ---------------------------------------------------------
@Composable
private fun CalcSliderRow(
    label: String,
    valueText: String,
    sliderValue: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = Slate700
            )
            Text(
                text = valueText,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Slate900
            )
        }
        Slider(
            value = sliderValue.coerceIn(valueRange.start, valueRange.endInclusive),
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = SapphireBlue,
                activeTrackColor = SapphireBlue,
                inactiveTrackColor = Slate200
            )
        )
    }
}

@Composable
private fun ResultHighlightCard(
    primaryTitle: String,
    primaryValue: String,
    metrics: List<Pair<String, String>>,
    highlightColor: Color = EmeraldGrowth
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = NavyPrimary,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Text(
                text = primaryTitle,
                style = MaterialTheme.typography.labelSmall,
                color = Slate300
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = primaryValue,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = highlightColor
            )

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = Slate600, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                metrics.forEach { (label, value) ->
                    Column {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = Slate300
                        )
                        Text(
                            text = value,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
