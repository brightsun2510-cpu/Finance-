package com.example.pdf

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintManager
import androidx.core.content.FileProvider
import com.example.data.model.FundCategory
import com.example.data.model.MutualFund
import com.example.data.model.PortfolioSummary
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

class PdfReportGenerator(private val context: Context) {

    // Standard A4 dimensions in PostScript points (72 points per inch)
    private val pageWidth = 595
    private val pageHeight = 842

    fun generateReport(
        clientName: String,
        advisorName: String,
        selectedFunds: List<MutualFund>,
        summary: PortfolioSummary
    ): File {
        val pdfDocument = PdfDocument()
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val currentDate = dateFormat.format(Date())
        val reportId = "FI-${System.currentTimeMillis() % 1000000}"

        // Paint objects
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)

        // ================= PAGE 1: COVER PAGE & EXECUTIVE DASHBOARD =================
        val pageInfo1 = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page1 = pdfDocument.startPage(pageInfo1)
        val canvas1 = page1.canvas

        drawCoverPage(
            canvas = canvas1,
            clientName = clientName.ifBlank { "Valued Investor" },
            advisorName = advisorName.ifBlank { "FundIntel Wealth Advisory" },
            reportDate = currentDate,
            reportId = reportId,
            selectedFunds = selectedFunds,
            summary = summary,
            paint = paint,
            textPaint = textPaint
        )
        pdfDocument.finishPage(page1)

        // ================= PAGE 2: COMPARISON TABLE & 5Y GROWTH CHART =================
        val pageInfo2 = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 2).create()
        val page2 = pdfDocument.startPage(pageInfo2)
        val canvas2 = page2.canvas

        drawComparisonAndChartPage(
            canvas = canvas2,
            selectedFunds = selectedFunds,
            paint = paint,
            textPaint = textPaint
        )
        pdfDocument.finishPage(page2)

        // ================= PAGE 3: STOCK OVERLAP & MARKET CAP OVERLAP =================
        val pageInfo3 = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 3).create()
        val page3 = pdfDocument.startPage(pageInfo3)
        val canvas3 = page3.canvas

        drawStockOverlapPage(
            canvas = canvas3,
            selectedFunds = selectedFunds,
            summary = summary,
            paint = paint,
            textPaint = textPaint
        )
        pdfDocument.finishPage(page3)

        // Write to file
        val reportsDir = File(context.cacheDir, "reports")
        if (!reportsDir.exists()) reportsDir.mkdirs()

        val sanitizedClient = clientName.replace("[^a-zA-Z0-9]".toRegex(), "_").ifBlank { "Portfolio" }
        val pdfFile = File(reportsDir, "FundIntel_${sanitizedClient}_Report.pdf")

        FileOutputStream(pdfFile).use { outputStream ->
            pdfDocument.writeTo(outputStream)
        }
        pdfDocument.close()

        return pdfFile
    }

    private fun drawCoverPage(
        canvas: Canvas,
        clientName: String,
        advisorName: String,
        reportDate: String,
        reportId: String,
        selectedFunds: List<MutualFund>,
        summary: PortfolioSummary,
        paint: Paint,
        textPaint: Paint
    ) {
        // Deep Navy Header Banner
        paint.color = Color.parseColor("#0A2540")
        canvas.drawRect(0f, 0f, pageWidth.toFloat(), 220f, paint)

        // Golden Accent Stripe
        paint.color = Color.parseColor("#F59E0B")
        canvas.drawRect(0f, 220f, pageWidth.toFloat(), 226f, paint)

        // Brand Title
        textPaint.color = Color.WHITE
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.textSize = 24f
        canvas.drawText("FUNDINTEL ANALYTICS", 40f, 65f, textPaint)

        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        textPaint.textSize = 12f
        textPaint.color = Color.parseColor("#94A3B8")
        canvas.drawText("PORTFOLIO INTELLIGENCE & COMPREHENSIVE FUND AUDIT", 40f, 85f, textPaint)

        // Cover Page Title
        textPaint.color = Color.WHITE
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.textSize = 20f
        canvas.drawText("MUTUAL FUND PORTFOLIO ANALYSIS REPORT", 40f, 135f, textPaint)

        // Client Name Highlight in Cover Box
        paint.color = Color.parseColor("#1E3A5F")
        val clientCard = RectF(40f, 150f, pageWidth - 40f, 205f)
        canvas.drawRoundRect(clientCard, 8f, 8f, paint)

        textPaint.color = Color.parseColor("#CBD5E1")
        textPaint.textSize = 10f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("PREPARED EXCLUSIVELY FOR:", 55f, 172f, textPaint)

        textPaint.color = Color.parseColor("#FBBF24")
        textPaint.textSize = 18f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(clientName.uppercase(Locale.getDefault()), 55f, 195f, textPaint)

        // Metadata Subtitle
        textPaint.color = Color.parseColor("#475569")
        textPaint.textSize = 10f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("Report ID: $reportId  |  Valuation Date: $reportDate  |  Prepared By: $advisorName", 40f, 248f, textPaint)

        // Section Title
        textPaint.color = Color.parseColor("#0F172A")
        textPaint.textSize = 15f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("EXECUTIVE PORTFOLIO SUMMARY", 40f, 280f, textPaint)

        // Dashboard Metric Cards (2x2 grid)
        val cardWidth = (pageWidth - 90f) / 2f
        val cardHeight = 65f

        // Metric 1: Funds Analyzed & 5Y Return
        drawMetricCard(
            canvas, 40f, 295f, cardWidth, cardHeight,
            label = "FUNDS ANALYZED",
            value = "${selectedFunds.size} Funds",
            subtext = "Combined Portfolio Set",
            paint, textPaint
        )

        // Metric 2: 5Y Weighted Return
        drawMetricCard(
            canvas, 40f + cardWidth + 10f, 295f, cardWidth, cardHeight,
            label = "WEIGHTED 5Y CAGR RETURN",
            value = "${summary.weightedReturn5Y}%",
            subtext = "3Y CAGR: ${summary.weightedReturn3Y}%",
            paint, textPaint,
            valueColor = Color.parseColor("#059669")
        )

        // Metric 3: Portfolio P/E vs Category P/E
        drawMetricCard(
            canvas, 40f, 370f, cardWidth, cardHeight,
            label = "WEIGHTED P/E RATIO",
            value = "${summary.weightedPeRatio}x",
            subtext = "Category Avg P/E: ${summary.weightedCategoryPe}x",
            paint, textPaint
        )

        // Metric 4: Portfolio Alpha
        drawMetricCard(
            canvas, 40f + cardWidth + 10f, 370f, cardWidth, cardHeight,
            label = "WEIGHTED PORTFOLIO ALPHA",
            value = "+${summary.weightedAlpha}%",
            subtext = "Risk (Std Dev): ${summary.weightedStdDev}%",
            paint, textPaint,
            valueColor = Color.parseColor("#2563EB")
        )

        // Market Cap Allocation Section
        textPaint.color = Color.parseColor("#0F172A")
        textPaint.textSize = 13f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("COMBINED MARKET CAP DIVERSIFICATION", 40f, 465f, textPaint)

        // Draw market cap horizontal bar
        val barY = 480f
        val barHeight = 22f
        val totalBarWidth = pageWidth - 80f

        val largeW = (summary.aggregateLargeCap.toFloat() / 100f) * totalBarWidth
        val midW = (summary.aggregateMidCap.toFloat() / 100f) * totalBarWidth
        val smallW = (summary.aggregateSmallCap.toFloat() / 100f) * totalBarWidth

        paint.color = Color.parseColor("#1D4ED8") // Blue: Large Cap
        canvas.drawRoundRect(RectF(40f, barY, 40f + largeW, barY + barHeight), 4f, 4f, paint)

        paint.color = Color.parseColor("#F59E0B") // Amber: Mid Cap
        canvas.drawRect(40f + largeW, barY, 40f + largeW + midW, barY + barHeight, paint)

        paint.color = Color.parseColor("#10B981") // Green: Small Cap
        canvas.drawRoundRect(RectF(40f + largeW + midW, barY, 40f + totalBarWidth, barY + barHeight), 4f, 4f, paint)

        // Market Cap Legend
        drawLegendItem(canvas, 40f, 520f, Color.parseColor("#1D4ED8"), "Large Cap: ${summary.aggregateLargeCap}%", paint, textPaint)
        drawLegendItem(canvas, 200f, 520f, Color.parseColor("#F59E0B"), "Mid Cap: ${summary.aggregateMidCap}%", paint, textPaint)
        drawLegendItem(canvas, 360f, 520f, Color.parseColor("#10B981"), "Small Cap: ${summary.aggregateSmallCap}%", paint, textPaint)

        // Selected Funds Summary List
        textPaint.color = Color.parseColor("#0F172A")
        textPaint.textSize = 13f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("PORTFOLIO COMPONENT FUNDS", 40f, 555f, textPaint)

        var fundY = 580f
        selectedFunds.take(5).forEachIndexed { index, fund ->
            paint.color = if (index % 2 == 0) Color.parseColor("#F8FAFC") else Color.WHITE
            canvas.drawRect(40f, fundY - 14f, pageWidth - 40f, fundY + 16f, paint)

            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textPaint.textSize = 10.5f
            textPaint.color = Color.parseColor("#1E293B")
            canvas.drawText("${index + 1}. ${fund.name}", 45f, fundY, textPaint)

            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            textPaint.textSize = 9.5f
            textPaint.color = Color.parseColor("#64748B")
            canvas.drawText("${fund.category.displayName} | NAV: Rs.${fund.nav}", 320f, fundY, textPaint)

            textPaint.color = Color.parseColor("#059669")
            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("5Y: ${fund.return5Y}%", 490f, fundY, textPaint)

            fundY += 28f
        }

        // Footer
        drawPageFooter(canvas, 1, 3, reportId, paint, textPaint)
    }

    private fun drawComparisonAndChartPage(
        canvas: Canvas,
        selectedFunds: List<MutualFund>,
        paint: Paint,
        textPaint: Paint
    ) {
        // Page Header
        drawPageHeader(canvas, "FUND COMPARISON & 5-YEAR HISTORICAL GROWTH", paint, textPaint)

        // TABLE SECTION
        textPaint.color = Color.parseColor("#0F172A")
        textPaint.textSize = 13f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("COMPREHENSIVE RETURN & VALUATION METRICS", 40f, 85f, textPaint)

        // Table Header
        val startY = 100f
        val rowHeight = 26f
        paint.color = Color.parseColor("#0A2540")
        canvas.drawRoundRect(RectF(40f, startY, pageWidth - 40f, startY + rowHeight), 4f, 4f, paint)

        textPaint.color = Color.WHITE
        textPaint.textSize = 8.5f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        canvas.drawText("FUND NAME", 48f, startY + 16f, textPaint)
        canvas.drawText("NAV (Rs)", 195f, startY + 16f, textPaint)
        canvas.drawText("3Y RET", 240f, startY + 16f, textPaint)
        canvas.drawText("5Y RET", 285f, startY + 16f, textPaint)
        canvas.drawText("ALPHA", 330f, startY + 16f, textPaint)
        canvas.drawText("PE RATIO", 375f, startY + 16f, textPaint)
        canvas.drawText("CAT PE", 425f, startY + 16f, textPaint)
        canvas.drawText("STD DEV", 470f, startY + 16f, textPaint)
        canvas.drawText("SHARPE", 515f, startY + 16f, textPaint)

        // Table Rows
        var currentY = startY + rowHeight
        selectedFunds.forEachIndexed { i, fund ->
            paint.color = if (i % 2 == 0) Color.parseColor("#F1F5F9") else Color.WHITE
            canvas.drawRect(40f, currentY, pageWidth - 40f, currentY + rowHeight, paint)

            paint.color = Color.parseColor("#CBD5E1")
            paint.strokeWidth = 0.5f
            canvas.drawLine(40f, currentY + rowHeight, pageWidth - 40f, currentY + rowHeight, paint)

            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textPaint.textSize = 8.5f
            textPaint.color = Color.parseColor("#1E293B")
            val fundShortName = if (fund.name.length > 25) fund.name.take(24) + "..." else fund.name
            canvas.drawText(fundShortName, 48f, currentY + 17f, textPaint)

            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText(fund.nav.toString(), 195f, currentY + 17f, textPaint)

            textPaint.color = Color.parseColor("#059669")
            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("${fund.return3Y}%", 240f, currentY + 17f, textPaint)
            canvas.drawText("${fund.return5Y}%", 285f, currentY + 17f, textPaint)

            textPaint.color = Color.parseColor("#2563EB")
            canvas.drawText("+${fund.alpha}%", 330f, currentY + 17f, textPaint)

            textPaint.color = Color.parseColor("#1E293B")
            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText("${fund.peRatio}x", 375f, currentY + 17f, textPaint)
            canvas.drawText("${fund.categoryAvgPe}x", 425f, currentY + 17f, textPaint)

            textPaint.color = Color.parseColor("#DC2626")
            canvas.drawText("${fund.standardDeviation}%", 470f, currentY + 17f, textPaint)

            textPaint.color = Color.parseColor("#0F172A")
            canvas.drawText(fund.sharpeRatio.toString(), 515f, currentY + 17f, textPaint)

            currentY += rowHeight
        }

        // 5-YEAR HISTORICAL GROWTH PERFORMANCE CHART
        val chartTop = currentY + 30f
        textPaint.color = Color.parseColor("#0F172A")
        textPaint.textSize = 13f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("5-YEAR HISTORICAL GROWTH PERFORMANCE (BASE: Rs. 10,000)", 40f, chartTop, textPaint)

        val chartBox = RectF(40f, chartTop + 15f, pageWidth - 40f, chartTop + 245f)
        paint.color = Color.parseColor("#F8FAFC")
        canvas.drawRoundRect(chartBox, 6f, 6f, paint)

        paint.color = Color.parseColor("#E2E8F0")
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        canvas.drawRoundRect(chartBox, 6f, 6f, paint)
        paint.style = Paint.Style.FILL

        // Draw Chart Grid and Vectors
        drawGrowthChart(canvas, chartBox, selectedFunds, paint, textPaint)

        // Footer
        drawPageFooter(canvas, 2, 3, "FUND-COMP", paint, textPaint)
    }

    private fun drawGrowthChart(
        canvas: Canvas,
        bounds: RectF,
        funds: List<MutualFund>,
        paint: Paint,
        textPaint: Paint
    ) {
        val chartLeft = bounds.left + 50f
        val chartRight = bounds.right - 20f
        val chartTop = bounds.top + 25f
        val chartBottom = bounds.bottom - 40f

        // Y-axis gridlines: Rs. 10,000 to Rs. 40,000
        val minY = 10000.0
        val maxY = 40000.0
        val steps = 4

        paint.color = Color.parseColor("#E2E8F0")
        paint.strokeWidth = 0.8f
        textPaint.color = Color.parseColor("#94A3B8")
        textPaint.textSize = 8f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

        for (i in 0..steps) {
            val yVal = minY + (maxY - minY) * (i.toDouble() / steps.toDouble())
            val yPos = chartBottom - ((yVal - minY) / (maxY - minY)).toFloat() * (chartBottom - chartTop)
            canvas.drawLine(chartLeft, yPos, chartRight, yPos, paint)
            canvas.drawText("Rs.${(yVal / 1000).toInt()}k", bounds.left + 10f, yPos + 3f, textPaint)
        }

        // X-axis years
        val years = listOf("2021", "2022", "2023", "2024", "2025", "2026")
        val xStep = (chartRight - chartLeft) / (years.size - 1)
        for (i in years.indices) {
            val xPos = chartLeft + i * xStep
            canvas.drawText(years[i], xPos - 10f, bounds.bottom - 15f, textPaint)
        }

        // Draw Fund curves
        val colors = listOf(
            Color.parseColor("#2563EB"), // Royal Blue
            Color.parseColor("#10B981"), // Emerald Green
            Color.parseColor("#F59E0B"), // Amber
            Color.parseColor("#8B5CF6"), // Purple
            Color.parseColor("#EC4899")  // Pink
        )

        paint.strokeWidth = 2.5f
        paint.style = Paint.Style.STROKE

        funds.take(5).forEachIndexed { index, fund ->
            paint.color = colors[index % colors.size]
            val history = fund.navHistory5Y
            if (history.size >= 2) {
                for (j in 0 until history.size - 1) {
                    val p1 = history[j]
                    val p2 = history[j + 1]

                    val x1 = chartLeft + j * xStep
                    val y1 = chartBottom - ((p1.value - minY) / (maxY - minY)).toFloat() * (chartBottom - chartTop)
                    val x2 = chartLeft + (j + 1) * xStep
                    val y2 = chartBottom - ((p2.value - minY) / (maxY - minY)).toFloat() * (chartBottom - chartTop)

                    canvas.drawLine(x1, y1, x2, y2, paint)
                }
            }
        }
        paint.style = Paint.Style.FILL

        // Legend inside chart
        var legendX = chartLeft
        val legendY = bounds.bottom - 3f
        funds.take(5).forEachIndexed { index, fund ->
            val color = colors[index % colors.size]
            paint.color = color
            canvas.drawCircle(legendX + 4f, legendY - 3f, 3.5f, paint)

            textPaint.color = Color.parseColor("#334155")
            textPaint.textSize = 7.5f
            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            val nameShort = if (fund.name.length > 15) fund.name.take(14) + ".." else fund.name
            canvas.drawText(nameShort, legendX + 11f, legendY, textPaint)
            legendX += 95f
        }
    }

    private fun drawStockOverlapPage(
        canvas: Canvas,
        selectedFunds: List<MutualFund>,
        summary: PortfolioSummary,
        paint: Paint,
        textPaint: Paint
    ) {
        // Page Header
        drawPageHeader(canvas, "STOCK OVERLAP & PORTFOLIO CONCENTRATION", paint, textPaint)

        // SECTION 1: CONSOLIDATED STOCK EXPOSURE
        textPaint.color = Color.parseColor("#0F172A")
        textPaint.textSize = 13f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("CONSOLIDATED STOCK EXPOSURE (ALL FUNDS COMBINED)", 40f, 85f, textPaint)

        textPaint.color = Color.parseColor("#64748B")
        textPaint.textSize = 9f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("Shows your total investment exposure in individual stocks across all selected mutual funds.", 40f, 98f, textPaint)

        // Table Header
        val startY = 110f
        val rowHeight = 24f
        paint.color = Color.parseColor("#0A2540")
        canvas.drawRoundRect(RectF(40f, startY, pageWidth - 40f, startY + rowHeight), 4f, 4f, paint)

        textPaint.color = Color.WHITE
        textPaint.textSize = 8.5f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        canvas.drawText("STOCK HOLDING", 48f, startY + 15f, textPaint)
        canvas.drawText("SECTOR", 230f, startY + 15f, textPaint)
        canvas.drawText("HOLDING FUNDS", 340f, startY + 15f, textPaint)
        canvas.drawText("TOTAL EXPOSURE %", 445f, startY + 15f, textPaint)

        var currentY = startY + rowHeight
        val topStocks = summary.topConsolidatedHoldings.take(8)

        topStocks.forEachIndexed { i, stock ->
            paint.color = if (i % 2 == 0) Color.parseColor("#F8FAFC") else Color.WHITE
            canvas.drawRect(40f, currentY, pageWidth - 40f, currentY + rowHeight, paint)

            paint.color = Color.parseColor("#E2E8F0")
            paint.strokeWidth = 0.5f
            canvas.drawLine(40f, currentY + rowHeight, pageWidth - 40f, currentY + rowHeight, paint)

            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textPaint.textSize = 8.5f
            textPaint.color = Color.parseColor("#1E293B")
            val stockNameStr = if (stock.stockName.length > 28) stock.stockName.take(27) + ".." else stock.stockName
            canvas.drawText(stockNameStr, 48f, currentY + 16f, textPaint)

            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            textPaint.color = Color.parseColor("#64748B")
            canvas.drawText(stock.sector, 230f, currentY + 16f, textPaint)

            textPaint.color = Color.parseColor("#0F172A")
            canvas.drawText("${stock.fundCount} of ${selectedFunds.size} Funds", 340f, currentY + 16f, textPaint)

            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textPaint.color = if (stock.aggregatePercent > 6.0) Color.parseColor("#DC2626") else Color.parseColor("#2563EB")
            canvas.drawText("${stock.aggregatePercent}%", 460f, currentY + 16f, textPaint)

            currentY += rowHeight
        }

        // SECTION 2: PAIRWISE FUND OVERLAP MATRIX
        val overlapSectionY = currentY + 30f
        textPaint.color = Color.parseColor("#0F172A")
        textPaint.textSize = 13f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("MUTUAL FUND PAIRWISE OVERLAP MATRIX", 40f, overlapSectionY, textPaint)

        textPaint.color = Color.parseColor("#64748B")
        textPaint.textSize = 9f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("High portfolio overlap (>35%) indicates duplicate exposure across fund managers.", 40f, overlapSectionY + 13f, textPaint)

        var overlapTableY = overlapSectionY + 25f
        paint.color = Color.parseColor("#1E3A5F")
        canvas.drawRoundRect(RectF(40f, overlapTableY, pageWidth - 40f, overlapTableY + rowHeight), 4f, 4f, paint)

        textPaint.color = Color.WHITE
        textPaint.textSize = 8.5f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("FUND PAIR", 48f, overlapTableY + 15f, textPaint)
        canvas.drawText("COMMON STOCKS", 330f, overlapTableY + 15f, textPaint)
        canvas.drawText("OVERLAP %", 450f, overlapTableY + 15f, textPaint)

        overlapTableY += rowHeight
        val overlaps = summary.pairwiseOverlaps.take(6)

        if (overlaps.isEmpty()) {
            paint.color = Color.parseColor("#F8FAFC")
            canvas.drawRect(40f, overlapTableY, pageWidth - 40f, overlapTableY + rowHeight + 10f, paint)
            textPaint.color = Color.parseColor("#64748B")
            textPaint.textSize = 9f
            canvas.drawText("Please select at least 2 mutual funds to calculate pairwise overlap.", 50f, overlapTableY + 20f, textPaint)
            overlapTableY += rowHeight + 10f
        } else {
            overlaps.forEachIndexed { i, ov ->
                paint.color = if (i % 2 == 0) Color.parseColor("#F8FAFC") else Color.WHITE
                canvas.drawRect(40f, overlapTableY, pageWidth - 40f, overlapTableY + rowHeight, paint)

                paint.color = Color.parseColor("#E2E8F0")
                paint.strokeWidth = 0.5f
                canvas.drawLine(40f, overlapTableY + rowHeight, pageWidth - 40f, overlapTableY + rowHeight, paint)

                textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textPaint.textSize = 8f
                textPaint.color = Color.parseColor("#1E293B")
                val f1Short = if (ov.fund1Name.length > 20) ov.fund1Name.take(19) + ".." else ov.fund1Name
                val f2Short = if (ov.fund2Name.length > 20) ov.fund2Name.take(19) + ".." else ov.fund2Name
                canvas.drawText("$f1Short  vs  $f2Short", 48f, overlapTableY + 15f, textPaint)

                textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                textPaint.color = Color.parseColor("#475569")
                canvas.drawText("${ov.commonStocksCount} common stocks", 330f, overlapTableY + 15f, textPaint)

                textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textPaint.color = if (ov.overlapPercent >= 40.0) Color.parseColor("#DC2626") else Color.parseColor("#059669")
                canvas.drawText("${ov.overlapPercent}%", 460f, overlapTableY + 15f, textPaint)

                overlapTableY += rowHeight
            }
        }

        // ADVISOR RECOMMENDATION CARD
        val notesY = overlapTableY + 20f
        val notesBox = RectF(40f, notesY, pageWidth - 40f, notesY + 95f)
        paint.color = Color.parseColor("#EFF6FF")
        canvas.drawRoundRect(notesBox, 6f, 6f, paint)

        paint.color = Color.parseColor("#3B82F6")
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        canvas.drawRoundRect(notesBox, 6f, 6f, paint)
        paint.style = Paint.Style.FILL

        textPaint.color = Color.parseColor("#1E40AF")
        textPaint.textSize = 10f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("PORTFOLIO INTELLIGENCE ADVISORY NOTES", 55f, notesY + 22f, textPaint)

        textPaint.color = Color.parseColor("#334155")
        textPaint.textSize = 8.5f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("- Rebalancing & Diversification: Ensure overlap between any two equity funds stays below 35%.", 55f, notesY + 40f, textPaint)
        canvas.drawText("- Single Stock Concentration: Combined exposure in any single stock (e.g. ICICI Bank) should ideally be under 10%.", 55f, notesY + 56f, textPaint)
        canvas.drawText("- Tax Harvesting: Utilize the annual Rs. 1,25,000 LTCG exemption to rebalance without incurring tax penalties.", 55f, notesY + 72f, textPaint)
        canvas.drawText("- Disclaimer: Mutual fund investments are subject to market risks. Past returns are not indicative of future performance.", 55f, notesY + 86f, textPaint)

        // Footer
        drawPageFooter(canvas, 3, 3, "OVERLAP", paint, textPaint)
    }

    private fun drawMetricCard(
        canvas: Canvas,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        label: String,
        value: String,
        subtext: String,
        paint: Paint,
        textPaint: Paint,
        valueColor: Int = Color.parseColor("#0F172A")
    ) {
        val rect = RectF(x, y, x + width, y + height)
        paint.color = Color.parseColor("#F8FAFC")
        canvas.drawRoundRect(rect, 6f, 6f, paint)

        paint.color = Color.parseColor("#E2E8F0")
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        canvas.drawRoundRect(rect, 6f, 6f, paint)
        paint.style = Paint.Style.FILL

        textPaint.color = Color.parseColor("#64748B")
        textPaint.textSize = 8f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(label, x + 12f, y + 18f, textPaint)

        textPaint.color = valueColor
        textPaint.textSize = 15f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(value, x + 12f, y + 40f, textPaint)

        textPaint.color = Color.parseColor("#94A3B8")
        textPaint.textSize = 7.5f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText(subtext, x + 12f, y + 54f, textPaint)
    }

    private fun drawLegendItem(
        canvas: Canvas,
        x: Float,
        y: Float,
        color: Int,
        label: String,
        paint: Paint,
        textPaint: Paint
    ) {
        paint.color = color
        canvas.drawRoundRect(RectF(x, y - 9f, x + 12f, y + 3f), 2f, 2f, paint)

        textPaint.color = Color.parseColor("#334155")
        textPaint.textSize = 9f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(label, x + 18f, y, textPaint)
    }

    private fun drawPageHeader(
        canvas: Canvas,
        title: String,
        paint: Paint,
        textPaint: Paint
    ) {
        paint.color = Color.parseColor("#0A2540")
        canvas.drawRect(0f, 0f, pageWidth.toFloat(), 45f, paint)

        paint.color = Color.parseColor("#F59E0B")
        canvas.drawRect(0f, 45f, pageWidth.toFloat(), 48f, paint)

        textPaint.color = Color.WHITE
        textPaint.textSize = 12f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("FUNDINTEL ANALYTICS", 40f, 28f, textPaint)

        textPaint.textSize = 9.5f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        textPaint.color = Color.parseColor("#CBD5E1")
        canvas.drawText(title, 200f, 28f, textPaint)
    }

    private fun drawPageFooter(
        canvas: Canvas,
        pageNum: Int,
        total: Int,
        ref: String,
        paint: Paint,
        textPaint: Paint
    ) {
        paint.color = Color.parseColor("#E2E8F0")
        paint.strokeWidth = 1f
        canvas.drawLine(40f, pageHeight - 35f, pageWidth - 40f, pageHeight - 35f, paint)

        textPaint.color = Color.parseColor("#94A3B8")
        textPaint.textSize = 8f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("FundIntel Confidential | Generated Report Ref: $ref", 40f, pageHeight - 20f, textPaint)

        val pageStr = "Page $pageNum of $total"
        canvas.drawText(pageStr, pageWidth - 90f, pageHeight - 20f, textPaint)
    }

    // Helper to share PDF file
    fun createShareIntent(pdfFile: File): Intent {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            pdfFile
        )
        return Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "FundIntel Portfolio Analytics Report")
            putExtra(Intent.EXTRA_TEXT, "Please find attached your comprehensive Mutual Fund Portfolio Intelligence & Overlap Report.")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    // Helper to view PDF file
    fun createViewIntent(pdfFile: File): Intent {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            pdfFile
        )
        return Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    // Helper to print PDF via Android PrintManager
    fun printPdf(activity: Activity, pdfFile: File) {
        val printManager = activity.getSystemService(Context.PRINT_SERVICE) as? PrintManager ?: return
        val printAdapter = object : PrintDocumentAdapter() {
            override fun onLayout(
                oldAttributes: PrintAttributes?,
                newAttributes: PrintAttributes?,
                cancellationSignal: android.os.CancellationSignal?,
                callback: LayoutResultCallback?,
                extras: android.os.Bundle?
            ) {
                if (cancellationSignal?.isCanceled == true) {
                    callback?.onLayoutCancelled()
                    return
                }
                val info = android.print.PrintDocumentInfo.Builder(pdfFile.name)
                    .setContentType(android.print.PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(3)
                    .build()
                callback?.onLayoutFinished(info, true)
            }

            override fun onWrite(
                pages: Array<out android.print.PageRange>?,
                destination: android.os.ParcelFileDescriptor?,
                cancellationSignal: android.os.CancellationSignal?,
                callback: WriteResultCallback?
            ) {
                try {
                    val input = pdfFile.inputStream()
                    val output = java.io.FileOutputStream(destination?.fileDescriptor)
                    input.copyTo(output)
                    input.close()
                    output.close()
                    callback?.onWriteFinished(arrayOf(android.print.PageRange.ALL_PAGES))
                } catch (e: Exception) {
                    callback?.onWriteFailed(e.message)
                }
            }
        }
        printManager.print("FundIntel_Portfolio_Report", printAdapter, null)
    }
}
