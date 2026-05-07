package com.quantvault.network.service

import com.quantvault.network.api.EventApi
import com.quantvault.network.model.OrganizationEventJson
import com.quantvault.network.util.toResult

/**
 * The default implementation of the [EventService].
 */
internal class EventServiceImpl(
    private val eventApi: EventApi,
) : EventService {
    override suspend fun sendOrganizationEvents(
        events: List<OrganizationEventJson>,
    ): Result<Unit> = eventApi.collectOrganizationEvents(events = events).toResult()
}





