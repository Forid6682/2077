package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PoliceUpgrades
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GarageScreen(
    coins: Int,
    upgrades: PoliceUpgrades,
    onUpgradeDrone: () -> Unit,
    onUpgradeSonic: () -> Unit,
    onUpgradeEmp: () -> Unit,
    onUpgradeFines: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "সাইবার পুলিশ গ্যারেজ",
                        color = NeonCyan,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("garage_back_button")) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    Row(
                        modifier = Modifier.padding(end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🪙", fontSize = 16.sp)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "$coins",
                            color = NeonGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CyberBackground)
            )
        },
        containerColor = CyberBackground,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "ট্রাফিক কন্ট্রোলে ব্যবহৃত গ্যাজেট ও সাইবার ডিভাইস আপগ্রেড করুন:",
                color = TextSecondary,
                fontSize = 13.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // 1. Drone Upgrade
            val droneCost = upgrades.droneSpeedLevel * 80
            UpgradeCard(
                emoji = "🛸",
                title = "হেভি-লিফটার ড্রোন গতি ও কুলডাউন",
                desc = "ড্রোন আরও দ্রুত জ্যামের উপর উড়ে যাবে এবং কুলডাউন সময় কমে ${(8f - (upgrades.droneSpeedLevel - 1) * 1.2f).toInt()}s হবে।",
                currentLevel = upgrades.droneSpeedLevel,
                maxLevel = 5,
                cost = droneCost,
                canAfford = coins >= droneCost,
                onUpgrade = onUpgradeDrone,
                testTag = "upgrade_drone_button"
            )

            // 2. Sonic Dampener
            val sonicCost = upgrades.sonicDampenerLevel * 70
            UpgradeCard(
                emoji = "🔇",
                title = "সনিক হর্ন ড্যাম্পেনার",
                desc = "অসহ্য হর্নের আওয়াজ ও তীব্রতা হ্রাস করে, যার ফলে হর্ন মিটার ${((1f - upgrades.getHonkMultiplier()) * 100).toInt()}% কম গতিতে বাড়বে।",
                currentLevel = upgrades.sonicDampenerLevel,
                maxLevel = 5,
                cost = sonicCost,
                canAfford = coins >= sonicCost,
                onUpgrade = onUpgradeSonic,
                testTag = "upgrade_sonic_button"
            )

            // 3. EMP Blast
            val empCost = upgrades.empRadiusLevel * 90
            UpgradeCard(
                emoji = "⚡",
                title = "ইএমপি শকওয়েভ চার্জার",
                desc = "শক্তিশালী ইলেকট্রোম্যাগনেটিক শকওয়েভ ছড়িয়ে এক নিমেষে মোড়ের সব চালককে শান্ত করে ও লাল বাতি ভাঙা রোধ করে।",
                currentLevel = upgrades.empRadiusLevel,
                maxLevel = 5,
                cost = empCost,
                canAfford = coins >= empCost,
                onUpgrade = onUpgradeEmp,
                testTag = "upgrade_emp_button"
            )

            // 4. Fine Collector
            val finesCost = upgrades.cyberFineLevel * 60
            UpgradeCard(
                emoji = "💳",
                title = "সাইবার ফাইন কালেক্টর",
                desc = "প্রতিটি সফলভাবে পার হওয়া বা ড্রোন দিয়ে ক্লিয়ার করা গাড়ি থেকে ${(upgrades.getCoinMultiplier() * 100 - 100).toInt()}% বেশি কয়েন আদায় করুন।",
                currentLevel = upgrades.cyberFineLevel,
                maxLevel = 5,
                cost = finesCost,
                canAfford = coins >= finesCost,
                onUpgrade = onUpgradeFines,
                testTag = "upgrade_fines_button"
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun UpgradeCard(
    emoji: String,
    title: String,
    desc: String,
    currentLevel: Int,
    maxLevel: Int,
    cost: Int,
    canAfford: Boolean,
    onUpgrade: () -> Unit,
    testTag: String
) {
    val isMaxed = currentLevel >= maxLevel

    Surface(
        color = CyberSurface,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(emoji, fontSize = 26.sp)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(title, color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("লেভেল $currentLevel / $maxLevel", color = NeonGold, fontSize = 12.sp)
                    }
                }

                if (isMaxed) {
                    Surface(
                        color = NeonGreen.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "MAXED",
                            color = NeonGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                } else {
                    Button(
                        onClick = onUpgrade,
                        enabled = canAfford,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonGold,
                            disabledContainerColor = CyberSurfaceVariant
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag(testTag)
                    ) {
                        Text("🪙 $cost", color = if (canAfford) Color.Black else TextSecondary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(desc, color = TextSecondary, fontSize = 12.sp, lineHeight = 17.sp)

            Spacer(Modifier.height(8.dp))

            // Level Progress Pips
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                for (i in 1..maxLevel) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .background(
                                color = if (i <= currentLevel) NeonCyan else CyberSurfaceVariant,
                                shape = RoundedCornerShape(3.dp)
                            )
                    )
                }
            }
        }
    }
}
