package com.quantvault.network.service

import com.quantvault.network.api.AuthenticatedOrganizationApi
import com.quantvault.network.api.UnauthenticatedOrganizationApi
import com.quantvault.network.model.OrganizationAutoEnrollStatusResponseJson
import com.quantvault.network.model.OrganizationKeysResponseJson
import com.quantvault.network.model.OrganizationResetPasswordEnrollRequestJson
import com.quantvault.network.model.VerifiedOrganizationDomainSsoDetailsRequest
import com.quantvault.network.model.VerifiedOrganizationDomainSsoDetailsResponse
import com.quantvault.network.util.toResult

/**
 * Default implementation of [OrganizationService].
 */
internal class OrganizationServiceImpl(
    private val authenticatedOrganizationApi: AuthenticatedOrganizationApi,
    private val unauthenticatedOrganizationApi: UnauthenticatedOrganizationApi,
) : OrganizationService {
    override suspend fun organizationResetPasswordEnroll(
        organizationId: String,
        userId: String,
        passwordHash: String?,
        resetPasswordKey: String,
    ): Result<Unit> = authenticatedOrganizationApi
        .organizationResetPasswordEnroll(
            organizationId = organizationId,
            userId = userId,
            body = OrganizationResetPasswordEnrollRequestJson(
                passwordHash = passwordHash,
                resetPasswordKey = resetPasswordKey,
            ),
        )
        .toResult()

    override suspend fun getOrganizationAutoEnrollStatus(
        organizationIdentifier: String,
    ): Result<OrganizationAutoEnrollStatusResponseJson> = authenticatedOrganizationApi
        .getOrganizationAutoEnrollResponse(
            organizationIdentifier = organizationIdentifier,
        )
        .toResult()

    override suspend fun getOrganizationKeys(
        organizationId: String,
    ): Result<OrganizationKeysResponseJson> = authenticatedOrganizationApi
        .getOrganizationKeys(
            organizationId = organizationId,
        )
        .toResult()

    override suspend fun getVerifiedOrganizationDomainSsoDetails(
        email: String,
    ): Result<VerifiedOrganizationDomainSsoDetailsResponse> = unauthenticatedOrganizationApi
        .getVerifiedOrganizationDomainsByEmail(
            body = VerifiedOrganizationDomainSsoDetailsRequest(
                email = email,
            ),
        )
        .toResult()

    override suspend fun leaveOrganization(organizationId: String): Result<Unit> =
        authenticatedOrganizationApi
            .leaveOrganization(organizationId = organizationId)
            .toResult()

    override suspend fun revokeFromOrganization(organizationId: String): Result<Unit> =
        authenticatedOrganizationApi
            .revokeFromOrganization(organizationId = organizationId)
            .toResult()
}





