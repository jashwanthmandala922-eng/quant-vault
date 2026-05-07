package com.quantvault.network.service

import com.quantvault.network.model.OrganizationEventJson

/**
 * Provides an API for submitting events.
 */
interface EventService {
    /**
     * Attempts to submit all of the given organizations events.
     */
    suspend fun sendOrganizationEvents(events: List<OrganizationEventJson>): Result<Unit>
}





