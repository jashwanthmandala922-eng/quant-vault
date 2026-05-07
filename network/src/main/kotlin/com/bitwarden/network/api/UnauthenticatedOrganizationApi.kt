package com.quantvault.network.api

import com.quantvault.network.model.NetworkResult
import com.quantvault.network.model.VerifiedOrganizationDomainSsoDetailsRequest
import com.quantvault.network.model.VerifiedOrganizationDomainSsoDetailsResponse
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Defines raw calls under the /organizations API.
 */
internal interface UnauthenticatedOrganizationApi {

    /**
     * Checks for the verified organization domains of an email for SSO purposes.
     */
    @POST("/organizations/domain/sso/verified")
    suspend fun getVerifiedOrganizationDomainsByEmail(
        @Body body: VerifiedOrganizationDomainSsoDetailsRequest,
    ): NetworkResult<VerifiedOrganizationDomainSsoDetailsResponse>
}





