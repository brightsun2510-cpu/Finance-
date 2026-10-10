package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.model.MutualFund
import com.example.ui.theme.*

@Composable
fun FundMetricsSummaryCard(
    fund: MutualFund,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("fund_metrics_card_${fund.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Name & Category
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = fund.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${fund.amc} • ${fund.fundManager}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = fund.category.displayName,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Primary Metrics Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricTile(
                    title = "NAV",
                    value = "₹${fund.nav}",
                    subtext = "Current Value",
                    modifier = Modifier.weight(1f)
                )
                MetricTile(
                    title = "3Y Return",
                    value = "${fund.return3Y}%",
                    subtext = "CAGR",
                    valueColor = EmeraldGrowth,
                    modifier = Modifier.weight(1f)
                )
                MetricTile(
                    title = "5Y Return",
                    value = "${fund.return5Y}%",
                    subtext = "CAGR",
                    valueColor = EmeraldGrowth,
                    modifier = Modifier.weight(1f)
                )
                MetricTile(
                    title = "Alpha",
                    value = "+${fund.alpha}%",
                    subtext = "vs Benchmark",
                    valueColor = ElectricBlue,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Valuation & PE Comparison Box
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Slate100
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Fund P/E: ${fund.peRatio}x",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "(Cat Avg: ${fund.categoryAvgPe}x)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Slate600
                            )
                        }
                        val peDiff = fund.peRatio - fund.categoryAvgPe
                        val isDiscount = peDiff < 0
                        Text(
                            text = if (isDiscount)
                                "Valuation Discount: ${"%.1f".format(-peDiff)} pts lower than peer average"
                            else
                                "Valuation Premium: ${"%.1f".format(peDiff)} pts higher than peer average",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isDiscount) EmeraldGrowth else AmberGold,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (fund.peRatio < fund.categoryAvgPe) EmeraldLight else AmberLight
                    ) {
                        Text(
                            text = if (fund.peRatio < fund.categoryAvgPe) "Attractively Valued" else "Growth Premium",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (fund.peRatio < fund.categoryAvgPe) EmeraldGrowth else AmberGold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Secondary Stats: Risk & Efficiency
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SecondaryStat("Std Dev", "${fund.standardDeviation}%")
                SecondaryStat("Beta", "${fund.beta}")
                SecondaryStat("Sharpe", "${fund.sharpeRatio}")
                SecondaryStat("Expense", "${fund.expenseRatio}%")
                SecondaryStat("AUM", "₹${"%,.0f".format(fund.aumCr)} Cr")
            }
        }
    }
}

@Composable
fun MultiFundComparisonTable(
    funds: List<MutualFund>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("multi_fund_comparison_table"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Comparative Fund Metrics Table",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Side-by-side returns, valuation ratios, alpha & risk metrics",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Horizontally Scrollable Table
            val horizontalScrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(horizontalScrollState)
            ) {
                // Table Header Row
                Row(
                    modifier = Modifier
                        .background(NavyPrimary, RoundedCornerShape(8.dp))
                        .padding(vertical = 10.dp, horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TableCell("Fund Name", width = 180.dp, isHeader = true)
                    TableCell("NAV (₹)", width = 80.dp, isHeader = true)
                    TableCell("3Y Ret", width = 75.dp, isHeader = true)
                    TableCell("5Y Ret", width = 75.dp, isHeader = true)
                    TableCell("Alpha", width = 75.dp, isHeader = true)
                    TableCell("P/E", width = 70.dp, isHeader = true)
                    TableCell("Cat P/E", width = 75.dp, isHeader = true)
                    TableCell("Std Dev", width = 75.dp, isHeader = true)
                    TableCell("Sharpe", width = 70.dp, isHeader = true)
                    TableCell("Expense", width = 75.dp, isHeader = true)
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Table Content Rows
                funds.forEachIndexed { index, fund ->
                    val rowBg = if (index % 2 == 0) Slate50 else Color.White
                    Row(
                        modifier = Modifier
                            .background(rowBg, RoundedCornerShape(6.dp))
                            .padding(vertical = 10.dp, horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TableCell(fund.name, width = 180.dp, isBold = true)
                        TableCell("₹${fund.nav}", width = 80.dp)
                        TableCell("${fund.return3Y}%", width = 75.dp, color = EmeraldGrowth, isBold = true)
                        TableCell("${fund.return5Y}%", width = 75.dp, color = EmeraldGrowth, isBold = true)
                        TableCell("+${fund.alpha}%", width = 75.dp, color = ElectricBlue, isBold = true)
                        TableCell("${fund.peRatio}x", width = 70.dp)
                        TableCell("${fund.categoryAvgPe}x", width = 75.dp, color = Slate500)
                        TableCell("${fund.standardDeviation}%", width = 75.dp, color = CrimsonRisk)
                        TableCell("${fund.sharpeRatio}", width = 70.dp)
                        TableCell("${fund.expenseRatio}%", width = 75.dp)
                    }
                    Divider(color = Slate200, thickness = 0.5.dp)
                }
            }
        }
    }
}

@Composable
private fun TableCell(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    isHeader: Boolean = false,
    isBold: Boolean = false,
    color: Color = Color.Unspecified
) {
    val cellColor = when {
        isHeader -> Color.White
        color != Color.Unspecified -> color
        isBold -> Slate900
        else -> Slate700
    }

    Text(
        text = text,
        modifier = Modifier.width(width),
        style = if (isHeader) MaterialTheme.typography.labelMedium else MaterialTheme.typography.bodyMedium,
        fontWeight = if (isHeader || isBold) FontWeight.Bold else FontWeight.Normal,
        color = cellColor,
        textAlign = TextAlign.Start,
        maxLines = 1
    )
}

@Composable
private fun MetricTile(
    title: String,
    value: String,
    subtext: String,
    modifier: Modifier = Modifier,
    valueColor: Color = Slate900
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = Slate100
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = Slate600
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtext,
                style = MaterialTheme.typography.labelSmall,
                color = Slate500
            )
        }
    }
}

@Composable
private fun SecondaryStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Slate500
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = Slate800
        )
    }
}
