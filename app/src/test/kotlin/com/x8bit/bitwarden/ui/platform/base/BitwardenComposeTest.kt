package com.quantvault.app.ui.platform.base

import androidx.compose.runtime.Composable
import com.quantvault.cxf.importer.CredentialExchangeImporter
import com.quantvault.cxf.manager.CredentialExchangeCompletionManager
import com.quantvault.cxf.validator.CredentialExchangeRequestValidator
import com.bitwarden.ui.platform.base.BaseComposeTest
import com.bitwarden.ui.platform.feature.cardscanner.util.CardTextAnalyzer
import com.bitwarden.ui.platform.feature.qrcodescan.util.QrCodeAnalyzer
import com.bitwarden.ui.platform.feature.settings.appearance.model.AppTheme
import com.bitwarden.ui.platform.manager.IntentManager
import com.bitwarden.ui.platform.manager.exit.ExitManager
import com.bitwarden.ui.platform.theme.QuantVaultTheme
import com.quantvault.app.data.platform.manager.util.AppResumeStateManager
import com.quantvault.app.ui.credentials.manager.CredentialProviderCompletionManager
import com.quantvault.app.ui.platform.composition.LocalManagerProvider
import com.quantvault.app.ui.platform.manager.biometrics.BiometricsManager
import com.quantvault.app.ui.platform.manager.keychain.KeyChainManager
import com.quantvault.app.ui.platform.manager.nfc.NfcManager
import com.quantvault.app.ui.platform.manager.permissions.PermissionsManager
import com.quantvault.app.ui.platform.manager.review.AppReviewManager
import com.quantvault.app.ui.platform.model.AuthTabLaunchers
import com.quantvault.app.ui.platform.model.FeatureFlagsState
import io.mockk.mockk
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

abstract class quantvaultComposeTest : BaseComposeTest() {

    /**
     * Helper for testing a basic Composable function that only requires a [Composable]. The
     * [AppTheme] is overridable and the [backDispatcher] is configured automatically.
     */
    @Suppress("LongParameterList")
    protected fun setContent(
        theme: AppTheme = AppTheme.DEFAULT,
        featureFlagsState: FeatureFlagsState = FeatureFlagsState,
        authTabLaunchers: AuthTabLaunchers = mockk(),
        appResumeStateManager: AppResumeStateManager = mockk(),
        appReviewManager: AppReviewManager = mockk(),
        biometricsManager: BiometricsManager = mockk(),
        clock: Clock = Clock.fixed(Instant.parse("2023-10-27T12:00:00Z"), ZoneOffset.UTC),
        exitManager: ExitManager = mockk(),
        intentManager: IntentManager = mockk(),
        credentialProviderCompletionManager: CredentialProviderCompletionManager = mockk(),
        keyChainManager: KeyChainManager = mockk(),
        nfcManager: NfcManager = mockk(),
        permissionsManager: PermissionsManager = mockk(),
        credentialExchangeImporter: CredentialExchangeImporter = mockk(),
        credentialExchangeCompletionManager: CredentialExchangeCompletionManager = mockk(),
        credentialExchangeRequestValidator: CredentialExchangeRequestValidator = mockk(),
        cardTextAnalyzer: CardTextAnalyzer = mockk(),
        qrCodeAnalyzer: QrCodeAnalyzer = mockk(),
        test: @Composable () -> Unit,
    ) {
        setTestContent {
            LocalManagerProvider(
                featureFlagsState = featureFlagsState,
                authTabLaunchers = authTabLaunchers,
                appResumeStateManager = appResumeStateManager,
                appReviewManager = appReviewManager,
                biometricsManager = biometricsManager,
                clock = clock,
                exitManager = exitManager,
                intentManager = intentManager,
                credentialProviderCompletionManager = credentialProviderCompletionManager,
                keyChainManager = keyChainManager,
                nfcManager = nfcManager,
                permissionsManager = permissionsManager,
                credentialExchangeImporter = credentialExchangeImporter,
                credentialExchangeCompletionManager = credentialExchangeCompletionManager,
                credentialExchangeRequestValidator = credentialExchangeRequestValidator,
                cardTextAnalyzer = cardTextAnalyzer,
                qrCodeAnalyzer = qrCodeAnalyzer,
            ) {
                QuantVaultTheme(
                    theme = theme,
                    content = test,
                )
            }
        }
    }
}




