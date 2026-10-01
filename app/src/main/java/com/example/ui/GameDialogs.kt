package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.GameState
import com.example.ui.theme.*

@Composable
fun GameOverDialog(
    state: GameState,
    onRestart: () -> Unit,
    onMainMenu: () -> Unit
) {
    Dialog(onDismissRequest = onRestart) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, DangerRed, RoundedCornerShape(18.dp)),
            shape = RoundedCornerShape(18.dp),
            color = CyberSurface
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "💥", fontSize = 44.sp)
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "জ্যামে ঢাকা অচল!",
                    color = DangerRed,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "হর্ন মিটার ১০০% ছাড়িয়ে গেছে! পুরো মোড় ডেডলক হয়ে গেছে।",
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 6.dp)
                )

                Spacer(Modifier.height(14.dp))

                // Stats Box
                Surface(
                    color = CyberSurfaceVariant,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("গাড়ি পার", color = TextSecondary, fontSize = 11.sp)
                            Text("${state.carsPassed}", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("অর্জিত স্কোর", color = TextSecondary, fontSize = 11.sp)
                            Text("${state.score}", color = NeonGold, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("সাইবার কয়েন", color = TextSecondary, fontSize = 11.sp)
                            Text("🪙 ${state.coins}", color = NeonPink, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = onRestart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("retry_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Retry", tint = Color.Black)
                    Spacer(Modifier.width(6.dp))
                    Text("আবার খেলুন", color = Color.Black, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onMainMenu,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("game_over_menu_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Home, contentDescription = "Home", tint = TextPrimary)
                    Spacer(Modifier.width(6.dp))
                    Text("মূল মেনু", color = TextPrimary)
                }
            }
        }
    }
}

@Composable
fun LevelCompleteDialog(
    state: GameState,
    onNextLevel: () -> Unit,
    onMainMenu: () -> Unit
) {
    Dialog(onDismissRequest = onNextLevel) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, NeonGreen, RoundedCornerShape(18.dp)),
            shape = RoundedCornerShape(18.dp),
            color = CyberSurface
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "🏆", fontSize = 44.sp)
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "লেভেল সম্পন্ন!",
                    color = SignalGreen,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "সাবাশ সার্জেন্ট! মোড়টি সফলভাবে জ্যামমুক্ত রাখা হয়েছে।",
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 6.dp)
                )

                // 3 Stars rating
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(vertical = 10.dp)
                ) {
                    repeat(3) {
                        Text("⭐", fontSize = 28.sp)
                    }
                }

                Surface(
                    color = CyberSurfaceVariant,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("গাড়ি পার", color = TextSecondary, fontSize = 11.sp)
                            Text("${state.carsPassed}", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("বোনাস কয়েন", color = TextSecondary, fontSize = 11.sp)
                            Text("+50 🪙", color = NeonGold, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                    }
                }

                Spacer(Modifier.height(18.dp))

                Button(
                    onClick = onNextLevel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("next_level_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("পরবর্তী লেভেল", color = Color.Black, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(6.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = "Next", tint = Color.Black)
                }

                Spacer(Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onMainMenu,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("complete_menu_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Home, contentDescription = "Home", tint = TextPrimary)
                    Spacer(Modifier.width(6.dp))
                    Text("মূল মেনু", color = TextPrimary)
                }
            }
        }
    }
}

@Composable
fun PauseDialog(
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onMainMenu: () -> Unit
) {
    Dialog(onDismissRequest = onResume) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CyberCardBorder, RoundedCornerShape(18.dp)),
            shape = RoundedCornerShape(18.dp),
            color = CyberSurface
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "⏸️ খেলা স্থগিত",
                    color = NeonCyan,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = onResume,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("resume_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("চালিয়ে যান (Resume)", color = Color.Black, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onRestart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("restart_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("পুনরায় শুরু করুন (Restart)", color = TextPrimary)
                }

                Spacer(Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onMainMenu,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("menu_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("মূল মেনু (Main Menu)", color = TextSecondary)
                }
            }
        }
    }
}
