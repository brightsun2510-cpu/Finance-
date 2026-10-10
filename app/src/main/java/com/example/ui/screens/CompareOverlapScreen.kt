package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.MutualFund
import com.example.ui.AppNavTab
import com.example.ui.FundUiState
import com.example.ui.FundViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun CompareOverlapScreen(
    viewModel: FundViewModel,
    uiState: FundUiState,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Portfolio Header & Fund Selector Chips
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("portfolio_selector_card"),
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
                                text = "Selected Comparison Portfolio",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${uiState.selectedComparisonFunds.size} funds active in comparison & overlap model",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = { showAddDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SapphireBlue)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Add Fund",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Selected Fund Chips
                    val scrollState = rememberScrollState()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(scrollState),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        uiState.selectedComparisonFunds.forEach { fund ->
                            InputChip(
                                selected = true,
                                onClick = { },
                                label = {
                                    Text(
                                        text = fund.name,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                trailingIcon = {
                                    IconButton(
                                        onClick = { viewModel.toggleComparisonFund(fund) },
                                        modifier = Modifier.size(18.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove fund",
                                            tint = Slate600
                                        )
                                    }
                                },
                                colors = InputChipDefaults.inputChipColors(
                                    selectedContainerColor = LightBlueBg,
                                    selectedLabelColor = NavyPrimary
                                )
                            )
                        }
                    }
                }
            }
        }

        // PDF Generation Call-To-Action Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("pdf_download_banner"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Download / Print Client PDF Report",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Includes client cover page, metrics table, 5Y chart & stock overlap",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate300
                        )
                    }

                    Button(
                        onClick = { viewModel.selectTab(AppNavTab.PDF_REPORT) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AmberGold)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = null,
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Export PDF",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }
        }

        // Side-by-Side Comparison Table
        item {
            MultiFundComparisonTable(funds = uiState.selectedComparisonFunds)
        }

        // 5-Year Historical Growth Chart
        item {
            GrowthChartComposable(funds = uiState.selectedComparisonFunds)
        }

        // Market Cap Overlap & Diversification Bar
        item {
            MarketCapOverlapCard(summary = uiState.portfolioSummary)
        }

        // Total Combined Stock Exposure (e.g. ICICI Bank, HDFC Bank, etc. total % across all funds)
        item {
            ConsolidatedStockExposureSection(
                stocks = uiState.portfolioSummary.topConsolidatedHoldings,
                totalFundsCount = uiState.selectedComparisonFunds.size
            )
        }

        // Pairwise Mutual Fund Overlap Matrix
        item {
            PairwiseOverlapMatrixCard(overlaps = uiState.portfolioSummary.pairwiseOverlaps)
        }
    }

    // Quick Add Fund Dialog with 3-char Search
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text(
                    text = "Add Fund to Comparison",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                var query by remember { mutableStateOf("") }
                val available = uiState.allFunds.filter { fund ->
                    val notIn = uiState.selectedComparisonFunds.none { it.id == fund.id }
                    if (query.isBlank()) notIn else notIn && fund.name.contains(query, ignoreCase = true)
                }

                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Type fund name to filter...") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 240.dp)
                    ) {
                        items(available) { fund ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = fund.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${fund.category.displayName} • 5Y: ${fund.return5Y}%",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Slate500
                                    )
                                }
                                TextButton(
                                    onClick = {
                                        viewModel.toggleComparisonFund(fund)
                                        showAddDialog = false
                                    }
                                ) {
                                    Text("Add", fontWeight = FontWeight.Bold)
                                }
                            }
                            Divider(color = Slate200, thickness = 0.5.dp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}
