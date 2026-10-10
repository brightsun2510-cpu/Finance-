package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppNavTab
import com.example.ui.FundViewModel
import com.example.ui.screens.CalculatorsScreen
import com.example.ui.screens.CompareOverlapScreen
import com.example.ui.screens.FundsExplorerScreen
import com.example.ui.screens.PdfReportScreen
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                FundIntelApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FundIntelApp(
    viewModel: FundViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Handle Android system back press to return to home funds tab
    BackHandler(enabled = uiState.currentTab != AppNavTab.FUNDS) {
        viewModel.selectTab(AppNavTab.FUNDS)
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 600.dp

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = NavyPrimary,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = "FundIntel",
                                        tint = AmberGold,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "FundIntel",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Mutual Fund Analytics & Overlap Intelligence",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    actions = {
                        if (uiState.currentTab != AppNavTab.PDF_REPORT) {
                            FilledTonalButton(
                                onClick = { viewModel.selectTab(AppNavTab.PDF_REPORT) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = LightBlueBg,
                                    contentColor = SapphireBlue
                                ),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("top_pdf_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PictureAsPdf,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("PDF", fontWeight = FontWeight.Bold)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            bottomBar = {
                if (!isWideScreen) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 6.dp,
                        modifier = Modifier.testTag("bottom_nav_bar")
                    ) {
                        NavigationBarItem(
                            selected = uiState.currentTab == AppNavTab.FUNDS,
                            onClick = { viewModel.selectTab(AppNavTab.FUNDS) },
                            icon = { Icon(Icons.Default.Search, contentDescription = "Funds") },
                            label = { Text("Funds", style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.testTag("nav_item_funds")
                        )
                        NavigationBarItem(
                            selected = uiState.currentTab == AppNavTab.COMPARE,
                            onClick = { viewModel.selectTab(AppNavTab.COMPARE) },
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if (uiState.selectedComparisonFunds.isNotEmpty()) {
                                            Badge(containerColor = SapphireBlue) {
                                                Text("${uiState.selectedComparisonFunds.size}")
                                            }
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.CompareArrows, contentDescription = "Compare")
                                }
                            },
                            label = { Text("Overlap", style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.testTag("nav_item_compare")
                        )
                        NavigationBarItem(
                            selected = uiState.currentTab == AppNavTab.CALCULATORS,
                            onClick = { viewModel.selectTab(AppNavTab.CALCULATORS) },
                            icon = { Icon(Icons.Default.Calculate, contentDescription = "Calculators") },
                            label = { Text("Calculators", style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.testTag("nav_item_calculators")
                        )
                        NavigationBarItem(
                            selected = uiState.currentTab == AppNavTab.PDF_REPORT,
                            onClick = { viewModel.selectTab(AppNavTab.PDF_REPORT) },
                            icon = { Icon(Icons.Default.PictureAsPdf, contentDescription = "PDF Report") },
                            label = { Text("Client PDF", style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.testTag("nav_item_pdf")
                        )
                    }
                }
            }
        ) { paddingValues ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Wide Screen Navigation Rail (Desktop / Tablet view)
                if (isWideScreen) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        NavigationRailItem(
                            selected = uiState.currentTab == AppNavTab.FUNDS,
                            onClick = { viewModel.selectTab(AppNavTab.FUNDS) },
                            icon = { Icon(Icons.Default.Search, contentDescription = "Funds") },
                            label = { Text("Funds") }
                        )
                        NavigationRailItem(
                            selected = uiState.currentTab == AppNavTab.COMPARE,
                            onClick = { viewModel.selectTab(AppNavTab.COMPARE) },
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if (uiState.selectedComparisonFunds.isNotEmpty()) {
                                            Badge { Text("${uiState.selectedComparisonFunds.size}") }
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.CompareArrows, contentDescription = "Compare")
                                }
                            },
                            label = { Text("Overlap") }
                        )
                        NavigationRailItem(
                            selected = uiState.currentTab == AppNavTab.CALCULATORS,
                            onClick = { viewModel.selectTab(AppNavTab.CALCULATORS) },
                            icon = { Icon(Icons.Default.Calculate, contentDescription = "Calculators") },
                            label = { Text("Calculators") }
                        )
                        NavigationRailItem(
                            selected = uiState.currentTab == AppNavTab.PDF_REPORT,
                            onClick = { viewModel.selectTab(AppNavTab.PDF_REPORT) },
                            icon = { Icon(Icons.Default.PictureAsPdf, contentDescription = "Client PDF") },
                            label = { Text("Client PDF") }
                        )
                    }
                }

                // Main Content Body with max width alignment on wide screens
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 900.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    when (uiState.currentTab) {
                        AppNavTab.FUNDS -> FundsExplorerScreen(viewModel, uiState)
                        AppNavTab.COMPARE -> CompareOverlapScreen(viewModel, uiState)
                        AppNavTab.CALCULATORS -> CalculatorsScreen(viewModel, uiState)
                        AppNavTab.PDF_REPORT -> PdfReportScreen(viewModel, uiState)
                    }
                }
            }
        }
    }
}
