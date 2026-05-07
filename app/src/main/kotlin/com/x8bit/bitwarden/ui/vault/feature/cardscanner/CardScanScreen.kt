package com.x8bit.bitwarden.ui.vault.feature.cardscanner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.bitwarden.ui.platform.base.util.EventsEffect
import com.bitwarden.ui.platform.base.util.StatusBarsAppearanceAffect
import com.bitwarden.ui.platform.components.appbar.QuantVaultTopAppBar
import com.bitwarden.ui.platform.components.camera.CameraPreview
import com.bitwarden.ui.platform.components.camera.CardScanOverlay
import com.bitwarden.ui.platform.components.scaffold.QuantVaultScaffold
import com.bitwarden.ui.platform.components.util.rememberVectorPainter
import com.bitwarden.ui.platform.composition.LocalCardTextAnalyzer
import com.bitwarden.ui.platform.feature.cardscanner.util.CardTextAnalyzer
import com.bitwarden.ui.platform.model.WindowSize
import com.bitwarden.ui.platform.theme.QuantVaultTheme
import com.bitwarden.ui.platform.theme.LocalQuantVaultColorScheme
import com.bitwarden.ui.platform.theme.color.darkQuantVaultColorScheme
import com.bitwarden.ui.platform.util.rememberWindowSize
import com.x8bit.bitwarden.R

/**
 * The screen to scan credit cards for the application.
 */
@Suppress("LongMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardScanScreen(
    onNavigateBack: () -> Unit,
    viewModel: CardScanViewModel = hiltViewModel(),
    cardTextAnalyzer: CardTextAnalyzer = LocalCardTextAnalyzer.current,
) {
    cardTextAnalyzer.onCardScanned = { cardScanData ->
        viewModel.trySendAction(
            CardScanAction.CardScanReceive(cardScanData = cardScanData),
        )
    }

    EventsEffect(viewModel = viewModel) { event ->
        when (event) {
            is CardScanEvent.NavigateBack -> onNavigateBack()
        }
    }

    // This screen should always look like it's in dark mode
    CompositionLocalProvider(
        LocalQuantVaultColorScheme provides darkQuantVaultColorScheme,
    ) {
        StatusBarsAppearanceAffect()
        QuantVaultScaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                QuantVaultTopAppBar(
                    title = stringResource(id = R.string.scan_card),
                    navigationIcon = rememberVectorPainter(
                        id = R.drawable.ic_close,
                    ),
                    navigationIconContentDescription = stringResource(
                        id = R.string.close,
                    ),
                    onNavigationIconClick = {
                        viewModel.trySendAction(CardScanAction.CloseClick)
                    },
                    scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
                        state = rememberTopAppBarState(),
                    ),
                )
            },
        ) {
            CameraPreview(
                cameraErrorReceive = {
                    viewModel.trySendAction(
                        CardScanAction.CameraSetupErrorReceive,
                    )
                },
                analyzer = cardTextAnalyzer,
                modifier = Modifier.fillMaxSize(),
            )
            when (rememberWindowSize()) {
                WindowSize.Compact -> {
                    CardScanContentCompact()
                }

                WindowSize.Medium -> {
                    CardScanContentMedium()
                }
            }
        }
    }
}

@Composable
private fun CardScanContentCompact(
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        CardScanOverlay(
            overlayWidth = 300.dp,
            modifier = Modifier.weight(2f),
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .background(color = QuantVaultTheme.colorScheme.background.scrim)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = stringResource(
                    id = R.string.scan_card_instruction,
                ),
                textAlign = TextAlign.Center,
                color = QuantVaultTheme.colorScheme.text.primary,
                style = QuantVaultTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

@Composable
private fun CardScanContentMedium(
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        CardScanOverlay(
            overlayWidth = 250.dp,
            modifier = Modifier.weight(2f),
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .background(color = QuantVaultTheme.colorScheme.background.scrim)
                .padding(horizontal = 16.dp)
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = stringResource(
                    id = R.string.scan_card_instruction,
                ),
                textAlign = TextAlign.Center,
                color = QuantVaultTheme.colorScheme.text.primary,
                style = QuantVaultTheme.typography.bodySmall,
            )
        }
    }
}






