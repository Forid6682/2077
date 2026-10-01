package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.TrafficEngine
import com.example.model.DroneStatus
import com.example.model.GameMode
import com.example.model.SignalState
import com.example.ui.theme.*

@Composable
fun GameOverlay(
    engine: TrafficEngine,
    onPauseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state = engine.gameState
    val signal = engine.signal
    val drone = engine.drone
    val level = state.currentLevel

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val honkPulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 350, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "honkPulse"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // TOP BAR
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            // Stats Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = CyberSurface.copy(alpha = 0.92f),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .border(1.dp, CyberCardBorder, RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Level / Mode Name
                Column {
                    Text(
                        text = if (state.mode == GameMode.CAMPAIGN) level.nameBn else "অনন্ত জ্যাম মোড",
                        color = NeonCyan,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (state.mode == GameMode.CAMPAIGN)
                            "গাড়ি পার: ${state.carsPassed}/${level.targetCarsToPass}"
                        else
                            "মোট পার: ${state.carsPassed} টি গাড়ি",
                        color = TextSecondary,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                // Timer & Score
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (state.mode == GameMode.CAMPAIGN) {
                        Surface(
                            color = if (state.timeRemainingSec < 15f) DangerRed.copy(alpha = 0.2f) else CyberSurfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Timer,
                                    contentDescription = "Timer",
                                    tint = if (state.timeRemainingSec < 15f) DangerRed else NeonGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = "${state.timeRemainingSec.toInt()}s",
                                    color = if (state.timeRemainingSec < 15f) DangerRed else TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    // Cyber Coins
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🪙", fontSize = 14.sp)
                        Spacer(Modifier.width(2.dp))
                        Text(
                            text = "${state.coins}",
                            color = NeonGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    // Pause Button
                    IconButton(
                        onClick = onPauseClick,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("pause_button")
                    ) {
                        Icon(
                            Icons.Default.Pause,
                            contentDescription = "Pause",
                            tint = TextPrimary
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // HONK METER (হর্ন মিটার) - KEY FEATURE
            val isCritical = state.honkMeter >= 70f
            val honkColor = when {
                state.honkMeter < 40f -> SignalGreen
                state.honkMeter < 70f -> NeonGold
                else -> DangerRed
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = if (isCritical) DarkDangerRed.copy(alpha = 0.5f * honkPulseAlpha) else CyberSurface.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .border(
                        width = if (isCritical) 2.dp else 1.dp,
                        color = if (isCritical) DangerRed else CyberCardBorder,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "📢 হর্ন মিটার (Honk Meter):",
                                color = if (isCritical) DangerRed else TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.width(6.dp))
                            if (isCritical) {
                                Text(
                                    text = "⚠️ জ্যাম বিস্ফোরক!",
                                    color = DangerRed,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                        Text(
                            text = "${state.honkMeter.toInt()}%",
                            color = honkColor,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(Modifier.height(4.dp))

                    // Progress Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(CyberSurfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction = (state.honkMeter / 100f).coerceIn(0f, 1f))
                                .fillMaxHeight()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(SignalGreen, NeonGold, DangerRed)
                                    )
                                )
                        )
                    }
                }
            }

            // DYNAMIC VIP & EMERGENCY ALERTS BANNER
            AnimatedVisibility(
                visible = state.isVipProtocolActive,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Surface(
                    color = DangerRed.copy(alpha = 0.95f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🚨", fontSize = 16.sp)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "ভিআইপি প্রোটোকল সাইরেন অন! অন্য সব রাস্তা বন্ধ থাকবে!",
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // BOTTOM CONTROLS & TACTICAL BUTTONS
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Main Signal Switch Button
            val isVertGreen = signal.verticalState == SignalState.GREEN
            Button(
                onClick = { engine.toggleSignal() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("signal_toggle_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isVertGreen) SignalGreen else NeonCyan
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isVertGreen) "🟢 উত্তর-দক্ষিণ চালু (North-South GO)" else "🟢 পূর্ব-পশ্চিম চালু (East-West GO)",
                        color = Color.Black,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(text = "🚦 ট্যাপ করে সিগন্যাল বদলান", color = Color.Black.copy(alpha = 0.75f), fontSize = 12.sp)
                }
            }

            // Tactical Devices Row: Drone, EMP, Holo-Barricade, Whistle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 1. DRONE BUTTON (ড্রোন দিয়ে জ্যাম ছাড়ানো)
                val droneReady = drone.isReady
                Button(
                    onClick = { engine.deployDrone() },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("deploy_drone_button"),
                    enabled = droneReady,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonPink,
                        disabledContainerColor = CyberSurfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(if (droneReady) "🛸" else "⏳", fontSize = 14.sp)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = if (droneReady) "ড্রোন তলব" else "${drone.cooldownRemainingSec.toInt()}s",
                            color = if (droneReady) Color.White else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                // 2. EMP SHOCKWAVE (ইএমপি ব্লাস্ট)
                val empReady = state.empCooldownSec <= 0f
                Button(
                    onClick = { engine.triggerEmpBlast() },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("emp_blast_button"),
                    enabled = empReady,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonPurple,
                        disabledContainerColor = CyberSurfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(if (empReady) "⚡" else "⏳", fontSize = 14.sp)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = if (empReady) "ইএমপি ব্লাস্ট" else "${state.empCooldownSec.toInt()}s",
                            color = if (empReady) Color.White else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                // 3. HOLO-BARRICADE (হলো-ব্যারিকেড)
                val barricadeActive = state.holoBarricadeActiveSec > 0f
                Button(
                    onClick = { engine.triggerHoloBarricade() },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("holo_barricade_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (barricadeActive) NeonCyan else CyberSurfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("🛡️", fontSize = 14.sp)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = if (barricadeActive) "ব্যারিকেড অন" else "ব্যারিকেড",
                            color = if (barricadeActive) Color.Black else TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                // 4. CYBER WHISTLE (সাইবার বাঁশি)
                Button(
                    onClick = { engine.triggerWhistle() },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("cyber_whistle_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGold),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("📢", fontSize = 18.sp)
                }
            }
        }
    }
}
