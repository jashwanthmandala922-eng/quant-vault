package com.quantvault.ui.platform.components.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.quantvault.ui.platform.base.util.standardHorizontalMargin
import com.quantvault.ui.platform.components.indicator.quantvaultCircularProgressIndicator
import com.quantvault.ui.platform.components.scaffold.quantvaultScaffold
import com.quantvault.ui.platform.theme.QuantVaultTheme

/**
 * A quantvault-themed, re-usable loading state.
 */
@Composable
fun quantvaultLoadingContent(
    modifier: Modifier = Modifier,
    text: String? = null,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        text?.let {
            Text(
                text = it,
                style = QuantVaultTheme.typography.titleMedium,
                // setting color explicitly here as we can't assume what the surface will be.
                color = QuantVaultTheme.colorScheme.text.primary,
                modifier = Modifier.testTag(tag = "AlertTitleText"),
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        quantvaultCircularProgressIndicator(
            modifier = Modifier
                .size(48.dp)
                .testTag(tag = "AlertProgressIndicator"),
        )
        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}

@Preview(showBackground = true, name = "quantvault loading content")
@Composable
private fun quantvaultLoadingContent_preview() {
    quantvaultScaffold {
        quantvaultLoadingContent(
            text = "Loading...",
            modifier = Modifier
                .fillMaxSize()
                .standardHorizontalMargin(),
        )
    }
}






