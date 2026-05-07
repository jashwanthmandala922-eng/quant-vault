package com.quantvault.data.datasource.disk.util

import com.quantvault.core.data.repository.util.bufferedMutableSharedFlow
import com.quantvault.data.datasource.disk.FlightRecorderDiskSource
import com.quantvault.data.datasource.disk.model.FlightRecorderDataSet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onSubscription
import org.junit.jupiter.api.Assertions.assertEquals

class FakeFlightRecorderDiskSource : FlightRecorderDiskSource {
    private val mutableFlightRecorderDataFlow =
        bufferedMutableSharedFlow<FlightRecorderDataSet?>(replay = 1)

    private var storedFlightRecorderData: FlightRecorderDataSet? = null

    override var flightRecorderData: FlightRecorderDataSet?
        get() = storedFlightRecorderData
        set(value) {
            storedFlightRecorderData = value
            mutableFlightRecorderDataFlow.tryEmit(value)
        }

    override val flightRecorderDataFlow: Flow<FlightRecorderDataSet?>
        get() = mutableFlightRecorderDataFlow.onSubscription { emit(storedFlightRecorderData) }

    /**
     * Asserts that the stored [FlightRecorderDataSet] matches the [expected] one.
     */
    fun assertFlightRecorderData(expected: FlightRecorderDataSet) {
        assertEquals(expected, storedFlightRecorderData)
    }
}





