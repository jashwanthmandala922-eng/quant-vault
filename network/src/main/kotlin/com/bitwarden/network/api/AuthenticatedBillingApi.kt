package com.quantvault.network.api

import com.quantvault.network.model.quantvaultSubscriptionResponseJson
import com.quantvault.network.model.CheckoutSessionRequestJson
import com.quantvault.network.model.CheckoutSessionResponseJson
import com.quantvault.network.model.NetworkResult
import com.quantvault.network.model.PortalUrlResponseJson
import com.quantvault.network.model.PremiumPlanResponseJson
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Defines raw calls under the /account/billing API with authentication applied.
 */
internal interface AuthenticatedBillingApi {

    /**
     * Creates a Stripe checkout session for Premium upgrade.
     */
    @POST("/account/billing/vnext/premium/checkout")
    suspend fun createCheckoutSession(
        @Body body: CheckoutSessionRequestJson,
    ): NetworkResult<CheckoutSessionResponseJson>

    /**
     * Creates a Stripe customer portal session for managing the Premium subscription.
     */
    @POST("/account/billing/vnext/portal-session")
    suspend fun getPortalUrl(): NetworkResult<PortalUrlResponseJson>

    /**
     * Retrieves the premium plan pricing information.
     */
    @GET("/plans/premium")
    suspend fun getPremiumPlan(): NetworkResult<PremiumPlanResponseJson>

    /**
     * Retrieves the user's premium subscription details.
     */
    @GET("/account/billing/vnext/subscription")
    suspend fun getSubscription(): NetworkResult<quantvaultSubscriptionResponseJson>
}





