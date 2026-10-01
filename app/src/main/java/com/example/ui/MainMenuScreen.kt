package com.example.ui

import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

@Composable
fun MainMenuScreen(
    coins: Int,
    highScore: Int,
    isSoundMuted: Boolean,
    onToggleSound: () -> Unit,
    onStartCampaign: () -> Unit,
    onStartEndless: () -> Unit,
    onOpenGarage: () -> Unit,
    onOpenHowToPlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
    ) {
        // Hero Background Artwork
        Image(
            painter = painterResource(id = R.drawable.dhaka_2077_bg_1790873285716),
            contentDescription = "Dhaka 2077 Backdrop",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.55f)
                .align(Alignment.TopCenter)
        )

        // Gradient Fade Over Image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f)
                .align(Alignment.TopCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            CyberBackground.copy(alpha = 0.5f),
                            CyberBackground
                        )
                    )
                )
        )

        // Foreground Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar with Coins & Audio Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // High Score Badge
                Surface(
                    color = CyberSurface.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🏆", fontSize = 14.sp)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "সেরা স্কোর: $highScore",
                            color = NeonGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Coins Badge
                    Surface(
                        color = CyberSurface.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🪙", fontSize = 14.sp)
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "$coins",
                                color = NeonGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    // Sound Toggle Button
                    IconButton(
                        onClick = onToggleSound,
                        modifier = Modifier
                            .size(38.dp)
                            .background(CyberSurface.copy(alpha = 0.9f), RoundedCornerShape(10.dp))
                            .border(1.dp, CyberCardBorder, RoundedCornerShape(10.dp))
                            .testTag("toggle_sound_button")
                    ) {
                        Icon(
                            if (isSoundMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                            contentDescription = "Sound Toggle",
                            tint = if (isSoundMuted) TextSecondary else NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Title & Lore Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 40.dp)
            ) {
                Surface(
                    color = NeonPink.copy(alpha = 0.18f),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonPink)
                ) {
                    Text(
                        text = "⚡ DHAKA 2077 CYBER-POLICE ⚡",
                        color = NeonPink,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }

                Spacer(Modifier.height(10.dp))

                Text(
                    text = "ট্রাফিক কনট্রোলার",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "ঢাকা ২০৭৭",
                    color = NeonCyan,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    fontSize = 38.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "ড্রোন দিয়ে জ্যাম ছাড়ান • রকেট-লেগুনা আটকান • হর্ন মিটার বাঁচান",
                    color = NeonGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }

            // Main Actions Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 1. Play Campaign
                Button(
                    onClick = onStartCampaign,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("start_campaign_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("🚦", fontSize = 20.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "ক্যাম্পেইন মোড (Campaign Mode)",
                            color = Color.Black,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp
                        )
                    }
                }

                // 2. Endless Jam Mode
                Button(
                    onClick = onStartEndless,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("start_endless_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("♾️", fontSize = 18.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "অনন্ত জ্যাম মোড (Endless Jam)",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                // 3. Garage & How to Play Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onOpenGarage,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("open_garage_button"),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonGold)
                    ) {
                        Text("🛠️ গ্যারেজ", color = NeonGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    OutlinedButton(
                        onClick = onOpenHowToPlay,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("open_how_to_play_button"),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder)
                    ) {
                        Text("📖 গেমের নিয়ম", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
