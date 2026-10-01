package com.example.model

enum class DroneStatus {
    READY,
    DISPATCHED,
    LIFTING,
    RECHARGING
}

data class CyberDrone(
    var status: DroneStatus = DroneStatus.READY,
    var x: Float = 500f,
    var y: Float = 100f,
    var targetVehicleId: Long? = null,
    var targetX: Float = 500f,
    var targetY: Float = 500f,
    var liftTimer: Float = 0f,
    var cooldownRemainingSec: Float = 0f,
    val maxCooldownSec: Float = 8f
) {
    val isReady: Boolean get() = status == DroneStatus.READY
}
