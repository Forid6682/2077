package com.example.model

enum class SignalState {
    GREEN,
    YELLOW,
    RED
}

data class IntersectionSignal(
    var verticalState: SignalState = SignalState.GREEN,
    var horizontalState: SignalState = SignalState.RED,
    var timeInCurrentStateSec: Float = 0f,
    var isEmergencyVipLocked: Boolean = false
) {
    fun toggle() {
        if (isEmergencyVipLocked) return
        if (verticalState == SignalState.GREEN) {
            verticalState = SignalState.RED
            horizontalState = SignalState.GREEN
        } else {
            verticalState = SignalState.GREEN
            horizontalState = SignalState.RED
        }
        timeInCurrentStateSec = 0f
    }

    fun setVerticalGreen() {
        if (isEmergencyVipLocked) return
        verticalState = SignalState.GREEN
        horizontalState = SignalState.RED
        timeInCurrentStateSec = 0f
    }

    fun setHorizontalGreen() {
        if (isEmergencyVipLocked) return
        verticalState = SignalState.RED
        horizontalState = SignalState.GREEN
        timeInCurrentStateSec = 0f
    }
}
