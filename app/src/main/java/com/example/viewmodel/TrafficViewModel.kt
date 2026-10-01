package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundSystem
import com.example.game.TrafficEngine
import com.example.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class CurrentScreen {
    MENU,
    LEVEL_SELECT,
    GAME,
    GARAGE
}

class TrafficViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("dhaka_2077_prefs", Context.MODE_PRIVATE)
    val soundSystem = SoundSystem(application)
    val engine = TrafficEngine(soundSystem)

    private val _currentScreen = MutableStateFlow(CurrentScreen.MENU)
    val currentScreen: StateFlow<CurrentScreen> = _currentScreen.asStateFlow()

    private val _coins = MutableStateFlow(prefs.getInt("saved_coins", 150))
    val coins: StateFlow<Int> = _coins.asStateFlow()

    private val _highScore = MutableStateFlow(prefs.getInt("high_score", 0))
    val highScore: StateFlow<Int> = _highScore.asStateFlow()

    private val _isSoundMuted = MutableStateFlow(prefs.getBoolean("sound_muted", false))
    val isSoundMuted: StateFlow<Boolean> = _isSoundMuted.asStateFlow()

    private val _showHowToPlay = MutableStateFlow(false)
    val showHowToPlay: StateFlow<Boolean> = _showHowToPlay.asStateFlow()

    private val _showPauseDialog = MutableStateFlow(false)
    val showPauseDialog: StateFlow<Boolean> = _showPauseDialog.asStateFlow()

    private val _upgrades = MutableStateFlow(
        PoliceUpgrades(
            droneSpeedLevel = prefs.getInt("upg_drone", 1),
            sonicDampenerLevel = prefs.getInt("upg_sonic", 1),
            empRadiusLevel = prefs.getInt("upg_emp", 1),
            cyberFineLevel = prefs.getInt("upg_fine", 1)
        )
    )
    val upgrades: StateFlow<PoliceUpgrades> = _upgrades.asStateFlow()

    // Observable tick to trigger recomposition of GameCanvas
    private val _gameTick = MutableStateFlow(0L)
    val gameTick: StateFlow<Long> = _gameTick.asStateFlow()

    private var gameLoopJob: Job? = null

    init {
        soundSystem.isMuted = _isSoundMuted.value
        engine.gameState.upgrades = _upgrades.value
        engine.gameState.coins = _coins.value
    }

    fun navigateTo(screen: CurrentScreen) {
        _currentScreen.value = screen
        if (screen != CurrentScreen.GAME) {
            stopGameLoop()
        }
    }

    fun startCampaign(level: GameLevel) {
        engine.gameState.coins = _coins.value
        engine.gameState.upgrades = _upgrades.value
        engine.startLevel(level, GameMode.CAMPAIGN)
        _showPauseDialog.value = false
        _currentScreen.value = CurrentScreen.GAME
        startGameLoop()
    }

    fun startEndless() {
        val endlessLevel = GameLevel(
            id = 99,
            nameBn = "অনন্ত ঢাকা জ্যাম ২০৭৭",
            nameEn = "Endless Dhaka Jam",
            descriptionBn = "যতক্ষণ সম্ভব জ্যাম সামলে টিকে থাকুন!",
            targetCarsToPass = 9999,
            timeLimitSec = 9999,
            spawnRateSec = 1.6f,
            legunaProbability = 0.35f,
            rickshawProbability = 0.35f,
            busBreakdownChance = 0.15f,
            vipProtocolIntervalSec = 22f,
            backgroundTheme = "endless"
        )
        engine.gameState.coins = _coins.value
        engine.gameState.upgrades = _upgrades.value
        engine.startLevel(endlessLevel, GameMode.ENDLESS)
        _showPauseDialog.value = false
        _currentScreen.value = CurrentScreen.GAME
        startGameLoop()
    }

    private fun startGameLoop() {
        gameLoopJob?.cancel()
        gameLoopJob = viewModelScope.launch(Dispatchers.Default) {
            var lastTimeNanos = System.nanoTime()
            while (isActive) {
                val nowNanos = System.nanoTime()
                val deltaSec = ((nowNanos - lastTimeNanos) / 1_000_000_000f).coerceIn(0.005f, 0.05f)
                lastTimeNanos = nowNanos

                if (!_showPauseDialog.value) {
                    engine.update(deltaSec)

                    // Sync coins and score
                    if (engine.gameState.coins != _coins.value) {
                        _coins.value = engine.gameState.coins
                        prefs.edit().putInt("saved_coins", _coins.value).apply()
                    }
                    if (engine.gameState.score > _highScore.value) {
                        _highScore.value = engine.gameState.score
                        prefs.edit().putInt("high_score", _highScore.value).apply()
                    }
                }

                _gameTick.value = System.currentTimeMillis()
                delay(16) // ~60fps
            }
        }
    }

    private fun stopGameLoop() {
        gameLoopJob?.cancel()
        gameLoopJob = null
    }

    fun toggleSignal() {
        engine.toggleSignal()
    }

    fun deployDrone(target: Vehicle? = null) {
        engine.deployDrone(target)
    }

    fun triggerEmp() {
        engine.triggerEmpBlast()
    }

    fun triggerHoloBarricade() {
        engine.triggerHoloBarricade()
    }

    fun triggerWhistle() {
        engine.triggerWhistle()
    }

    fun toggleSound() {
        val newMuted = !_isSoundMuted.value
        _isSoundMuted.value = newMuted
        soundSystem.isMuted = newMuted
        prefs.edit().putBoolean("sound_muted", newMuted).apply()
    }

    fun setPause(isPaused: Boolean) {
        _showPauseDialog.value = isPaused
    }

    fun setShowHowToPlay(show: Boolean) {
        _showHowToPlay.value = show
    }

    fun restartCurrentLevel() {
        _showPauseDialog.value = false
        engine.startLevel(engine.gameState.currentLevel, engine.gameState.mode)
        startGameLoop()
    }

    fun advanceToNextLevel() {
        val currentId = engine.gameState.currentLevel.id
        val nextLevel = DhakaLevels.levels.find { it.id == currentId + 1 }
        if (nextLevel != null) {
            startCampaign(nextLevel)
        } else {
            // Completed all levels! Reward bonus coins
            _coins.value += 200
            prefs.edit().putInt("saved_coins", _coins.value).apply()
            navigateTo(CurrentScreen.LEVEL_SELECT)
        }
    }

    // Upgrades
    fun upgradeDrone() {
        val cost = _upgrades.value.droneSpeedLevel * 80
        if (_coins.value >= cost && _upgrades.value.droneSpeedLevel < 5) {
            _coins.value -= cost
            val newUpg = _upgrades.value.copy(droneSpeedLevel = _upgrades.value.droneSpeedLevel + 1)
            _upgrades.value = newUpg
            saveUpgrades(newUpg)
            soundSystem.playCoinChime()
        }
    }

    fun upgradeSonic() {
        val cost = _upgrades.value.sonicDampenerLevel * 70
        if (_coins.value >= cost && _upgrades.value.sonicDampenerLevel < 5) {
            _coins.value -= cost
            val newUpg = _upgrades.value.copy(sonicDampenerLevel = _upgrades.value.sonicDampenerLevel + 1)
            _upgrades.value = newUpg
            saveUpgrades(newUpg)
            soundSystem.playCoinChime()
        }
    }

    fun upgradeEmp() {
        val cost = _upgrades.value.empRadiusLevel * 90
        if (_coins.value >= cost && _upgrades.value.empRadiusLevel < 5) {
            _coins.value -= cost
            val newUpg = _upgrades.value.copy(empRadiusLevel = _upgrades.value.empRadiusLevel + 1)
            _upgrades.value = newUpg
            saveUpgrades(newUpg)
            soundSystem.playCoinChime()
        }
    }

    fun upgradeFines() {
        val cost = _upgrades.value.cyberFineLevel * 60
        if (_coins.value >= cost && _upgrades.value.cyberFineLevel < 5) {
            _coins.value -= cost
            val newUpg = _upgrades.value.copy(cyberFineLevel = _upgrades.value.cyberFineLevel + 1)
            _upgrades.value = newUpg
            saveUpgrades(newUpg)
            soundSystem.playCoinChime()
        }
    }

    private fun saveUpgrades(upgrades: PoliceUpgrades) {
        engine.gameState.upgrades = upgrades
        prefs.edit()
            .putInt("saved_coins", _coins.value)
            .putInt("upg_drone", upgrades.droneSpeedLevel)
            .putInt("upg_sonic", upgrades.sonicDampenerLevel)
            .putInt("upg_emp", upgrades.empRadiusLevel)
            .putInt("upg_fine", upgrades.cyberFineLevel)
            .apply()
    }

    override fun onCleared() {
        super.onCleared()
        stopGameLoop()
    }
}
