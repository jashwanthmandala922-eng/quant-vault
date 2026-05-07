@file:Suppress("TooManyFunctions")

package com.x8bit.bitwarden.ui.platform.feature.premium.plan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quantvault.annotation.OmitFromCoverage
import com.bitwarden.ui.platform.base.util.EventsEffect
import com.bitwarden.ui.platform.base.util.cardStyle
import com.bitwarden.ui.platform.base.util.standardHorizontalMargin
import com.bitwarden.ui.platform.components.appbar.QuantVaultTopAppBar
import com.bitwarden.ui.platform.components.badge.QuantVaultStatusBadge
import com.bitwarden.ui.platform.components.button.QuantVaultFilledButton
import com.bitwarden.ui.platform.components.button.QuantVaultOutlinedButton
import com.bitwarden.ui.platform.components.content.QuantVaultContentBlock
import com.bitwarden.ui.platform.components.content.model.ContentBlockData
import com.bitwarden.ui.platform.components.dialog.QuantVaultLoadingDialog
import com.bitwarden.ui.platform.components.dialog.QuantVaultTwoButtonDialog
import com.bitwarden.ui.platform.components.divider.QuantVaultHorizontalDivider
import com.bitwarden.ui.platform.components.model.CardStyle
import com.bitwarden.ui.platform.components.scaffold.QuantVaultScaffold
import com.bitwarden.ui.platform.components.snackbar.QuantVaultSnackbarHost
import com.bitwarden.ui.platform.components.snackbar.model.rememberQuantVaultSnackbarHostState
import com.bitwarden.ui.platform.components.util.rememberVectorPainter
import com.bitwarden.ui.platform.composition.LocalIntentManager
import com.bitwarden.ui.platform.manager.IntentManager
import com.bitwarden.ui.platform.theme.QuantVaultTheme
import com.bitwarden.ui.util.Text
import com.bitwarden.ui.util.asText
import com.x8bit.bitwarden.data.billing.repository.model.PremiumSubscriptionStatus
import com.x8bit.bitwarden.ui.platform.composition.LocalAuthTabLaunchers
import com.x8bit.bitwarden.ui.platform.feature.premium.plan.handlers.PlanHandlers
import com.x8bit.bitwarden.ui.platform.feature.premium.plan.util.badgeColors
import com.x8bit.bitwarden.ui.platform.feature.premium.plan.util.labelRes
import com.x8bit.bitwarden.ui.platform.model.AuthTabLaunchers
import com.x8bit.bitwarden.R

/**
 * The screen for the plan — shows the upgrade flow for free users and the
 * subscription-management surface for premium users.
 */
@Suppress("LongMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanScreen(
    onNavigateBack: () -> Unit,
    viewModel: PlanViewModel = hiltViewModel(),
    intentManager: IntentManager = LocalIntentManager.current,
    authTabLaunchers: AuthTabLaunchers = LocalAuthTabLaunchers.current,
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val handlers = remember(viewModel) { PlanHandlers.create(viewModel) }
    val snackbarHostState = rememberQuantVaultSnackbarHostState()

    EventsEffect(viewModel = viewModel) { event ->
        when (event) {
            is PlanEvent.LaunchBrowser -> {
                intentManager.startAuthTab(
                    uri = event.url.toUri(),
                    authTabData = event.authTabData,
                    launcher = authTabLaunchers.premiumCheckout,
                )
            }

            is PlanEvent.LaunchPortal -> intentManager.launchUri(event.url.toUri())
            PlanEvent.NavigateBack -> onNavigateBack()
            is PlanEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.data)
        }
    }

    PlanDialogs(
        dialogState = state.dialogState,
        handlers = handlers,
    )

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    QuantVaultScaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = {
            QuantVaultSnackbarHost(QuantVaultHostState = snackbarHostState)
        },
        topBar = {
            QuantVaultTopAppBar(
                title = stringResource(id = state.title),
                scrollBehavior = scrollBehavior,
                navigationIcon = rememberVectorPainter(id = state.navigationIcon),
                navigationIconContentDescription = stringResource(
                    id = state.navigationIconContentDescription,
                ),
                onNavigationIconClick = handlers.onBackClick,
            )
        },
    ) {
        when (val viewState = state.viewState) {
            is PlanState.ViewState.Free -> {
                FreeContent(
                    viewState = viewState,
                    handlers = handlers,
                )
            }

            is PlanState.ViewState.Premium -> {
                PremiumContent(
                    viewState = viewState,
                    handlers = handlers,
                )
            }
        }
    }
}

@Suppress("LongMethod")
@Composable
private fun PlanDialogs(
    dialogState: PlanState.DialogState?,
    handlers: PlanHandlers,
) {
    when (dialogState) {
        is PlanState.DialogState.CheckoutError -> {
            QuantVaultTwoButtonDialog(
                title = stringResource(id = R.string.secure_checkout_didnt_load),
                message = stringResource(id = R.string.trouble_opening_payment_page),
                confirmButtonText = stringResource(id = R.string.try_again),
                dismissButtonText = stringResource(id = R.string.close),
                onConfirmClick = handlers.onRetryClick,
                onDismissClick = handlers.onDismissError,
                onDismissRequest = handlers.onDismissError,
            )
        }

        is PlanState.DialogState.GetPricingError -> {
            QuantVaultTwoButtonDialog(
                title = dialogState.title(),
                message = dialogState.message(),
                confirmButtonText = stringResource(R.string.try_again),
                dismissButtonText = stringResource(R.string.close),
                onConfirmClick = handlers.onRetryPricingClick,
                onDismissClick = handlers.onClosePricingErrorClick,
                onDismissRequest = handlers.onClosePricingErrorClick,
            )
        }

        is PlanState.DialogState.WaitingForPayment -> {
            QuantVaultTwoButtonDialog(
                title = stringResource(id = R.string.payment_not_received_yet),
                message = stringResource(id = R.string.return_to_stripe_to_finish),
                confirmButtonText = stringResource(id = R.string.go_back),
                dismissButtonText = stringResource(id = R.string.close),
                onConfirmClick = handlers.onGoBackClick,
                onDismissClick = handlers.onCancelWaiting,
                onDismissRequest = handlers.onCancelWaiting,
            )
        }

        is PlanState.DialogState.PendingUpgrade -> {
            QuantVaultTwoButtonDialog(
                title = stringResource(id = R.string.upgrade_pending),
                message = stringResource(
                    id = R.string.upgrade_pending_message,
                ),
                confirmButtonText = stringResource(id = R.string.sync_now),
                dismissButtonText = stringResource(id = R.string.continue_text),
                onConfirmClick = handlers.onSyncClick,
                onDismissClick = handlers.onContinueClick,
                onDismissRequest = handlers.onContinueClick,
            )
        }

        is PlanState.DialogState.CancelConfirmation -> {
            QuantVaultTwoButtonDialog(
                title = stringResource(id = R.string.cancel_premium),
                message = stringResource(
                    id = R.string.cancel_premium_confirmation,
                    dialogState.nextRenewalDate,
                ),
                confirmButtonText = stringResource(id = R.string.cancel_now),
                dismissButtonText = stringResource(id = R.string.close),
                onConfirmClick = handlers.onConfirmCancelClick,
                onDismissClick = handlers.onDismissCancelConfirmation,
                onDismissRequest = handlers.onDismissCancelConfirmation,
            )
        }

        is PlanState.DialogState.PortalError -> {
            QuantVaultTwoButtonDialog(
                title = stringResource(id = R.string.portal_error),
                message = stringResource(id = R.string.trouble_loading_portal),
                confirmButtonText = stringResource(id = R.string.try_again),
                dismissButtonText = stringResource(id = R.string.close),
                onConfirmClick = handlers.onManagePlanClick,
                onDismissClick = handlers.onDismissPortalError,
                onDismissRequest = handlers.onDismissPortalError,
            )
        }

        is PlanState.DialogState.SubscriptionError -> {
            QuantVaultTwoButtonDialog(
                title = dialogState.title(),
                message = dialogState.message(),
                confirmButtonText = stringResource(id = R.string.try_again),
                dismissButtonText = stringResource(id = R.string.close),
                onConfirmClick = handlers.onRetrySubscriptionClick,
                onDismissClick = handlers.onBackClick,
                onDismissRequest = handlers.onBackClick,
            )
        }

        PlanState.DialogState.LoadingPortal -> {
            QuantVaultLoadingDialog(
                text = stringResource(id = R.string.loading_portal),
            )
        }

        is PlanState.DialogState.Loading -> {
            QuantVaultLoadingDialog(text = dialogState.message())
        }

        null -> Unit
    }
}

@Composable
private fun FreeContent(
    viewState: PlanState.ViewState.Free,
    handlers: PlanHandlers,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(Modifier.height(12.dp))
        PremiumDetailsCard(
            rate = viewState.rate,
            frequency = stringResource(id = R.string.per_month),
            modifier = Modifier.standardHorizontalMargin(),
        )

        Spacer(modifier = Modifier.height(24.dp))

        QuantVaultFilledButton(
            label = stringResource(id = R.string.upgrade_now),
            onClick = handlers.onUpgradeNowClick,
            icon = rememberVectorPainter(id = R.drawable.ic_external_link),
            modifier = Modifier
                .standardHorizontalMargin()
                .fillMaxWidth()
                .testTag("UpgradeNowButton"),
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(id = R.string.stripe_checkout_footer),
            style = QuantVaultTheme.typography.bodyMedium,
            color = QuantVaultTheme.colorScheme.text.secondary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .standardHorizontalMargin()
                .testTag("StripeFooterText"),
        )

        Spacer(modifier = Modifier.height(16.dp))
        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}

@Composable
private fun PremiumDetailsCard(
    rate: String,
    frequency: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .cardStyle(
                cardStyle = CardStyle.Full,
                // Override bottom padding to account for custom
                // `QuantVaultContentBlock` vertical padding, below.
                paddingBottom = 0.dp,
            ),
    ) {
        Column(
            modifier = Modifier
                .padding(bottom = 16.dp)
                .standardHorizontalMargin(),
        ) {
            PriceRow(
                rate = rate,
                frequency = frequency,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(
                    id = R.string.unlock_premium_features,
                ),
                style = QuantVaultTheme.typography.bodyMedium,
                color = QuantVaultTheme.colorScheme.text.secondary,
            )
        }

        QuantVaultHorizontalDivider()

        val features = listOf(
            R.string.built_in_authenticator,
            R.string.emergency_access,
            R.string.secure_file_storage,
            R.string.breach_monitoring,
        )
        features.forEachIndexed { index, featureStringRes ->
            QuantVaultContentBlock(
                data = ContentBlockData(
                    headerText = stringResource(id = featureStringRes),
                    iconVectorResource = R.drawable.ic_check_mark,
                ),
                headerTextStyle = QuantVaultTheme.typography.titleMedium,
                showDivider = index != features.lastIndex,
                modifier = Modifier.padding(vertical = 8.dp),
            )
        }
    }
}

@Composable
private fun PriceRow(
    rate: String,
    frequency: String,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        Text(
            text = rate,
            style = QuantVaultTheme.typography.headlineMedium,
            color = QuantVaultTheme.colorScheme.text.primary,
            modifier = Modifier.alignByBaseline(),
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = frequency,
            style = QuantVaultTheme.typography.bodyMedium,
            color = QuantVaultTheme.colorScheme.text.secondary,
            modifier = Modifier.alignByBaseline(),
        )
    }
}

@Composable
private fun PremiumContent(
    viewState: PlanState.ViewState.Premium,
    handlers: PlanHandlers,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        SubscriptionCard(
            viewState = viewState,
            modifier = Modifier.standardHorizontalMargin(),
        )

        Spacer(modifier = Modifier.height(24.dp))

        QuantVaultFilledButton(
            label = stringResource(id = R.string.manage_plan),
            onClick = handlers.onManagePlanClick,
            icon = rememberVectorPainter(id = R.drawable.ic_external_link),
            modifier = Modifier
                .standardHorizontalMargin()
                .fillMaxWidth()
                .testTag("ManagePlanButton"),
        )

        if (viewState.showCancelButton) {
            Spacer(modifier = Modifier.height(12.dp))
            QuantVaultOutlinedButton(
                label = stringResource(id = R.string.cancel_premium),
                onClick = handlers.onCancelPremiumClick,
                icon = rememberVectorPainter(id = R.drawable.ic_external_link),
                modifier = Modifier
                    .standardHorizontalMargin()
                    .fillMaxWidth()
                    .testTag("CancelPremiumButton"),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}

@Suppress("LongMethod")
@Composable
private fun SubscriptionCard(
    viewState: PlanState.ViewState.Premium,
    modifier: Modifier = Modifier,
) {
    val rowModifier = Modifier
        .fillMaxWidth()
        .standardHorizontalMargin()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .cardStyle(
                cardStyle = CardStyle.Full,
                // Override bottom padding; the final row owns its own spacing.
                paddingBottom = 0.dp,
            ),
    ) {
        SubscriptionHeader(
            status = viewState.status,
            descriptionText = viewState.descriptionText,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .standardHorizontalMargin(),
        )

        QuantVaultHorizontalDivider()

        SubscriptionLineItem(
            label = stringResource(id = R.string.billing_amount),
            value = viewState.billingAmountText(),
            testTag = "BillingAmountRow",
            modifier = rowModifier,
        )

        QuantVaultHorizontalDivider(modifier = Modifier.padding(start = 16.dp))

        SubscriptionLineItem(
            label = stringResource(id = R.string.storage_cost),
            value = viewState.storageCostText,
            testTag = "StorageCostRow",
            modifier = rowModifier,
        )

        QuantVaultHorizontalDivider(modifier = Modifier.padding(start = 16.dp))

        SubscriptionLineItem(
            label = stringResource(id = R.string.discount),
            value = viewState.discountAmountText,
            valueColor = if (viewState.discountAmountText == "--") {
                QuantVaultTheme.colorScheme.text.primary
            } else {
                QuantVaultTheme.colorScheme.statusBadge.success.text
            },
            testTag = "DiscountRow",
            modifier = rowModifier,
        )

        QuantVaultHorizontalDivider(modifier = Modifier.padding(start = 16.dp))

        SubscriptionLineItem(
            label = stringResource(id = R.string.estimated_tax),
            value = viewState.estimatedTaxText,
            testTag = "EstimatedTaxRow",
            modifier = rowModifier,
        )
    }
}

@Composable
private fun SubscriptionHeader(
    status: PremiumSubscriptionStatus?,
    descriptionText: Text?,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(id = R.string.premium_plan_name),
                style = QuantVaultTheme.typography.titleLarge,
                color = QuantVaultTheme.colorScheme.text.primary,
            )
            status?.let {
                Spacer(modifier = Modifier.width(8.dp))
                QuantVaultStatusBadge(
                    label = stringResource(id = it.labelRes()),
                    colors = it.badgeColors(),
                )
            }
        }

        descriptionText?.let {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = it(),
                style = QuantVaultTheme.typography.bodyMedium,
                color = QuantVaultTheme.colorScheme.text.secondary,
            )
        }
    }
}

@Composable
private fun SubscriptionLineItem(
    label: String,
    value: String,
    testTag: String,
    modifier: Modifier = Modifier,
    valueColor: Color = QuantVaultTheme.colorScheme.text.primary,
) {
    Row(
        modifier = modifier
            .padding(vertical = 16.dp)
            .testTag(testTag),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = QuantVaultTheme.typography.bodyLarge,
            color = QuantVaultTheme.colorScheme.text.secondary,
        )
        Text(
            text = value,
            style = QuantVaultTheme.typography.bodyLarge,
            color = valueColor,
        )
    }
}

@Preview
@OmitFromCoverage
@Composable
private fun PlanScreenFreeAccount_preview() {
    QuantVaultTheme {
        QuantVaultScaffold {
            FreeContent(
                viewState = PlanState.ViewState.Free(
                    rate = "$1.67",
                    checkoutUrl = null,
                    isAwaitingPremiumStatus = false,
                ),
                handlers = PlanHandlers(
                    onBackClick = {},
                    onUpgradeNowClick = {},
                    onDismissError = {},
                    onRetryClick = {},
                    onRetryPricingClick = {},
                    onClosePricingErrorClick = {},
                    onCancelWaiting = {},
                    onGoBackClick = {},
                    onSyncClick = {},
                    onContinueClick = {},
                    onManagePlanClick = {},
                    onCancelPremiumClick = {},
                    onConfirmCancelClick = {},
                    onDismissCancelConfirmation = {},
                    onDismissPortalError = {},
                    onRetrySubscriptionClick = {},
                ),
            )
        }
    }
}

@Preview
@OmitFromCoverage
@Composable
private fun PlanScreenPremiumAccount_preview() {
    QuantVaultTheme {
        QuantVaultScaffold {
            PremiumContent(
                viewState = PlanState.ViewState.Premium(
                    status = PremiumSubscriptionStatus.ACTIVE,
                    descriptionText = R.string.premium_next_charge_summary.asText(
                        "$45.55",
                        "April 2, 2026",
                    ),
                    billingAmountText = R.string.billing_rate_per_year.asText("$19.80"),
                    storageCostText = "$24.00",
                    discountAmountText = "-$2.10",
                    estimatedTaxText = "$3.85",
                    nextChargeDateText = "April 2, 2026",
                    showCancelButton = true,
                ),
                handlers = PlanHandlers(
                    onBackClick = {},
                    onUpgradeNowClick = {},
                    onDismissError = {},
                    onRetryClick = {},
                    onRetryPricingClick = {},
                    onClosePricingErrorClick = {},
                    onCancelWaiting = {},
                    onGoBackClick = {},
                    onSyncClick = {},
                    onContinueClick = {},
                    onManagePlanClick = {},
                    onCancelPremiumClick = {},
                    onConfirmCancelClick = {},
                    onDismissCancelConfirmation = {},
                    onDismissPortalError = {},
                    onRetrySubscriptionClick = {},
                ),
            )
        }
    }
}






