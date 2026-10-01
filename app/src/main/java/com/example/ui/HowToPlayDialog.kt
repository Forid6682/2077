package com.example.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*

@Composable
fun HowToPlayDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .border(1.dp, NeonCyan, RoundedCornerShape(18.dp)),
            shape = RoundedCornerShape(18.dp),
            color = CyberSurface
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxSize()
            ) {
                Text(
                    text = "📖 কীভাবে খেলবেন ও ঢাকার মেম",
                    color = NeonCyan,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "ট্রাফিক কনট্রোলার: ঢাকা ২০৭৭ গাইডবুক",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Spacer(Modifier.height(14.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    RuleCard(
                        emoji = "🚦",
                        title = "ডিজিটাল সিগন্যাল ও হর্ন মিটার",
                        desc = "স্ক্রিনে ট্যাপ করে লাল/সবুজ সিগন্যাল পরিবর্তন করুন। গাড়ি বেশি সময় আটকালে চালকরা হর্ন বাজাতে থাকে! হর্ন মিটার ৭০% পেরোলে স্ক্রিন কাঁপবে, ১০০% হলে মোড় ডেডলক হয়ে খেলা শেষ!"
                    )

                    RuleCard(
                        emoji = "🛸",
                        title = "ড্রোন দিয়ে জ্যাম ছাড়ানো",
                        desc = "রাস্তায় জ্যাম জমে গেলে বা বিকল গাড়ি আটকালে 'ড্রোন তলব' চাপুন। হেভি-লিফটার সাইবার ড্রোন এসে নিয়ন ট্রাক্টর বিম দিয়ে গাড়ি শূন্যে তুলে রাস্তা ফাঁকা করে দেবে!"
                    )

                    RuleCard(
                        emoji = "🚀",
                        title = "রকেট-লেগুনা (The Rocket Leguna)",
                        desc = "অত্যন্ত অধৈর্য! ২ সেকেন্ডের বেশি লাল বাতিতে দাঁড়ালেই সিগন্যাল ভেঙে হুড়মুড় করে রকেট গতিতে ছুটবে। এদের জন্য দ্রুত সিগন্যাল দিন বা ব্যারিকেড দিন!"
                    )

                    RuleCard(
                        emoji = "🚨",
                        title = "ভিআইপি প্রোটোকল সতর্কতা",
                        desc = "হঠাৎ সাইরেন বাজিয়ে কোনো নোটিশ ছাড়াই পুরো রাস্তা ব্লকেজ করবে। ভিআইপি কনভয়কে অগ্রাধিকার দিয়ে যেতে দিতে হবে, নয়তো মারাত্মক ট্রাফিক ব্যাকলগ হবে!"
                    )

                    RuleCard(
                        emoji = "🛺",
                        title = "জায়ান্ট রিকশা ও পথচারী",
                        desc = "রিকশা ধীরে চলে রাস্তা আটকে রাখে, আর পথচারীরা সিগন্যাল না মেনেই রাস্তা পার হয়! 'সাইবার বাঁশি' বাজিয়ে পথচারীদের ফুটপাতে ফিরিয়ে দিন।"
                    )

                    RuleCard(
                        emoji = "⚡",
                        title = "ইএমপি ও হলো-ব্যারিকেড",
                        desc = "ইএমপি শকওয়েভ দিলে সাথে সাথে হর্ন মিটার ৩৫% কমে যায় এবং চালকরা শান্ত হয়। হলো-ব্যারিকেড দিয়ে যেকোনো লেন সাময়িকভাবে আটকে দেওয়া যায়।"
                    )
                }

                Spacer(Modifier.height(14.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("close_how_to_play_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("বুঝতে পেরেছি! (Got it!)", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun RuleCard(emoji: String, title: String, desc: String) {
    Surface(
        color = CyberSurfaceVariant,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(emoji, fontSize = 24.sp)
            Spacer(Modifier.width(10.dp))
            Column {
                Text(title, color = NeonGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(Modifier.height(2.dp))
                Text(desc, color = TextPrimary, fontSize = 12.sp, lineHeight = 17.sp)
            }
        }
    }
}
