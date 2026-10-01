package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.GameStatus
import com.example.ui.*
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.Dhaka2077Theme
import com.example.viewmodel.CurrentScreen
import com.example.viewmodel.TrafficViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: TrafficViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Dhaka2077Theme {
                val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
                val coins by viewModel.coins.collectAsStateWithLifecycle()
                val highScore by viewModel.highScore.collectAsStateWithLifecycle()
                val isSoundMuted by viewModel.isSoundMuted.collectAsStateWithLifecycle()
                val upgrades by viewModel.upgrades.collectAsStateWithLifecycle()
                val showHowToPlay by viewModel.showHowToPlay.collectAsStateWithLifecycle()
                val showPauseDialog by viewModel.showPauseDialog.collectAsStateWithLifecycle()

                // Observe game tick to drive continuous 60fps canvas recomposition
                val tick by viewModel.gameTick.collectAsStateWithLifecycle()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = CyberBackground
                ) {
                    when (currentScreen) {
                        CurrentScreen.MENU -> {
                            MainMenuScreen(
                                coins = coins,
                                highScore = highScore,
                                isSoundMuted = isSoundMuted,
                                onToggleSound = { viewModel.toggleSound() },
                                onStartCampaign = { viewModel.navigateTo(CurrentScreen.LEVEL_SELECT) },
                                onStartEndless = { viewModel.startEndless() },
                                onOpenGarage = { viewModel.navigateTo(CurrentScreen.GARAGE) },
                                onOpenHowToPlay = { viewModel.setShowHowToPlay(true) }
                            )
                        }

                        CurrentScreen.LEVEL_SELECT -> {
                            BackHandler {
                                viewModel.navigateTo(CurrentScreen.MENU)
                            }
                            LevelSelectScreen(
                                coins = coins,
                                onSelectLevel = { level -> viewModel.startCampaign(level) },
                                onBack = { viewModel.navigateTo(CurrentScreen.MENU) }
                            )
                        }

                        CurrentScreen.GARAGE -> {
                            BackHandler {
                                viewModel.navigateTo(CurrentScreen.MENU)
                            }
                            GarageScreen(
                                coins = coins,
                                upgrades = upgrades,
                                onUpgradeDrone = { viewModel.upgradeDrone() },
                                onUpgradeSonic = { viewModel.upgradeSonic() },
                                onUpgradeEmp = { viewModel.upgradeEmp() },
                                onUpgradeFines = { viewModel.upgradeFines() },
                                onBack = { viewModel.navigateTo(CurrentScreen.MENU) }
                            )
                        }

                        CurrentScreen.GAME -> {
                            BackHandler {
                                viewModel.setPause(true)
                            }

                            // Pass tick to ensure recomposition on each frame
                            key(tick) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    // 2D Game Canvas
                                    GameCanvasView(
                                        engine = viewModel.engine,
                                        onTapIntersection = { viewModel.toggleSignal() },
                                        onTapVehicle = { vehicle ->
                                            viewModel.deployDrone(vehicle)
                                        }
                                    )

                                    // HUD & Controls Overlay
                                    GameOverlay(
                                        engine = viewModel.engine,
                                        onPauseClick = { viewModel.setPause(true) }
                                    )
                                }
                            }

                            // Dialogs during gameplay
                            val gameState = viewModel.engine.gameState

                            if (showPauseDialog) {
                                PauseDialog(
                                    onResume = { viewModel.setPause(false) },
                                    onRestart = { viewModel.restartCurrentLevel() },
                                    onMainMenu = { viewModel.navigateTo(CurrentScreen.MENU) }
                                )
                            }

                            if (gameState.status == GameStatus.GAME_OVER) {
                                GameOverDialog(
                                    state = gameState,
                                    onRestart = { viewModel.restartCurrentLevel() },
                                    onMainMenu = { viewModel.navigateTo(CurrentScreen.MENU) }
                                )
                            }

                            if (gameState.status == GameStatus.LEVEL_COMPLETED) {
                                LevelCompleteDialog(
                                    state = gameState,
                                    onNextLevel = { viewModel.advanceToNextLevel() },
                                    onMainMenu = { viewModel.navigateTo(CurrentScreen.MENU) }
                                )
                            }
                        }
                    }

                    // Global How To Play Dialog
                    if (showHowToPlay) {
                        HowToPlayDialog(onDismiss = { viewModel.setShowHowToPlay(false) })
                    }
                }
            }
        }
    }
}
