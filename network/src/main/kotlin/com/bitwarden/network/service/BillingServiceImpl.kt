package com.quantvault.network.service

import com.quantvault.network.api.AuthenticatedBillingApi
import com.quantvault.network.model.quantvaultSubscriptionResponseJson
import com.quantvault.network.model.CheckoutSessionRequestJson
import com.quantvault.network.model.CheckoutSessionResponseJson
import com.quantvault.network.model.PortalUrlResponseJson
import com.quantvault.network.model.PremiumPlanResponseJson
import com.quantvault.network.util.toResult

private const val PLATFORM = "android"

/**
 * The default implementation of the [BillingService].
 */
internal class BillingServiceImpl(
    private val authenticatedBillingApi: AuthenticatedBillingApi,
) : BillingService {

    override suspend fun createCheckoutSession(): Result<CheckoutSessionResponseJson> =
        authenticatedBillingApi
            .createCheckoutSession(
                body = CheckoutSessionRequestJson(platform = PLATFORM),
            )
            .toResult()

    override suspend fun getPortalUrl(): Result<PortalUrlResponseJson> =
        authenticatedBillingApi
            .getPortalUrl()
            .toResult()

    override suspend fun getPremiumPlan(): Result<PremiumPlanResponseJson> =
        authenticatedBillingApi
            .getPremiumPlan()
            .toResult()

    override suspend fun getSubscription(): Result<quantvaultSubscriptionResponseJson> =
        authenticatedBillingApi
            .getSubscription()
            .toResult()
}





