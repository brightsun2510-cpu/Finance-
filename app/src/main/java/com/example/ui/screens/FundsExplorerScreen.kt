package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.FundCategory
import com.example.data.model.MutualFund
import com.example.ui.FundUiState
import com.example.ui.FundViewModel
import com.example.ui.components.FundMetricsSummaryCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FundsExplorerScreen(
    viewModel: FundViewModel,
    uiState: FundUiState,
    modifier: Modifier = Modifier
) {
    var showDetailSheet by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Search Header with 3-char Auto-fetch
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("fund_search_input"),
                    placeholder = {
                        Text(
                            text = "Type fund name (auto-fetches in 3 letters)...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search icon",
                            tint = ElectricBlue
                        )
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear search",
                                    tint = Slate500
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = Slate300
                    ),
                    singleLine = true
                )

                // Auto-fetch suggestion popup/chips when >= 3 letters typed
                AnimatedVisibility(visible = uiState.autoSuggestions.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Text(
                            text = "Auto-fetched Matching Funds:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Slate600
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        val scrollState = rememberScrollState()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(scrollState),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            uiState.autoSuggestions.forEach { match ->
                                SuggestionChip(
                                    onClick = {
                                        viewModel.inspectFund(match)
                                        showDetailSheet = true
                                    },
                                    label = {
                                        Text(
                                            text = match.name,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    },
                                    colors = SuggestionChipDefaults.suggestionChipColors(
                                        containerColor = LightBlueBg,
                                        labelColor = SapphireBlue
                                    ),
                                    border = null
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Category Filter Chips
                val categories = listOf(null) + FundCategory.values().toList()
                val chipScrollState = rememberScrollState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(chipScrollState),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = uiState.selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.selectCategory(cat) },
                            label = {
                                Text(
                                    text = cat?.displayName ?: "All Funds",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
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
        }

        // Fund List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Available Funds (${uiState.filteredFunds.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${uiState.selectedComparisonFunds.size} selected for overlap & PDF",
                        style = MaterialTheme.typography.bodySmall,
                        color = ElectricBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            items(uiState.filteredFunds, key = { it.id }) { fund ->
                FundCardItem(
                    fund = fund,
                    isInComparison = uiState.selectedComparisonFunds.any { it.id == fund.id },
                    onCardClick = {
                        viewModel.inspectFund(fund)
                        showDetailSheet = true
                    },
                    onToggleComparison = { viewModel.toggleComparisonFund(fund) }
                )
            }
        }
    }

    // Modal BottomSheet for Deep-Dive Details
    if (showDetailSheet && uiState.inspectedFund != null) {
        ModalBottomSheet(
            onDismissRequest = { showDetailSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .padding(bottom = 32.dp)
            ) {
                FundMetricsSummaryCard(fund = uiState.inspectedFund)

                Spacer(modifier = Modifier.height(16.dp))

                // Top Stock Holdings Section
                Text(
                    text = "Top Stock Portfolio Holdings",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                uiState.inspectedFund.topHoldings.take(8).forEach { holding ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = holding.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                            Text(
                                text = "${holding.ticker} • ${holding.sector}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate500
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = LightBlueBg
                        ) {
                            Text(
                                text = "${holding.weight}%",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = ElectricBlue
                            )
                        }
                    }
                    Divider(color = Slate200, thickness = 0.5.dp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.toggleComparisonFund(uiState.inspectedFund)
                        showDetailSheet = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                ) {
                    val inComp = uiState.selectedComparisonFunds.any { it.id == uiState.inspectedFund.id }
                    Icon(
                        imageVector = if (inComp) Icons.Default.Check else Icons.Default.Add,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (inComp) "Remove from Comparison Set" else "Add to Comparison & Overlap Analysis")
                }
            }
        }
    }
}

@Composable
private fun FundCardItem(
    fund: MutualFund,
    isInComparison: Boolean,
    onCardClick: () -> Unit,
    onToggleComparison: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick)
            .testTag("fund_card_${fund.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = fund.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${fund.category.displayName} • ${fund.amc}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate500
                    )
                }

                IconButton(
                    onClick = onToggleComparison,
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            if (isInComparison) EmeraldGrowth else Slate100,
                            RoundedCornerShape(8.dp)
                        )
                ) {
                    Icon(
                        imageVector = if (isInComparison) Icons.Default.Check else Icons.Default.Add,
                        contentDescription = "Toggle comparison",
                        tint = if (isInComparison) Color.White else Slate700,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Key Metrics Pill Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricSnippet("NAV", "₹${fund.nav}")
                MetricSnippet("P/E Ratio", "${fund.peRatio}x")
                MetricSnippet("Cat P/E", "${fund.categoryAvgPe}x")
                MetricSnippet("3Y Ret", "${fund.return3Y}%", color = EmeraldGrowth)
                MetricSnippet("5Y Ret", "${fund.return5Y}%", color = EmeraldGrowth)
                MetricSnippet("Alpha", "+${fund.alpha}%", color = ElectricBlue)
            }
        }
    }
}

@Composable
private fun MetricSnippet(label: String, value: String, color: Color = Slate900) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Slate500
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
