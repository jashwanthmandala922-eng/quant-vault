package com.quantvault.network.service

import com.quantvault.network.api.EventApi
import com.quantvault.network.base.BaseServiceTest
import com.quantvault.network.model.OrganizationEventJson
import com.quantvault.network.model.OrganizationEventType
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import retrofit2.create
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class EventServiceTest : BaseServiceTest() {
    private val fixedClock: Clock = Clock.fixed(
        Instant.parse("2023-10-27T12:00:00Z"),
        ZoneOffset.UTC,
    )

    private val eventApi: EventApi = retrofit.create()

    private val eventService: EventService = EventServiceImpl(
        eventApi = eventApi,
    )

    @Test
    fun `sendOrganizationEvents should return the correct response`() = runTest {
        server.enqueue(MockResponse())
        val result = eventService.sendOrganizationEvents(
            events = listOf(
                OrganizationEventJson(
                    type = OrganizationEventType.CIPHER_CREATED,
                    cipherId = "cipher-id",
                    date = Instant.now(fixedClock),
                    organizationId = null,
                ),
            ),
        )
        assertEquals(Unit, result.getOrThrow())
    }
}





