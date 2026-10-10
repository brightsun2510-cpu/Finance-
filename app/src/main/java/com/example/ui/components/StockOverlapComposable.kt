package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.PairwiseOverlap
import com.example.data.model.PortfolioStockExposure
import com.example.data.model.PortfolioSummary
import com.example.ui.theme.*

@Composable
fun ConsolidatedStockExposureSection(
    stocks: List<PortfolioStockExposure>,
    totalFundsCount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("consolidated_stock_exposure_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total Combined Stock Exposure",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Total percentage invested in each stock across all your $totalFundsCount funds",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldLight
                ) {
                    Text(
                        text = "Aggregated Weight",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldGrowth
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (stocks.isEmpty()) {
                Text(
                    text = "Add funds to portfolio to see consolidated stock holdings.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Slate500
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    stocks.take(8).forEach { stock ->
                        StockExposureItem(
                            stock = stock,
                            totalFundsCount = totalFundsCount
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StockExposureItem(
    stock: PortfolioStockExposure,
    totalFundsCount: Int
) {
    var isExpanded by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Slate50,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stock.stockName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Slate200
                        ) {
                            Text(
                                text = stock.sector,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = Slate700
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Held across ${stock.fundCount} of $totalFundsCount portfolio funds",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate500
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${stock.aggregatePercent}%",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (stock.aggregatePercent > 7.0) CrimsonRisk else ElectricBlue
                        )
                        Text(
                            text = "of total portfolio",
                            style = MaterialTheme.typography.labelSmall,
                            color = Slate500
                        )
                    }
                    IconButton(
                        onClick = { isExpanded = !isExpanded },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Toggle fund details",
                            tint = Slate500
                        )
                    }
                }
            }

            // Visual Progress Bar for Exposure
            Spacer(modifier = Modifier.height(8.dp))
            val progress = (stock.aggregatePercent / 15.0).coerceIn(0.05, 1.0).toFloat()
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (stock.aggregatePercent > 7.0) AmberGold else SapphireBlue,
                trackColor = Slate200
            )

            // Expanded Breakdown by Fund
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    Text(
                        text = "Contribution Breakdown by Fund:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Slate700
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    stock.fundBreakdown.forEach { share ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "• ${share.fundName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate800,
                                maxLines = 1,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "Holds ${share.individualHoldingPercent}% (Adds ${share.effectiveContributionPercent}% to portfolio)",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = Slate600
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PairwiseOverlapMatrixCard(
    overlaps: List<PairwiseOverlap>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("pairwise_overlap_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Mutual Fund Pairwise Overlap Matrix",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Common stock weights between fund pairs",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AmberLight
                ) {
                    Text(
                        text = "Overlap Alert > 35%",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = AmberGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (overlaps.isEmpty()) {
                Text(
                    text = "Select at least 2 mutual funds to calculate pairwise portfolio overlap.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Slate500
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    overlaps.forEach { overlap ->
                        val isHigh = overlap.overlapPercent >= 35.0
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isHigh) CrimsonLight else Slate50,
                            modifier = Modifier.fillMaxWidth()
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
                                        text = "${overlap.fund1Name}  vs  ${overlap.fund2Name}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isHigh) CrimsonRisk else Slate900
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${overlap.commonStocksCount} common stocks (${overlap.topCommonStocks.take(2).joinToString()})",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Slate600
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (isHigh) {
                                        Icon(
                                            imageVector = Icons.Default.WarningAmber,
                                            contentDescription = "High overlap warning",
                                            tint = CrimsonRisk,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                    Text(
                                        text = "${overlap.overlapPercent}%",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isHigh) CrimsonRisk else EmeraldGrowth
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MarketCapOverlapCard(
    summary: PortfolioSummary,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("market_cap_overlap_card"),
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
                text = "Market Cap Allocation & Overlap",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Combined exposure across Large Cap, Mid Cap & Small Cap categories",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Multi-segment horizontal bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(22.dp)
                    .clip(RoundedCornerShape(6.dp))
            ) {
                val largeWeight = summary.aggregateLargeCap.toFloat().coerceAtLeast(0.1f)
                val midWeight = summary.aggregateMidCap.toFloat().coerceAtLeast(0.1f)
                val smallWeight = summary.aggregateSmallCap.toFloat().coerceAtLeast(0.1f)

                Box(
                    modifier = Modifier
                        .weight(largeWeight)
                        .fillMaxHeight()
                        .background(SapphireBlue)
                )
                Box(
                    modifier = Modifier
                        .weight(midWeight)
                        .fillMaxHeight()
                        .background(AmberGold)
                )
                Box(
                    modifier = Modifier
                        .weight(smallWeight)
                        .fillMaxHeight()
                        .background(EmeraldGrowth)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Legend with exact values
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MarketCapLegendItem(color = SapphireBlue, label = "Large Cap", percent = summary.aggregateLargeCap)
                MarketCapLegendItem(color = AmberGold, label = "Mid Cap", percent = summary.aggregateMidCap)
                MarketCapLegendItem(color = EmeraldGrowth, label = "Small Cap", percent = summary.aggregateSmallCap)
            }
        }
    }
}

@Composable
private fun MarketCapLegendItem(color: Color, label: String, percent: Double) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, RoundedCornerShape(3.dp))
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Slate500
            )
            Text(
                text = "$percent%",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Slate900
            )
        }
    }
}
