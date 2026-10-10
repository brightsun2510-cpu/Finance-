package com.example.data.repository

import com.example.data.model.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

class MutualFundRepository {

    private val fundsList: MutableList<MutualFund> = mutableListOf(
        MutualFund(
            id = "ppfas-flexi-cap",
            name = "Parag Parikh Flexi Cap Fund",
            amc = "PPFAS Mutual Fund",
            category = FundCategory.FLEXI_CAP,
            nav = 84.20,
            peRatio = 24.5,
            categoryAvgPe = 26.8,
            return1Y = 24.8,
            return3Y = 20.4,
            return5Y = 22.8,
            alpha = 5.6,
            beta = 0.72,
            standardDeviation = 11.8,
            sharpeRatio = 1.45,
            expenseRatio = 0.65,
            aumCr = 76400.0,
            fundManager = "Rajeev Thakkar",
            ratingStars = 5,
            marketCapAlloc = MarketCapAlloc(largeCap = 65.4, midCap = 18.2, smallCap = 16.4),
            topHoldings = listOf(
                StockHolding("HDFC Bank Ltd.", "HDFCBANK", "Financial Services", 7.8),
                StockHolding("ITC Ltd.", "ITC", "Consumer Defensive", 7.2),
                StockHolding("Bajaj Holdings & Investment", "BAJAJHLDNG", "Financial Services", 6.8),
                StockHolding("ICICI Bank Ltd.", "ICICIBANK", "Financial Services", 6.1),
                StockHolding("Power Grid Corp. of India", "POWERGRID", "Utilities", 5.4),
                StockHolding("Coal India Ltd.", "COALINDIA", "Energy", 4.9),
                StockHolding("Alphabet Inc. (Google)", "GOOGL", "Technology", 4.8),
                StockHolding("Microsoft Corp.", "MSFT", "Technology", 4.2),
                StockHolding("Maruti Suzuki India Ltd.", "MARUTI", "Automobile", 3.6),
                StockHolding("Axis Bank Ltd.", "AXISBANK", "Financial Services", 3.2),
                StockHolding("Larsen & Toubro Ltd.", "LT", "Capital Goods", 2.9),
                StockHolding("Infosys Ltd.", "INFY", "Technology", 2.7)
            ),
            navHistory5Y = listOf(
                NavPoint("2021", 10000.0),
                NavPoint("2022", 12100.0),
                NavPoint("2023", 14600.0),
                NavPoint("2024", 18800.0),
                NavPoint("2025", 23400.0),
                NavPoint("2026", 27950.0)
            )
        ),
        MutualFund(
            id = "hdfc-top-100",
            name = "HDFC Top 100 Fund",
            amc = "HDFC Mutual Fund",
            category = FundCategory.LARGE_CAP,
            nav = 1048.50,
            peRatio = 23.8,
            categoryAvgPe = 24.2,
            return1Y = 22.4,
            return3Y = 21.2,
            return5Y = 19.4,
            alpha = 3.8,
            beta = 0.94,
            standardDeviation = 13.5,
            sharpeRatio = 1.25,
            expenseRatio = 0.98,
            aumCr = 38200.0,
            fundManager = "Rahul Baijal",
            ratingStars = 4,
            marketCapAlloc = MarketCapAlloc(largeCap = 88.5, midCap = 9.2, smallCap = 2.3),
            topHoldings = listOf(
                StockHolding("ICICI Bank Ltd.", "ICICIBANK", "Financial Services", 9.8),
                StockHolding("HDFC Bank Ltd.", "HDFCBANK", "Financial Services", 9.4),
                StockHolding("Reliance Industries Ltd.", "RELIANCE", "Energy", 7.8),
                StockHolding("Infosys Ltd.", "INFY", "Technology", 5.6),
                StockHolding("Larsen & Toubro Ltd.", "LT", "Capital Goods", 5.2),
                StockHolding("Bharti Airtel Ltd.", "BHARTIARTL", "Telecom", 4.8),
                StockHolding("Tata Consultancy Services", "TCS", "Technology", 4.1),
                StockHolding("State Bank of India", "SBIN", "Financial Services", 3.9),
                StockHolding("Axis Bank Ltd.", "AXISBANK", "Financial Services", 3.5),
                StockHolding("NTPC Ltd.", "NTPC", "Utilities", 3.1),
                StockHolding("ITC Ltd.", "ITC", "Consumer Defensive", 2.8),
                StockHolding("Maruti Suzuki India Ltd.", "MARUTI", "Automobile", 2.4)
            ),
            navHistory5Y = listOf(
                NavPoint("2021", 10000.0),
                NavPoint("2022", 11600.0),
                NavPoint("2023", 13800.0),
                NavPoint("2024", 17500.0),
                NavPoint("2025", 21200.0),
                NavPoint("2026", 24280.0)
            )
        ),
        MutualFund(
            id = "icici-pru-bluechip",
            name = "ICICI Prudential Bluechip Fund",
            amc = "ICICI Prudential Mutual Fund",
            category = FundCategory.LARGE_CAP,
            nav = 112.40,
            peRatio = 23.2,
            categoryAvgPe = 24.2,
            return1Y = 21.6,
            return3Y = 19.8,
            return5Y = 18.7,
            alpha = 3.2,
            beta = 0.91,
            standardDeviation = 12.9,
            sharpeRatio = 1.20,
            expenseRatio = 0.92,
            aumCr = 62100.0,
            fundManager = "Anish Tawakley",
            ratingStars = 5,
            marketCapAlloc = MarketCapAlloc(largeCap = 86.2, midCap = 11.4, smallCap = 2.4),
            topHoldings = listOf(
                StockHolding("ICICI Bank Ltd.", "ICICIBANK", "Financial Services", 10.2),
                StockHolding("Reliance Industries Ltd.", "RELIANCE", "Energy", 8.5),
                StockHolding("HDFC Bank Ltd.", "HDFCBANK", "Financial Services", 7.9),
                StockHolding("Larsen & Toubro Ltd.", "LT", "Capital Goods", 5.8),
                StockHolding("Infosys Ltd.", "INFY", "Technology", 5.4),
                StockHolding("Bharti Airtel Ltd.", "BHARTIARTL", "Telecom", 4.6),
                StockHolding("Axis Bank Ltd.", "AXISBANK", "Financial Services", 4.1),
                StockHolding("Maruti Suzuki India Ltd.", "MARUTI", "Automobile", 3.4),
                StockHolding("UltraTech Cement Ltd.", "ULTRACEMCO", "Materials", 3.1),
                StockHolding("Sun Pharmaceutical", "SUNPHARMA", "Healthcare", 2.8),
                StockHolding("State Bank of India", "SBIN", "Financial Services", 2.6),
                StockHolding("Tata Motors Ltd.", "TATAMOTORS", "Automobile", 2.4)
            ),
            navHistory5Y = listOf(
                NavPoint("2021", 10000.0),
                NavPoint("2022", 11500.0),
                NavPoint("2023", 13600.0),
                NavPoint("2024", 17200.0),
                NavPoint("2025", 20600.0),
                NavPoint("2026", 23560.0)
            )
        ),
        MutualFund(
            id = "mirae-asset-large-midcap",
            name = "Mirae Asset Large & Midcap Fund",
            amc = "Mirae Asset Mutual Fund",
            category = FundCategory.LARGE_AND_MID,
            nav = 142.60,
            peRatio = 27.4,
            categoryAvgPe = 28.1,
            return1Y = 25.1,
            return3Y = 19.5,
            return5Y = 20.1,
            alpha = 2.9,
            beta = 0.96,
            standardDeviation = 14.2,
            sharpeRatio = 1.15,
            expenseRatio = 0.74,
            aumCr = 41800.0,
            fundManager = "Neelesh Surana",
            ratingStars = 4,
            marketCapAlloc = MarketCapAlloc(largeCap = 52.4, midCap = 38.6, smallCap = 9.0),
            topHoldings = listOf(
                StockHolding("HDFC Bank Ltd.", "HDFCBANK", "Financial Services", 6.8),
                StockHolding("ICICI Bank Ltd.", "ICICIBANK", "Financial Services", 6.5),
                StockHolding("Reliance Industries Ltd.", "RELIANCE", "Energy", 5.4),
                StockHolding("State Bank of India", "SBIN", "Financial Services", 4.2),
                StockHolding("Larsen & Toubro Ltd.", "LT", "Capital Goods", 3.9),
                StockHolding("Federal Bank Ltd.", "FEDERALBNK", "Financial Services", 3.2),
                StockHolding("Bharat Electronics Ltd.", "BEL", "Capital Goods", 3.1),
                StockHolding("Infosys Ltd.", "INFY", "Technology", 2.9),
                StockHolding("Trent Ltd.", "TRENT", "Consumer Discretionary", 2.8),
                StockHolding("Tata Motors Ltd.", "TATAMOTORS", "Automobile", 2.5),
                StockHolding("Axis Bank Ltd.", "AXISBANK", "Financial Services", 2.3),
                StockHolding("Sun Pharmaceutical", "SUNPHARMA", "Healthcare", 2.1)
            ),
            navHistory5Y = listOf(
                NavPoint("2021", 10000.0),
                NavPoint("2022", 11800.0),
                NavPoint("2023", 14200.0),
                NavPoint("2024", 18500.0),
                NavPoint("2025", 22100.0),
                NavPoint("2026", 24980.0)
            )
        ),
        MutualFund(
            id = "nippon-india-small-cap",
            name = "Nippon India Small Cap Fund",
            amc = "Nippon India Mutual Fund",
            category = FundCategory.SMALL_CAP,
            nav = 182.90,
            peRatio = 29.8,
            categoryAvgPe = 31.4,
            return1Y = 32.5,
            return3Y = 28.6,
            return5Y = 31.2,
            alpha = 8.4,
            beta = 0.88,
            standardDeviation = 16.5,
            sharpeRatio = 1.62,
            expenseRatio = 0.70,
            aumCr = 58900.0,
            fundManager = "Samir Rachh",
            ratingStars = 5,
            marketCapAlloc = MarketCapAlloc(largeCap = 11.2, midCap = 18.4, smallCap = 70.4),
            topHoldings = listOf(
                StockHolding("Tube Investments of India", "TIINDIA", "Automobile", 3.2),
                StockHolding("HDFC Bank Ltd.", "HDFCBANK", "Financial Services", 2.8),
                StockHolding("Apar Industries Ltd.", "APARINDS", "Capital Goods", 2.6),
                StockHolding("KPIT Technologies Ltd.", "KPITTECH", "Technology", 2.4),
                StockHolding("Multi Commodity Exchange", "MCX", "Financial Services", 2.2),
                StockHolding("Karur Vysya Bank Ltd.", "KVB", "Financial Services", 2.1),
                StockHolding("Cummins India Ltd.", "CUMMINSIND", "Capital Goods", 2.0),
                StockHolding("Poonawalla Fincorp Ltd.", "POONAWALLA", "Financial Services", 1.9),
                StockHolding("Voltas Ltd.", "VOLTAS", "Consumer Discretionary", 1.8),
                StockHolding("ICICI Bank Ltd.", "ICICIBANK", "Financial Services", 1.7),
                StockHolding("KEC International Ltd.", "KEC", "Capital Goods", 1.6),
                StockHolding("CreditAccess Grameen", "CREDITACC", "Financial Services", 1.5)
            ),
            navHistory5Y = listOf(
                NavPoint("2021", 10000.0),
                NavPoint("2022", 13200.0),
                NavPoint("2023", 17800.0),
                NavPoint("2024", 25600.0),
                NavPoint("2025", 33400.0),
                NavPoint("2026", 38890.0)
            )
        ),
        MutualFund(
            id = "quant-active-fund",
            name = "Quant Active Fund",
            amc = "Quant Mutual Fund",
            category = FundCategory.FLEXI_CAP,
            nav = 682.10,
            peRatio = 22.1,
            categoryAvgPe = 25.4,
            return1Y = 27.8,
            return3Y = 22.8,
            return5Y = 29.5,
            alpha = 9.1,
            beta = 1.12,
            standardDeviation = 17.8,
            sharpeRatio = 1.48,
            expenseRatio = 0.75,
            aumCr = 12600.0,
            fundManager = "Sandeep Tandon",
            ratingStars = 5,
            marketCapAlloc = MarketCapAlloc(largeCap = 48.0, midCap = 31.5, smallCap = 20.5),
            topHoldings = listOf(
                StockHolding("Reliance Industries Ltd.", "RELIANCE", "Energy", 9.2),
                StockHolding("Jio Financial Services", "JIOFIN", "Financial Services", 6.4),
                StockHolding("Adani Power Ltd.", "ADANIPOWER", "Utilities", 5.8),
                StockHolding("Steel Authority of India", "SAIL", "Metals", 4.8),
                StockHolding("Tata Power Co. Ltd.", "TATAPOWER", "Utilities", 4.5),
                StockHolding("Sun Pharmaceutical", "SUNPHARMA", "Healthcare", 4.2),
                StockHolding("HDFC Bank Ltd.", "HDFCBANK", "Financial Services", 3.8),
                StockHolding("Life Insurance Corp. (LIC)", "LICI", "Financial Services", 3.5),
                StockHolding("ICICI Bank Ltd.", "ICICIBANK", "Financial Services", 3.1),
                StockHolding("Britannia Industries", "BRITANNIA", "Consumer Defensive", 2.9),
                StockHolding("Larsen & Toubro Ltd.", "LT", "Capital Goods", 2.6),
                StockHolding("State Bank of India", "SBIN", "Financial Services", 2.4)
            ),
            navHistory5Y = listOf(
                NavPoint("2021", 10000.0),
                NavPoint("2022", 13600.0),
                NavPoint("2023", 18200.0),
                NavPoint("2024", 26200.0),
                NavPoint("2025", 32800.0),
                NavPoint("2026", 36400.0)
            )
        ),
        MutualFund(
            id = "sbi-small-cap",
            name = "SBI Small Cap Fund",
            amc = "SBI Mutual Fund",
            category = FundCategory.SMALL_CAP,
            nav = 178.40,
            peRatio = 28.5,
            categoryAvgPe = 31.4,
            return1Y = 26.2,
            return3Y = 24.1,
            return5Y = 26.8,
            alpha = 6.2,
            beta = 0.82,
            standardDeviation = 14.9,
            sharpeRatio = 1.42,
            expenseRatio = 0.68,
            aumCr = 31400.0,
            fundManager = "R. Srinivasan",
            ratingStars = 4,
            marketCapAlloc = MarketCapAlloc(largeCap = 7.5, midCap = 16.2, smallCap = 76.3),
            topHoldings = listOf(
                StockHolding("Blue Star Ltd.", "BLUESTARCO", "Consumer Discretionary", 4.1),
                StockHolding("Kalpataru Projects", "KPIL", "Capital Goods", 3.8),
                StockHolding("Carborundum Universal", "CARBORUNIV", "Capital Goods", 3.6),
                StockHolding("Lemon Tree Hotels", "LEMONTREE", "Consumer Services", 3.4),
                StockHolding("Finolex Industries Ltd.", "FINPIPE", "Materials", 3.1),
                StockHolding("Sheela Foam Ltd.", "SFL", "Consumer Discretionary", 2.8),
                StockHolding("Timken India Ltd.", "TIMKEN", "Capital Goods", 2.7),
                StockHolding("HDFC Bank Ltd.", "HDFCBANK", "Financial Services", 2.2),
                StockHolding("Elgi Equipments Ltd.", "ELGIEQUIP", "Capital Goods", 2.1),
                StockHolding("ICICI Bank Ltd.", "ICICIBANK", "Financial Services", 1.5),
                StockHolding("TTK Prestige Ltd.", "TTKPRESTIG", "Consumer Discretionary", 1.4),
                StockHolding("V-Guard Industries Ltd.", "VGUARD", "Consumer Discretionary", 1.3)
            ),
            navHistory5Y = listOf(
                NavPoint("2021", 10000.0),
                NavPoint("2022", 12600.0),
                NavPoint("2023", 16400.0),
                NavPoint("2024", 22900.0),
                NavPoint("2025", 28900.0),
                NavPoint("2026", 32800.0)
            )
        ),
        MutualFund(
            id = "kotak-emerging-equity",
            name = "Kotak Emerging Equity Fund",
            amc = "Kotak Mahindra Mutual Fund",
            category = FundCategory.MID_CAP,
            nav = 128.50,
            peRatio = 32.1,
            categoryAvgPe = 30.8,
            return1Y = 28.4,
            return3Y = 23.4,
            return5Y = 24.7,
            alpha = 4.8,
            beta = 0.89,
            standardDeviation = 15.1,
            sharpeRatio = 1.34,
            expenseRatio = 0.78,
            aumCr = 49200.0,
            fundManager = "Pankaj Tibrewal",
            ratingStars = 5,
            marketCapAlloc = MarketCapAlloc(largeCap = 18.2, midCap = 68.4, smallCap = 13.4),
            topHoldings = listOf(
                StockHolding("Supreme Industries Ltd.", "SUPREMEIND", "Materials", 4.8),
                StockHolding("Schaeffler India Ltd.", "SCHAEFFLER", "Automobile", 4.2),
                StockHolding("Cummins India Ltd.", "CUMMINSIND", "Capital Goods", 4.0),
                StockHolding("Persistent Systems Ltd.", "PERSISTENT", "Technology", 3.8),
                StockHolding("Bharat Forge Ltd.", "BHARATFORG", "Automobile", 3.6),
                StockHolding("Thermax Ltd.", "THERMAX", "Capital Goods", 3.4),
                StockHolding("Solar Industries India", "SOLARINDS", "Materials", 3.2),
                StockHolding("HDFC Bank Ltd.", "HDFCBANK", "Financial Services", 3.0),
                StockHolding("ICICI Bank Ltd.", "ICICIBANK", "Financial Services", 2.8),
                StockHolding("Trent Ltd.", "TRENT", "Consumer Discretionary", 2.6),
                StockHolding("L&T Technology Services", "LTTS", "Technology", 2.4),
                StockHolding("Max Healthcare Institute", "MAXHEALTH", "Healthcare", 2.2)
            ),
            navHistory5Y = listOf(
                NavPoint("2021", 10000.0),
                NavPoint("2022", 12400.0),
                NavPoint("2023", 15800.0),
                NavPoint("2024", 21800.0),
                NavPoint("2025", 27200.0),
                NavPoint("2026", 30190.0)
            )
        ),
        MutualFund(
            id = "uti-nifty-50-index",
            name = "UTI Nifty 50 Index Fund",
            amc = "UTI Mutual Fund",
            category = FundCategory.LARGE_CAP,
            nav = 186.20,
            peRatio = 22.8,
            categoryAvgPe = 22.8,
            return1Y = 18.5,
            return3Y = 16.4,
            return5Y = 17.2,
            alpha = 0.0,
            beta = 1.00,
            standardDeviation = 13.1,
            sharpeRatio = 1.08,
            expenseRatio = 0.18,
            aumCr = 19800.0,
            fundManager = "Sharwan Kumar Goyal",
            ratingStars = 4,
            marketCapAlloc = MarketCapAlloc(largeCap = 99.2, midCap = 0.8, smallCap = 0.0),
            topHoldings = listOf(
                StockHolding("HDFC Bank Ltd.", "HDFCBANK", "Financial Services", 11.5),
                StockHolding("Reliance Industries Ltd.", "RELIANCE", "Energy", 9.8),
                StockHolding("ICICI Bank Ltd.", "ICICIBANK", "Financial Services", 7.9),
                StockHolding("Infosys Ltd.", "INFY", "Technology", 5.9),
                StockHolding("Tata Consultancy Services", "TCS", "Technology", 4.2),
                StockHolding("ITC Ltd.", "ITC", "Consumer Defensive", 4.1),
                StockHolding("Larsen & Toubro Ltd.", "LT", "Capital Goods", 4.0),
                StockHolding("Bharti Airtel Ltd.", "BHARTIARTL", "Telecom", 3.8),
                StockHolding("Axis Bank Ltd.", "AXISBANK", "Financial Services", 3.2),
                StockHolding("State Bank of India", "SBIN", "Financial Services", 3.1),
                StockHolding("Bajaj Finance Ltd.", "BAJFINANCE", "Financial Services", 2.9),
                StockHolding("Maruti Suzuki India Ltd.", "MARUTI", "Automobile", 2.3)
            ),
            navHistory5Y = listOf(
                NavPoint("2021", 10000.0),
                NavPoint("2022", 11400.0),
                NavPoint("2023", 13200.0),
                NavPoint("2024", 16500.0),
                NavPoint("2025", 19800.0),
                NavPoint("2026", 22120.0)
            )
        ),
        MutualFund(
            id = "motilal-oswal-midcap",
            name = "Motilal Oswal Midcap Fund",
            amc = "Motilal Oswal Mutual Fund",
            category = FundCategory.MID_CAP,
            nav = 98.70,
            peRatio = 33.6,
            categoryAvgPe = 30.8,
            return1Y = 34.2,
            return3Y = 27.5,
            return5Y = 28.2,
            alpha = 7.9,
            beta = 0.98,
            standardDeviation = 16.2,
            sharpeRatio = 1.54,
            expenseRatio = 0.69,
            aumCr = 18400.0,
            fundManager = "Niket Shah",
            ratingStars = 5,
            marketCapAlloc = MarketCapAlloc(largeCap = 21.0, midCap = 66.5, smallCap = 12.5),
            topHoldings = listOf(
                StockHolding("Trent Ltd.", "TRENT", "Consumer Discretionary", 7.8),
                StockHolding("Kalyan Jewellers India", "KALYANKJIL", "Consumer Discretionary", 6.2),
                StockHolding("Persistent Systems Ltd.", "PERSISTENT", "Technology", 5.8),
                StockHolding("Polycab India Ltd.", "POLYCAB", "Capital Goods", 5.2),
                StockHolding("Coforge Ltd.", "COFORGE", "Technology", 4.9),
                StockHolding("Jio Financial Services", "JIOFIN", "Financial Services", 4.6),
                StockHolding("Dixon Technologies Ltd.", "DIXON", "Technology", 4.2),
                StockHolding("ICICI Bank Ltd.", "ICICIBANK", "Financial Services", 3.5),
                StockHolding("HDFC Bank Ltd.", "HDFCBANK", "Financial Services", 3.1),
                StockHolding("Cummins India Ltd.", "CUMMINSIND", "Capital Goods", 2.8),
                StockHolding("L&T Technology Services", "LTTS", "Technology", 2.5),
                StockHolding("Bharat Electronics Ltd.", "BEL", "Capital Goods", 2.2)
            ),
            navHistory5Y = listOf(
                NavPoint("2021", 10000.0),
                NavPoint("2022", 12800.0),
                NavPoint("2023", 16900.0),
                NavPoint("2024", 24200.0),
                NavPoint("2025", 30800.0),
                NavPoint("2026", 34650.0)
            )
        )
    )

    fun getAllFunds(): List<MutualFund> = fundsList

    fun getFundById(id: String): MutualFund? = fundsList.find { it.id == id }

    // Quick search / auto-fetch feature matching >= 3 letters or words
    fun searchFunds(query: String): List<MutualFund> {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return fundsList
        val queryWords = trimmed.lowercase().split("\\s+".toRegex()).filter { it.isNotEmpty() }
        return fundsList.filter { fund ->
            val target = "${fund.name} ${fund.amc} ${fund.category.displayName}".lowercase()
            queryWords.all { word -> target.contains(word) } ||
            fund.topHoldings.any { h -> queryWords.any { q -> h.name.lowercase().contains(q) || h.ticker.lowercase().contains(q) } }
        }
    }

    // Auto-fetch suggestions when typing >= 3 chars or 3 words
    fun getAutoSuggestions(query: String): List<MutualFund> {
        val trimmed = query.trim()
        if (trimmed.length < 3) return emptyList()
        return searchFunds(trimmed).take(6)
    }

    // Calculate aggregated stock exposure across user's selected funds (equal or weighted)
    // "all my fund have in total how much percentage investment in a stock (like eg if I have 5 funds, how much in icici bank)"
    fun calculatePortfolioStockExposure(selectedFunds: List<MutualFund>): List<PortfolioStockExposure> {
        if (selectedFunds.isEmpty()) return emptyList()

        val fundWeight = 1.0 / selectedFunds.size
        val stockMap = mutableMapOf<String, MutableList<Pair<MutualFund, StockHolding>>>()

        for (fund in selectedFunds) {
            for (holding in fund.topHoldings) {
                val key = holding.name
                val list = stockMap.getOrPut(key) { mutableListOf() }
                list.add(Pair(fund, holding))
            }
        }

        val result = stockMap.map { (stockName, fundPairs) ->
            var aggregatePercent = 0.0
            val fundBreakdown = fundPairs.map { (fund, holding) ->
                val effectiveShare = holding.weight * fundWeight
                aggregatePercent += effectiveShare
                FundShare(
                    fundName = fund.name,
                    individualHoldingPercent = holding.weight,
                    effectiveContributionPercent = (effectiveShare * 100.0).roundToInt() / 100.0
                )
            }

            val firstHolding = fundPairs.first().second
            PortfolioStockExposure(
                stockName = stockName,
                ticker = firstHolding.ticker,
                sector = firstHolding.sector,
                aggregatePercent = (aggregatePercent * 100.0).roundToInt() / 100.0,
                fundCount = fundPairs.size,
                fundBreakdown = fundBreakdown.sortedByDescending { it.individualHoldingPercent }
            )
        }

        return result.sortedByDescending { it.aggregatePercent }
    }

    // Calculate pairwise fund overlap matrix
    fun calculatePairwiseOverlaps(selectedFunds: List<MutualFund>): List<PairwiseOverlap> {
        if (selectedFunds.size < 2) return emptyList()
        val overlaps = mutableListOf<PairwiseOverlap>()

        for (i in 0 until selectedFunds.size) {
            for (j in i + 1 until selectedFunds.size) {
                val f1 = selectedFunds[i]
                val f2 = selectedFunds[j]

                var overlapSum = 0.0
                val commonStocks = mutableListOf<String>()

                for (h1 in f1.topHoldings) {
                    val matchInF2 = f2.topHoldings.find { it.name.equals(h1.name, ignoreCase = true) }
                    if (matchInF2 != null) {
                        val minOverlap = min(h1.weight, matchInF2.weight)
                        overlapSum += minOverlap
                        commonStocks.add("${h1.name} (min ${minOverlap.roundToOneDecimal()}%)")
                    }
                }

                overlaps.add(
                    PairwiseOverlap(
                        fund1Name = f1.name,
                        fund2Name = f2.name,
                        overlapPercent = (overlapSum * 10.0).roundToInt() / 10.0,
                        commonStocksCount = commonStocks.size,
                        topCommonStocks = commonStocks.take(5)
                    )
                )
            }
        }
        return overlaps.sortedByDescending { it.overlapPercent }
    }

    // Multi-fund portfolio summary
    fun getPortfolioSummary(selectedFunds: List<MutualFund>): PortfolioSummary {
        if (selectedFunds.isEmpty()) {
            return PortfolioSummary(
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
            )
        }

        val n = selectedFunds.size.toDouble()
        val avg3Y = selectedFunds.sumOf { it.return3Y } / n
        val avg5Y = selectedFunds.sumOf { it.return5Y } / n
        val avgPe = selectedFunds.sumOf { it.peRatio } / n
        val avgCatPe = selectedFunds.sumOf { it.categoryAvgPe } / n
        val avgAlpha = selectedFunds.sumOf { it.alpha } / n
        val avgStdDev = selectedFunds.sumOf { it.standardDeviation } / n

        val avgLarge = selectedFunds.sumOf { it.marketCapAlloc.largeCap } / n
        val avgMid = selectedFunds.sumOf { it.marketCapAlloc.midCap } / n
        val avgSmall = selectedFunds.sumOf { it.marketCapAlloc.smallCap } / n

        val consolidatedHoldings = calculatePortfolioStockExposure(selectedFunds)
        val overlaps = calculatePairwiseOverlaps(selectedFunds)

        return PortfolioSummary(
            totalFunds = selectedFunds.size,
            weightedReturn3Y = avg3Y.roundToOneDecimal(),
            weightedReturn5Y = avg5Y.roundToOneDecimal(),
            weightedPeRatio = avgPe.roundToOneDecimal(),
            weightedCategoryPe = avgCatPe.roundToOneDecimal(),
            weightedAlpha = avgAlpha.roundToOneDecimal(),
            weightedStdDev = avgStdDev.roundToOneDecimal(),
            aggregateLargeCap = avgLarge.roundToOneDecimal(),
            aggregateMidCap = avgMid.roundToOneDecimal(),
            aggregateSmallCap = avgSmall.roundToOneDecimal(),
            topConsolidatedHoldings = consolidatedHoldings,
            pairwiseOverlaps = overlaps
        )
    }

    // ----------------------------------------------------
    // CALCULATORS ENGINE
    // ----------------------------------------------------

    fun calculateChildrenEducation(
        currentAge: Int,
        collegeAge: Int,
        currentCost: Double,
        inflationRatePercent: Double,
        expectedReturnPercent: Double
    ): EducationCalcResult {
        val yearsRemaining = max(1, collegeAge - currentAge)
        val futureCost = currentCost * (1.0 + inflationRatePercent / 100.0).pow(yearsRemaining.toDouble())
        val monthlyRate = (expectedReturnPercent / 100.0) / 12.0
        val totalMonths = yearsRemaining * 12

        val factor = ((1.0 + monthlyRate).pow(totalMonths.toDouble()) - 1.0) / monthlyRate * (1.0 + monthlyRate)
        val monthlySip = if (factor > 0) futureCost / factor else 0.0
        val totalInvested = monthlySip * totalMonths
        val estimatedGains = max(0.0, futureCost - totalInvested)

        return EducationCalcResult(
            yearsRemaining = yearsRemaining,
            futureCost = futureCost.roundToOneDecimal(),
            monthlySipRequired = monthlySip.roundToOneDecimal(),
            totalInvestment = totalInvested.roundToOneDecimal(),
            estimatedGains = estimatedGains.roundToOneDecimal()
        )
    }

    fun calculateRetirement(
        currentAge: Int,
        retireAge: Int,
        lifeExpectancyAge: Int,
        currentMonthlyExpense: Double,
        inflationPercent: Double,
        postRetirementReturnPercent: Double,
        preRetirementReturnPercent: Double
    ): RetirementCalcResult {
        val yearsToRetire = max(1, retireAge - currentAge)
        val yearsInRetirement = max(1, lifeExpectancyAge - retireAge)

        val monthlyExpenseAtRetire = currentMonthlyExpense * (1.0 + inflationPercent / 100.0).pow(yearsToRetire.toDouble())
        val annualExpenseAtRetire = monthlyExpenseAtRetire * 12.0

        // Real post-retirement rate of return adjusted for inflation
        val realReturn = ((1.0 + postRetirementReturnPercent / 100.0) / (1.0 + inflationPercent / 100.0)) - 1.0
        val corpusNeeded = if (realReturn > 0.001) {
            annualExpenseAtRetire * (1.0 - (1.0 + realReturn).pow(-yearsInRetirement.toDouble())) / realReturn
        } else {
            annualExpenseAtRetire * yearsInRetirement
        }

        val monthlyPreRate = (preRetirementReturnPercent / 100.0) / 12.0
        val totalMonths = yearsToRetire * 12
        val factor = ((1.0 + monthlyPreRate).pow(totalMonths.toDouble()) - 1.0) / monthlyPreRate * (1.0 + monthlyPreRate)
        val monthlySip = if (factor > 0) corpusNeeded / factor else 0.0
        val totalInvested = monthlySip * totalMonths

        return RetirementCalcResult(
            yearsToRetire = yearsToRetire,
            monthlyExpenseAtRetirement = monthlyExpenseAtRetire.roundToOneDecimal(),
            requiredCorpus = corpusNeeded.roundToOneDecimal(),
            monthlySipRequired = monthlySip.roundToOneDecimal(),
            totalInvested = totalInvested.roundToOneDecimal(),
            wealthCreated = max(0.0, corpusNeeded - totalInvested).roundToOneDecimal()
        )
    }

    fun calculateFutureValueOfFund(
        principalAmount: Double,
        tenureYears: Int,
        expectedReturnPercent: Double
    ): FutureValueResult {
        val futureValue = principalAmount * (1.0 + expectedReturnPercent / 100.0).pow(tenureYears.toDouble())
        val gains = futureValue - principalAmount
        val multiplier = if (principalAmount > 0) futureValue / principalAmount else 0.0

        return FutureValueResult(
            presentValue = principalAmount,
            futureValue = futureValue.roundToOneDecimal(),
            totalGains = gains.roundToOneDecimal(),
            growthMultiplier = (multiplier * 10.0).roundToInt() / 10.0
        )
    }

    fun calculateFutureValueWithInflation(
        presentAmount: Double,
        tenureYears: Int,
        inflationRatePercent: Double,
        expectedReturnPercent: Double
    ): InflationPurchasingPowerResult {
        val futureCostOfSameGoods = presentAmount * (1.0 + inflationRatePercent / 100.0).pow(tenureYears.toDouble())
        val futureNominalValue = presentAmount * (1.0 + expectedReturnPercent / 100.0).pow(tenureYears.toDouble())
        val realPurchasingPower = futureNominalValue / (1.0 + inflationRatePercent / 100.0).pow(tenureYears.toDouble())

        val uninvestedLossPercent = (1.0 - (presentAmount / futureCostOfSameGoods)) * 100.0

        return InflationPurchasingPowerResult(
            initialAmount = presentAmount,
            futureNominalValue = futureNominalValue.roundToOneDecimal(),
            futureCostOfSameGoods = futureCostOfSameGoods.roundToOneDecimal(),
            realPurchasingPower = realPurchasingPower.roundToOneDecimal(),
            purchasingPowerLossPercent = uninvestedLossPercent.roundToOneDecimal()
        )
    }

    fun calculateSip(
        monthlyAmount: Double,
        tenureYears: Int,
        expectedReturnPercent: Double,
        stepUpPercent: Double = 0.0
    ): SipCalcResult {
        val monthlyRate = (expectedReturnPercent / 100.0) / 12.0
        var currentMonthly = monthlyAmount
        var totalInvested = 0.0
        var portfolioValue = 0.0
        val milestones = mutableListOf<YearlyMilestone>()

        for (year in 1..tenureYears) {
            for (month in 1..12) {
                totalInvested += currentMonthly
                portfolioValue = (portfolioValue + currentMonthly) * (1.0 + monthlyRate)
            }
            milestones.add(
                YearlyMilestone(
                    year = year,
                    cumulativeInvested = totalInvested.roundToOneDecimal(),
                    portfolioValue = portfolioValue.roundToOneDecimal()
                )
            )
            if (stepUpPercent > 0.0) {
                currentMonthly *= (1.0 + stepUpPercent / 100.0)
            }
        }

        val totalReturns = max(0.0, portfolioValue - totalInvested)
        val growthPercent = if (totalInvested > 0) (totalReturns / totalInvested) * 100.0 else 0.0

        return SipCalcResult(
            totalInvested = totalInvested.roundToOneDecimal(),
            totalReturns = totalReturns.roundToOneDecimal(),
            totalMaturityAmount = portfolioValue.roundToOneDecimal(),
            growthPercentage = growthPercent.roundToOneDecimal(),
            yearlyBreakdown = milestones
        )
    }

    // Portfolio Rebalancing for Tax Optimization
    fun calculateTaxOptimizedRebalance(
        assets: List<RebalanceAsset>
    ): TaxRebalanceReport {
        val totalPortfolio = assets.sumOf { it.currentAmount }
        if (totalPortfolio <= 0) {
            return TaxRebalanceReport(
                totalPortfolioValue = 0.0,
                recommendations = emptyList(),
                totalSaleRequired = 0.0,
                totalBuyRequired = 0.0,
                totalLtcgRealized = 0.0,
                ltcgExemptionUsed = 0.0,
                taxableLtcg = 0.0,
                ltcgTaxAmount = 0.0,
                stcgTaxAmount = 0.0,
                totalTaxLiability = 0.0,
                smartTaxFreeStrategy = "Portfolio value is zero."
            )
        }

        var totalSaleReq = 0.0
        var totalBuyReq = 0.0
        var totalLtcg = 0.0
        var totalStcg = 0.0

        val recommendations = assets.map { asset ->
            val currentPct = (asset.currentAmount / totalPortfolio) * 100.0
            val targetAmt = (asset.targetPercent / 100.0) * totalPortfolio
            val diff = targetAmt - asset.currentAmount

            val action = when {
                diff > 100 -> {
                    totalBuyReq += diff
                    RebalanceAction.BUY
                }
                diff < -100 -> {
                    val sellAmt = -diff
                    totalSaleReq += sellAmt
                    RebalanceAction.SELL
                }
                else -> RebalanceAction.HOLD
            }

            var gainOnSale = 0.0
            var taxForThisAsset = 0.0

            if (action == RebalanceAction.SELL) {
                val sellAmt = -diff
                val costBasisFraction = if (asset.currentAmount > 0) asset.purchaseCost / asset.currentAmount else 0.7
                val costOfSoldUnits = sellAmt * costBasisFraction
                gainOnSale = max(0.0, sellAmt - costOfSoldUnits)

                if (asset.holdingPeriodMonths > 12) {
                    totalLtcg += gainOnSale
                } else {
                    totalStcg += gainOnSale
                    taxForThisAsset = gainOnSale * 0.20 // 20% STCG
                }
            }

            RebalanceRecommendation(
                assetName = asset.name,
                currentAmount = asset.currentAmount.roundToOneDecimal(),
                currentPercent = currentPct.roundToOneDecimal(),
                targetPercent = asset.targetPercent.roundToOneDecimal(),
                targetAmount = targetAmt.roundToOneDecimal(),
                differenceAmount = diff.roundToOneDecimal(),
                action = action,
                estimatedGainOnSale = gainOnSale.roundToOneDecimal(),
                estimatedTaxLiability = taxForThisAsset.roundToOneDecimal()
            )
        }

        // Budget 2024 LTCG exemption is ₹1,25,000 per financial year, remaining taxed at 12.5%
        val ltcgExemptionLimit = 125000.0
        val ltcgExemptionUsed = min(totalLtcg, ltcgExemptionLimit)
        val taxableLtcg = max(0.0, totalLtcg - ltcgExemptionUsed)
        val ltcgTaxAmount = taxableLtcg * 0.125
        val stcgTaxAmount = totalStcg * 0.20
        val totalTax = ltcgTaxAmount + stcgTaxAmount

        val smartStrategy = if (totalBuyReq > 0) {
            "Tax Optimization Advice: Instead of selling overweight funds and incurring capital gains tax (₹${totalTax.toCurrencyString()}), rebalance using fresh capital inflows or monthly SIPs. Direct the next monthly SIPs or ₹${totalBuyReq.toCurrencyString()} fresh cash specifically into the underweight funds (${recommendations.filter { it.action == RebalanceAction.BUY }.joinToString { it.assetName }}). This achieves your target allocation with ₹0 capital gains tax liability."
        } else {
            "Your portfolio allocation is in target equilibrium. No rebalancing transaction needed."
        }

        return TaxRebalanceReport(
            totalPortfolioValue = totalPortfolio.roundToOneDecimal(),
            recommendations = recommendations,
            totalSaleRequired = totalSaleReq.roundToOneDecimal(),
            totalBuyRequired = totalBuyReq.roundToOneDecimal(),
            totalLtcgRealized = totalLtcg.roundToOneDecimal(),
            ltcgExemptionUsed = ltcgExemptionUsed.roundToOneDecimal(),
            taxableLtcg = taxableLtcg.roundToOneDecimal(),
            ltcgTaxAmount = ltcgTaxAmount.roundToOneDecimal(),
            stcgTaxAmount = stcgTaxAmount.roundToOneDecimal(),
            totalTaxLiability = totalTax.roundToOneDecimal(),
            smartTaxFreeStrategy = smartStrategy
        )
    }
}

fun Double.roundToOneDecimal(): Double = (this * 10.0).roundToInt() / 10.0
fun Double.toCurrencyString(): String = "%,.0f".format(this)
