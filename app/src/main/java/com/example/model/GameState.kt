package com.example.model

enum class GameMode {
    CAMPAIGN,
    ENDLESS
}

enum class GameStatus {
    MENU,
    PLAYING,
    PAUSED,
    GAME_OVER,
    LEVEL_COMPLETED
}

data class MemeChatter(
    val id: Long,
    val text: String,
    val x: Float,
    val y: Float,
    var remainingLifeSec: Float = 2.0f
)

data class SonicRipple(
    val id: Long,
    val x: Float,
    val y: Float,
    var radius: Float = 10f,
    val maxRadius: Float = 120f,
    var alpha: Float = 0.8f
)

data class PoliceUpgrades(
    var droneSpeedLevel: Int = 1,     // Faster drone travel & lift
    var sonicDampenerLevel: Int = 1,  // Reduces honk meter rise rate
    var empRadiusLevel: Int = 1,      // Bigger EMP shockwave
    var cyberFineLevel: Int = 1       // Extra coins per cleared jam
) {
    fun getDroneCooldown(): Float = (8f - (droneSpeedLevel - 1) * 1.2f).coerceAtLeast(3.5f)
    fun getHonkMultiplier(): Float = (1.0f - (sonicDampenerLevel - 1) * 0.15f).coerceAtLeast(0.4f)
    fun getCoinMultiplier(): Float = 1.0f + (cyberFineLevel - 1) * 0.35f
}

data class GameState(
    var mode: GameMode = GameMode.CAMPAIGN,
    var currentLevel: GameLevel = DhakaLevels.levels[0],
    var status: GameStatus = GameStatus.MENU,
    var score: Int = 0,
    var coins: Int = 120,
    var carsPassed: Int = 0,
    var honkMeter: Float = 0f, // 0 to 100
    var timeRemainingSec: Float = 90f,
    var isVipProtocolActive: Boolean = false,
    var vipDirection: Direction = Direction.WEST_TO_EAST,
    var vipTimerSec: Float = 0f,
    var screenShakeX: Float = 0f,
    var screenShakeY: Float = 0f,
    var empBlastRadius: Float = 0f,
    var empCooldownSec: Float = 0f,
    var holoBarricadeActiveSec: Float = 0f,
    var activeChatters: List<MemeChatter> = emptyList(),
    var sonicRipples: List<SonicRipple> = emptyList(),
    var upgrades: PoliceUpgrades = PoliceUpgrades(),
    var isDroneModeSelected: Boolean = false,
    var languageBn: Boolean = true // Bengali primary, English toggle
)
