package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pdf.PdfReportGenerator
import com.example.ui.FundUiState
import com.example.ui.FundViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PdfReportScreen(
    viewModel: FundViewModel,
    uiState: FundUiState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val pdfGenerator = remember { PdfReportGenerator(context) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Configuration Card: Client Name & Advisor Details
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("pdf_config_card"),
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
                                text = "Client Report Configuration",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                            Text(
                                text = "Personalize the cover page and header of the generated PDF",
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate500
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = LightBlueBg
                        ) {
                            Text(
                                text = "3-Page PDF",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = SapphireBlue
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Client Name Input
                    OutlinedTextField(
                        value = uiState.clientName,
                        onValueChange = { viewModel.updateClientName(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("client_name_input"),
                        label = { Text("Client Name (Printed on Cover Page)") },
                        placeholder = { Text("e.g. Dr. Rajesh & Sunita Verma") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = SapphireBlue)
                        },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Advisor Name Input
                    OutlinedTextField(
                        value = uiState.advisorName,
                        onValueChange = { viewModel.updateAdvisorName(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("advisor_name_input"),
                        label = { Text("Prepared By (Advisor / Firm Name)") },
                        placeholder = { Text("e.g. FundIntel Wealth Advisory") },
                        leadingIcon = {
                            Icon(Icons.Default.Badge, contentDescription = null, tint = Slate600)
                        },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Generation Action Button
                    Button(
                        onClick = {
                            viewModel.generatePdfReport(context) { file ->
                                Toast.makeText(context, "Report ready: ${file.name}", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("generate_pdf_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        enabled = !uiState.isGeneratingPdf
                    ) {
                        if (uiState.isGeneratingPdf) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Compiling Vector PDF Document...", fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = AmberGold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generate High-Res Client PDF Report", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // PDF Actions Card (View, Share, Print)
        if (uiState.lastGeneratedPdf != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pdf_ready_actions_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldLight),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = EmeraldGrowth,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "PDF Report Generated Successfully",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldGrowth
                                )
                                Text(
                                    text = "${uiState.lastGeneratedPdf.name} (${uiState.lastGeneratedPdf.length() / 1024} KB)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Slate700
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // View / Open
                            Button(
                                onClick = {
                                    try {
                                        val intent = pdfGenerator.createViewIntent(uiState.lastGeneratedPdf)
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "No PDF viewer app found", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SapphireBlue)
                            ) {
                                Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Open PDF", style = MaterialTheme.typography.labelSmall)
                            }

                            // Share via WhatsApp / Mail
                            Button(
                                onClick = {
                                    try {
                                        val intent = pdfGenerator.createShareIntent(uiState.lastGeneratedPdf)
                                        context.startActivity(Intent.createChooser(intent, "Share Client PDF Report"))
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Error sharing: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Share", style = MaterialTheme.typography.labelSmall)
                            }

                            // System Print / Save as PDF
                            Button(
                                onClick = {
                                    if (activity != null) {
                                        pdfGenerator.printPdf(activity, uiState.lastGeneratedPdf)
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Slate800)
                            ) {
                                Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Print", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }

        // Live Interactive Preview of Cover Page
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("cover_page_preview_card"),
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
                        text = "Live Preview: Page 1 Cover Sheet",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    Text(
                        text = "How the cover page renders with the dynamic client name",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate500
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Simulated Cover Page Canvas Card
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = NavyPrimary,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "FUNDINTEL ANALYTICS",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberGold
                                )
                                Text(
                                    text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Slate300
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "PORTFOLIO INTELLIGENCE & AUDIT REPORT",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Highlighted Client Box
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Slate800,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "PREPARED EXCLUSIVELY FOR:",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Slate300
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = uiState.clientName.ifBlank { "Valued Investor" }.uppercase(Locale.getDefault()),
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = AmberGold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Prepared by: ${uiState.advisorName}  •  ${uiState.selectedComparisonFunds.size} Funds Analyzed",
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate300
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Mini preview summary pill
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("5Y CAGR", style = MaterialTheme.typography.labelSmall, color = Slate300)
                                    Text("${uiState.portfolioSummary.weightedReturn5Y}%", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = EmeraldGrowth)
                                }
                                Column {
                                    Text("Portfolio Alpha", style = MaterialTheme.typography.labelSmall, color = Slate300)
                                    Text("+${uiState.portfolioSummary.weightedAlpha}%", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = ElectricBlue)
                                }
                                Column {
                                    Text("Portfolio P/E", style = MaterialTheme.typography.labelSmall, color = Slate300)
                                    Text("${uiState.portfolioSummary.weightedPeRatio}x", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Report Contents Summary Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                        text = "Report Structure (Generated PDF Pages)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    ReportPageBullet(
                        pageNumber = 1,
                        title = "Client Cover Sheet & Executive Dashboard",
                        desc = "Custom client branding, portfolio asset value, weighted 5Y return, portfolio alpha & market cap allocation bar"
                    )
                    ReportPageBullet(
                        pageNumber = 2,
                        title = "Comprehensive Fund Table & 5-Year Growth Performance",
                        desc = "Tabular comparison (NAV, 1Y, 3Y, 5Y, Alpha, PE, Cat PE, Std Dev, Sharpe) & 5-year indexed historical curve chart"
                    )
                    ReportPageBullet(
                        pageNumber = 3,
                        title = "Consolidated Stock Exposure & Pairwise Overlap",
                        desc = "Top combined stock holdings (e.g. ICICI Bank %), pairwise mutual fund overlap matrix, and advisor notes"
                    )
                }
            }
        }
    }
}

@Composable
private fun ReportPageBullet(pageNumber: Int, title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = Slate100,
            modifier = Modifier.size(28.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "$pageNumber",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = SapphireBlue
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Slate900
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = Slate600
            )
        }
    }
}
