package com.quantvault.network.api

import com.quantvault.network.model.NetworkResult
import com.quantvault.network.model.OrganizationAutoEnrollStatusResponseJson
import com.quantvault.network.model.OrganizationKeysResponseJson
import com.quantvault.network.model.OrganizationResetPasswordEnrollRequestJson
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Defines raw calls under the authenticated /organizations API.
 */
internal interface AuthenticatedOrganizationApi {
    /**
     * Enrolls this user in the organization's password reset.
     */
    @PUT("/organizations/{orgId}/users/{userId}/reset-password-enrollment")
    suspend fun organizationResetPasswordEnroll(
        @Path("orgId") organizationId: String,
        @Path("userId") userId: String,
        @Body body: OrganizationResetPasswordEnrollRequestJson,
    ): NetworkResult<Unit>

    /**
     * Checks whether this organization auto enrolls users in password reset.
     */
    @GET("/organizations/{identifier}/auto-enroll-status")
    suspend fun getOrganizationAutoEnrollResponse(
        @Path("identifier") organizationIdentifier: String,
    ): NetworkResult<OrganizationAutoEnrollStatusResponseJson>

    /**
     * Gets the public and private keys for this organization.
     */
    @GET("/organizations/{id}/keys")
    suspend fun getOrganizationKeys(
        @Path("id") organizationId: String,
    ): NetworkResult<OrganizationKeysResponseJson>

    /**
     * Leaves the organization
     */
    @POST("/organizations/{id}/leave")
    suspend fun leaveOrganization(
        @Path("id") organizationId: String,
    ): NetworkResult<Unit>

    /**
     * Revokes self from organization
     */
    @PUT("/organizations/{orgId}/users/revoke-self")
    suspend fun revokeFromOrganization(
        @Path("orgId") organizationId: String,
    ): NetworkResult<Unit>
}





