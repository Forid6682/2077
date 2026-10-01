package com.example.model

enum class PedestrianCrossingAxis {
    HORIZONTAL, // Crossing vertical road
    VERTICAL    // Crossing horizontal road
}

data class Pedestrian(
    val id: Long,
    var x: Float,
    var y: Float,
    val axis: PedestrianCrossingAxis,
    val direction: Float, // +1 or -1
    val speed: Float = 1.2f,
    var isScared: Boolean = false,
    var dialogue: String = "রাস্তা পার হইতাছি!"
)
