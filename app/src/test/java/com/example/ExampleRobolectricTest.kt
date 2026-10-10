package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ui.FundViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("FundIntel", appName)
  }

  @Test
  fun `verify fund view model initial state`() {
    val viewModel = FundViewModel()
    val state = viewModel.uiState.value
    assertNotNull(state)
    assertTrue("Should load funds", state.allFunds.isNotEmpty())
    assertTrue("Should have initial comparison funds", state.selectedComparisonFunds.isNotEmpty())
    assertNotNull("Should have calculated portfolio summary", state.portfolioSummary)
    assertTrue("Should have non-empty consolidated stock exposure", state.portfolioSummary.topConsolidatedHoldings.isNotEmpty())
  }
}
