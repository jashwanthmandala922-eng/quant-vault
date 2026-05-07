package com.quantvault.authenticator.ui.authenticator.feature.qrcodescan

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quantvault.ui.platform.base.util.EventsEffect
import com.quantvault.ui.platform.base.util.StatusBarsAppearanceAffect
import com.quantvault.ui.platform.base.util.standardHorizontalMargin
import com.quantvault.ui.platform.components.appbar.QuantVaultTopAppBar
import com.quantvault.ui.platform.components.camera.CameraPreview
import com.quantvault.ui.platform.components.camera.QrCodeSquare
import com.quantvault.ui.platform.components.dialog.QuantVaultBasicDialog
import com.quantvault.ui.platform.components.scaffold.QuantVaultScaffold
import com.quantvault.ui.platform.components.text.QuantVaultHyperTextLink
import com.quantvault.ui.platform.composition.LocalQrCodeAnalyzer
import com.quantvault.ui.platform.feature.qrcodescan.util.QrCodeAnalyzer
import com.quantvault.ui.platform.model.WindowSize
import com.quantvault.ui.platform.resource.QuantVaultDrawable
import com.quantvault.ui.platform.resource.QuantVaultString
import com.quantvault.ui.platform.theme.QuantVaultTheme
import com.quantvault.ui.platform.theme.LocalQuantVaultColorScheme
import com.quantvault.ui.platform.theme.color.darkQuantVaultColorScheme
import com.quantvault.ui.platform.util.rememberWindowSize

/**
 * The screen to scan QR codes for the application.
 */
@Suppress("LongMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrCodeScanScreen(
    onNavigateBack: () -> Unit,
    viewModel: QrCodeScanViewModel = hiltViewModel(),
    qrCodeAnalyzer: QrCodeAnalyzer = LocalQrCodeAnalyzer.current,
    onNavigateToManualCodeEntryScreen: () -> Unit,
) {
    qrCodeAnalyzer.onQrCodeScanned = {
        viewModel.trySendAction(QrCodeScanAction.QrCodeScanReceive(it))
    }
    val onEnterCodeManuallyClick = {
        viewModel.trySendAction(QrCodeScanAction.ManualEntryTextClick)
    }
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    EventsEffect(viewModel = viewModel) { event ->
        when (event) {
            is QrCodeScanEvent.NavigateBack -> {
                onNavigateBack.invoke()
            }

            is QrCodeScanEvent.NavigateToManualCodeEntry -> {
                onNavigateToManualCodeEntryScreen.invoke()
            }
        }
    }

    // This screen should always look like it's in dark mode
    CompositionLocalProvider(LocalQuantVaultColorScheme provides darkQuantVaultColorScheme) {
        StatusBarsAppearanceAffect()
        QrCodeScanDialogs(
            dialogState = state.dialog,
            onSaveHereClick = { viewModel.trySendAction(QrCodeScanAction.SaveLocallyClick(it)) },
            onTakeMeToQuantVaultClick = {
                viewModel.trySendAction(QrCodeScanAction.SaveToQuantVaultClick(it))
            },
            onDismissRequest = {
                viewModel.trySendAction(QrCodeScanAction.SaveToQuantVaultErrorDismiss)
            },
        )

        QuantVaultScaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                QuantVaultTopAppBar(
                    title = stringResource(id = QuantVaultString.scan_qr_code),
                    navigationIcon = painterResource(id = QuantVaultDrawable.ic_close),
                    navigationIconContentDescription = stringResource(id = QuantVaultString.close),
                    onNavigationIconClick = {
                        viewModel.trySendAction(QrCodeScanAction.CloseClick)
                    },
                    scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                )
            },
        ) {
            CameraPreview(
                cameraErrorReceive = {
                    viewModel.trySendAction(QrCodeScanAction.CameraSetupErrorReceive)
                },
                analyzer = qrCodeAnalyzer,
                modifier = Modifier.fillMaxSize(),
            )

            when (rememberWindowSize()) {
                WindowSize.Compact -> {
                    QrCodeContentCompact(
                        onEnterCodeManuallyClick = onEnterCodeManuallyClick,
                    )
                }

                WindowSize.Medium -> {
                    QrCodeContentMedium(
                        onEnterCodeManuallyClick = onEnterCodeManuallyClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun QrCodeScanDialogs(
    dialogState: QrCodeScanState.DialogState?,
    onSaveHereClick: (Boolean) -> Unit,
    onTakeMeToQuantVaultClick: (Boolean) -> Unit,
    onDismissRequest: () -> Unit,
) {
    when (dialogState) {
        QrCodeScanState.DialogState.ChooseSaveLocation -> {
            ChooseSaveLocationDialog(
                onSaveHereClick = onSaveHereClick,
                onTakeMeToQuantVaultClick = onTakeMeToQuantVaultClick,
            )
        }

        QrCodeScanState.DialogState.SaveToQuantVaultError -> {
            QuantVaultBasicDialog(
                title = stringResource(id = QuantVaultString.something_went_wrong),
                message = stringResource(id = QuantVaultString.please_try_again),
                onDismissRequest = onDismissRequest,
            )
        }

        null -> Unit
    }
}

@Composable
private fun QrCodeContentCompact(
    onEnterCodeManuallyClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        QrCodeSquare(
            squareOutlineSize = 250.dp,
            modifier = Modifier.weight(2f),
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .background(color = Color.Black.copy(alpha = .4f))
                .standardHorizontalMargin()
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = stringResource(id = QuantVaultString.point_your_camera_at_the_qr_code),
                textAlign = TextAlign.Center,
                color = QuantVaultTheme.colorScheme.text.primary,
                style = QuantVaultTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp),
            )

            BottomClickableText(
                onEnterCodeManuallyClick = onEnterCodeManuallyClick,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

@Composable
private fun QrCodeContentMedium(
    onEnterCodeManuallyClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        QrCodeSquare(
            squareOutlineSize = 200.dp,
            modifier = Modifier.weight(2f),
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .background(color = Color.Black.copy(alpha = .4f))
                .standardHorizontalMargin()
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = stringResource(id = QuantVaultString.point_your_camera_at_the_qr_code),
                textAlign = TextAlign.Center,
                color = QuantVaultTheme.colorScheme.text.primary,
                style = QuantVaultTheme.typography.bodySmall,
            )

            BottomClickableText(
                onEnterCodeManuallyClick = onEnterCodeManuallyClick,
            )
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

@Composable
private fun BottomClickableText(
    onEnterCodeManuallyClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    QuantVaultHyperTextLink(
        annotatedResId = QuantVaultString.cannot_scan_qr_code_enter_key_manually,
        annotationKey = "enterKeyManually",
        accessibilityString = stringResource(QuantVaultString.enter_key_manually),
        onClick = onEnterCodeManuallyClick,
        style = QuantVaultTheme.typography.bodySmall,
        color = QuantVaultTheme.colorScheme.text.primary,
        modifier = modifier,
    )
}




