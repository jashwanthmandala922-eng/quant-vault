package com.quantvault.app.data.billing.repository

import com.quantvault.core.data.util.asFailure
import com.quantvault.core.data.util.asSuccess
import com.quantvault.network.model.QuantVaultSubscriptionResponseJson
import com.quantvault.network.model.CadenceTypeJson
import com.quantvault.network.model.CartItemJson
import com.quantvault.network.model.CartJson
import com.quantvault.network.model.CheckoutSessionResponseJson
import com.quantvault.network.model.PasswordManagerCartItemsJson
import com.quantvault.network.model.PortalUrlResponseJson
import com.quantvault.network.model.PremiumPlanResponseJson
import com.quantvault.network.model.SubscriptionStatusJson
import com.quantvault.network.service.BillingService
import com.quantvault.app.data.billing.manager.PlayBillingManager
import com.quantvault.app.data.billing.repository.model.CheckoutSessionResult
import com.quantvault.app.data.billing.repository.model.CustomerPortalResult
import com.quantvault.app.data.billing.repository.model.PlanCadence
import com.quantvault.app.data.billing.repository.model.PremiumPlanPricingResult
import com.quantvault.app.data.billing.repository.model.PremiumSubscriptionStatus
import com.quantvault.app.data.billing.repository.model.SubscriptionInfo
import com.quantvault.app.data.billing.repository.model.SubscriptionResult
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class BillingRepositoryTest {

    private val mutableIsInAppBillingSupportedFlow = MutableStateFlow(false)
    private val playBillingManager = mockk<PlayBillingManager> {
        every {
            isInAppBillingSupportedFlow
        } returns mutableIsInAppBillingSupportedFlow
    }
    private val billingService = mockk<BillingService>()
    private val repository = BillingRepositoryImpl(
        playBillingManager = playBillingManager,
        billingService = billingService,
    )

    @Test
    fun `isInAppBillingSupportedFlow should delegate to PlayBillingManager`() =
        runTest {
            assertFalse(repository.isInAppBillingSupportedFlow.value)

            mutableIsInAppBillingSupportedFlow.value = true

            assertTrue(repository.isInAppBillingSupportedFlow.value)
        }

    @Test
    fun `getCheckoutSessionUrl when service returns success should return Success`() =
        runTest {
            val expectedUrl =
                "https://checkout.stripe.com/c/pay/test_session_123"
            coEvery {
                billingService.createCheckoutSession()
            } returns CheckoutSessionResponseJson(checkoutSessionUrl = expectedUrl).asSuccess()

            val result = repository.getCheckoutSessionUrl()

            assertEquals(CheckoutSessionResult.Success(url = expectedUrl), result)
        }

    @Test
    fun `getCheckoutSessionUrl when service returns failure should return Error`() =
        runTest {
            val exception = RuntimeException("Network error")
            coEvery {
                billingService.createCheckoutSession()
            } returns exception.asFailure()

            val result = repository.getCheckoutSessionUrl()

            assertEquals(
                CheckoutSessionResult.Error(error = exception),
                result,
            )
        }

    @Test
    fun `getPortalUrl when service returns success should return Success`() =
        runTest {
            val expectedUrl =
                "https://billing.stripe.com/p/session/test_portal_456"
            coEvery {
                billingService.getPortalUrl()
            } returns PortalUrlResponseJson(url = expectedUrl).asSuccess()

            val result = repository.getPortalUrl()

            assertEquals(CustomerPortalResult.Success(url = expectedUrl), result)
        }

    @Test
    fun `getPortalUrl when service returns failure should return Error`() =
        runTest {
            val exception = RuntimeException("Network error")
            coEvery {
                billingService.getPortalUrl()
            } returns exception.asFailure()

            val result = repository.getPortalUrl()

            assertEquals(
                CustomerPortalResult.Error(error = exception),
                result,
            )
        }

    @Test
    fun `getPremiumPlanPricing when service returns success should return formatted pricing`() =
        runTest {
            coEvery {
                billingService.getPremiumPlan()
            } returns PremiumPlanResponseJson(
                name = "Premium",
                legacyYear = null,
                isAvailable = true,
                seat = PremiumPlanResponseJson.PurchasableJson(
                    stripePriceId = "premium-annually-2026",
                    price = ANNUAL_PRICE,
                    provided = 0,
                ),
                storage = PremiumPlanResponseJson.PurchasableJson(
                    stripePriceId = "personal-storage-gb-annually",
                    price = 4.00,
                    provided = 5,
                ),
            ).asSuccess()

            val result = repository.getPremiumPlanPricing()

            assertEquals(
                PremiumPlanPricingResult.Success(
                    annualPrice = ANNUAL_PRICE,
                ),
                result,
            )
        }

    @Test
    fun `getPremiumPlanPricing when service returns failure should return Error`() =
        runTest {
            val exception = RuntimeException("Network error")
            coEvery {
                billingService.getPremiumPlan()
            } returns exception.asFailure()

            val result = repository.getPremiumPlanPricing()

            assertEquals(
                PremiumPlanPricingResult.Error(error = exception),
                result,
            )
        }

    @Test
    fun `getSubscription when service returns success should return Success`() =
        runTest {
            coEvery {
                billingService.getSubscription()
            } returns ACTIVE_SUBSCRIPTION_RESPONSE.asSuccess()

            val result = repository.getSubscription()

            assertEquals(
                SubscriptionResult.Success(
                    subscription = SubscriptionInfo(
                        status = PremiumSubscriptionStatus.ACTIVE,
                        cadence = PlanCadence.ANNUALLY,
                        seatsCost = BigDecimal("19.80"),
                        storageCost = null,
                        discountAmount = null,
                        estimatedTax = BigDecimal.ZERO,
                        nextChargeTotal = BigDecimal("19.80"),
                        nextCharge = null,
                        canceledDate = null,
                        suspensionDate = null,
                        gracePeriodDays = null,
                    ),
                ),
                result,
            )
        }

    @Test
    fun `getSubscription when service returns failure should return Error`() =
        runTest {
            val exception = RuntimeException("Network error")
            coEvery {
                billingService.getSubscription()
            } returns exception.asFailure()

            val result = repository.getSubscription()

            assertEquals(
                SubscriptionResult.Error(error = exception),
                result,
            )
        }
}

private const val ANNUAL_PRICE = 19.99

private val ACTIVE_SUBSCRIPTION_RESPONSE = QuantVaultSubscriptionResponseJson(
    status = SubscriptionStatusJson.ACTIVE,
    cart = CartJson(
        passwordManager = PasswordManagerCartItemsJson(
            seats = CartItemJson(
                translationKey = "premiumMembership",
                quantity = 1,
                cost = BigDecimal("19.80"),
                discount = null,
            ),
            additionalStorage = null,
        ),
        secretsManager = null,
        cadence = CadenceTypeJson.ANNUALLY,
        discount = null,
        estimatedTax = BigDecimal.ZERO,
    ),
    storage = null,
    cancelAt = null,
    canceled = null,
    nextCharge = null,
    suspension = null,
    gracePeriod = null,
)




