package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MutualFund
import com.example.ui.theme.*

private val chartCurveColors = listOf(
    ElectricBlue,
    EmeraldGrowth,
    AmberGold,
    Color(0xFF8B5CF6), // Purple
    Color(0xFFEC4899), // Pink
    Color(0xFF06B6D4)  // Cyan
)

@Composable
fun GrowthChartComposable(
    funds: List<MutualFund>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("growth_chart_card"),
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "5-Year Growth Performance",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Historical trajectory indexed from ₹10,000 base",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "2021 – 2026",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (funds.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Select funds to view comparative growth trajectory",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                // Interactive Canvas Chart
                val baseMin = 10000f
                val baseMax = 40000f

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .testTag("historical_growth_canvas")
                ) {
                    val width = size.width
                    val height = size.height

                    val leftPadding = 80f
                    val rightPadding = 20f
                    val topPadding = 20f
                    val bottomPadding = 45f

                    val chartWidth = width - leftPadding - rightPadding
                    val chartHeight = height - topPadding - bottomPadding

                    // Draw horizontal grid lines (₹10k, ₹20k, ₹30k, ₹40k)
                    val gridSteps = 3
                    val paint = android.graphics.Paint().apply {
                        color = android.graphics.Color.GRAY
                        textSize = 28f
                        isAntiAlias = true
                    }

                    for (i in 0..gridSteps) {
                        val fraction = i.toFloat() / gridSteps.toFloat()
                        val y = topPadding + chartHeight * (1f - fraction)
                        val valueK = ((baseMin + (baseMax - baseMin) * fraction) / 1000).toInt()

                        // Grid line
                        drawLine(
                            color = Slate200.copy(alpha = 0.6f),
                            start = Offset(leftPadding, y),
                            end = Offset(width - rightPadding, y),
                            strokeWidth = 1.5f
                        )

                        // Y-axis label
                        drawContext.canvas.nativeCanvas.drawText(
                            "₹${valueK}k",
                            10f,
                            y + 10f,
                            paint
                        )
                    }

                    // Years on X-axis
                    val years = listOf("2021", "2022", "2023", "2024", "2025", "2026")
                    val xStep = chartWidth / (years.size - 1)

                    years.forEachIndexed { index, year ->
                        val x = leftPadding + index * xStep
                        drawContext.canvas.nativeCanvas.drawText(
                            year,
                            x - 25f,
                            height - 10f,
                            paint
                        )
                    }

                    // Draw curves for each fund
                    funds.take(5).forEachIndexed { fundIdx, fund ->
                        val curveColor = chartCurveColors[fundIdx % chartCurveColors.size]
                        val history = fund.navHistory5Y

                        if (history.size >= 2) {
                            val path = Path()
                            history.forEachIndexed { ptIdx, pt ->
                                val x = leftPadding + ptIdx * xStep
                                val normalizedY = ((pt.value.toFloat() - baseMin) / (baseMax - baseMin)).coerceIn(0f, 1f)
                                val y = topPadding + chartHeight * (1f - normalizedY)

                                if (ptIdx == 0) {
                                    path.moveTo(x, y)
                                } else {
                                    // Smooth bezier curve
                                    val prevPt = history[ptIdx - 1]
                                    val prevX = leftPadding + (ptIdx - 1) * xStep
                                    val prevNormY = ((prevPt.value.toFloat() - baseMin) / (baseMax - baseMin)).coerceIn(0f, 1f)
                                    val prevY = topPadding + chartHeight * (1f - prevNormY)

                                    val cx = (prevX + x) / 2f
                                    path.cubicTo(cx, prevY, cx, y, x, y)
                                }

                                // Draw dot at each point
                                drawCircle(
                                    color = curveColor,
                                    radius = 4f,
                                    center = Offset(x, y)
                                )
                            }

                            drawPath(
                                path = path,
                                color = curveColor,
                                style = Stroke(width = 3.5f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Chart Legend
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    funds.take(5).chunked(2).forEach { rowFunds ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowFunds.forEach { fund ->
                                val index = funds.indexOf(fund)
                                val color = chartCurveColors[index % chartCurveColors.size]
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .background(color, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = fund.name,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                            if (rowFunds.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}
