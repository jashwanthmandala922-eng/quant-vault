package com.quantvault.app.data.autofill.accessibility.manager

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeAccessibilityEnabledManager : AccessibilityEnabledManager {

    private val mutableIsAccessibilityEnabledStateFlow = MutableStateFlow(value = false)

    override val isAccessibilityEnabledStateFlow: StateFlow<Boolean>
        get() = mutableIsAccessibilityEnabledStateFlow.asStateFlow()

    override fun refreshAccessibilityEnabledFromSettings() {
        mutableIsAccessibilityEnabledStateFlow.value = isAccessibilityEnabled
    }

    var isAccessibilityEnabled: Boolean
        get() = mutableIsAccessibilityEnabledStateFlow.value
        set(value) {
            mutableIsAccessibilityEnabledStateFlow.value = value
        }
}




