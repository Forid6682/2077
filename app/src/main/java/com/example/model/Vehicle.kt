package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

enum class Direction {
    NORTH_TO_SOUTH,
    SOUTH_TO_NORTH,
    WEST_TO_EAST,
    EAST_TO_WEST;

    fun isVertical(): Boolean = this == NORTH_TO_SOUTH || this == SOUTH_TO_NORTH
    fun isHorizontal(): Boolean = this == WEST_TO_EAST || this == EAST_TO_WEST
}

enum class VehicleType(
    val titleBn: String,
    val titleEn: String,
    val baseSpeed: Float,
    val length: Float,
    val width: Float,
    val impatienceThresholdSec: Float,
    val scoreValue: Int,
    val coinValue: Int
) {
    ROCKET_LEGUNA(
        titleBn = "রকেট-লেগুনা",
        titleEn = "Rocket Leguna",
        baseSpeed = 4.2f,
        length = 42f,
        width = 24f,
        impatienceThresholdSec = 2.2f,
        scoreValue = 25,
        coinValue = 15
    ),
    HOVER_RICKSHAW(
        titleBn = "জায়ান্ট রিকশা",
        titleEn = "Giant Rickshaw",
        baseSpeed = 1.8f,
        length = 30f,
        width = 22f,
        impatienceThresholdSec = 5.0f,
        scoreValue = 10,
        coinValue = 5
    ),
    MURI_TIN_BUS(
        titleBn = "মুড়ির টিন ২০৭৭",
        titleEn = "Cyber Bus 2077",
        baseSpeed = 2.4f,
        length = 68f,
        width = 28f,
        impatienceThresholdSec = 4.0f,
        scoreValue = 35,
        coinValue = 20
    ),
    HOVER_CNG(
        titleBn = "হভার সিএনজি",
        titleEn = "Hover CNG",
        baseSpeed = 3.2f,
        length = 32f,
        width = 20f,
        impatienceThresholdSec = 3.0f,
        scoreValue = 15,
        coinValue = 10
    ),
    CYBER_CAR(
        titleBn = "সাইবার কার",
        titleEn = "Cyber Sedan",
        baseSpeed = 3.4f,
        length = 38f,
        width = 22f,
        impatienceThresholdSec = 3.5f,
        scoreValue = 20,
        coinValue = 12
    ),
    VIP_CONVOY(
        titleBn = "ভিআইপি প্রোটোকল",
        titleEn = "VIP Protocol",
        baseSpeed = 5.5f,
        length = 56f,
        width = 26f,
        impatienceThresholdSec = 1.0f,
        scoreValue = 50,
        coinValue = 40
    );

    fun getColor(): Color = when (this) {
        ROCKET_LEGUNA -> LegunaColor
        HOVER_RICKSHAW -> RickshawColor
        MURI_TIN_BUS -> BusColor
        HOVER_CNG -> CngColor
        CYBER_CAR -> CyberCarColor
        VIP_CONVOY -> VipGold
    }
}

data class Vehicle(
    val id: Long,
    val type: VehicleType,
    var x: Float,
    var y: Float,
    val direction: Direction,
    var speed: Float = type.baseSpeed,
    var waitingTimeSec: Float = 0f,
    var isBrokenDown: Boolean = false,
    var isReckless: Boolean = false, // Breaking signal!
    var isLiftedByDrone: Boolean = false,
    var droneLiftProgress: Float = 0f, // 0 to 1
    var chatter: String? = null,
    var chatterTimerSec: Float = 0f,
    var isPastIntersection: Boolean = false
) {
    fun getVisualColor(): Color = when {
        isBrokenDown -> DangerRed
        isReckless -> NeonPink
        else -> type.getColor()
    }
}
