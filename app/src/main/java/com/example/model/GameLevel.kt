package com.example.model

data class GameLevel(
    val id: Int,
    val nameBn: String,
    val nameEn: String,
    val descriptionBn: String,
    val targetCarsToPass: Int,
    val timeLimitSec: Int,
    val spawnRateSec: Float,
    val legunaProbability: Float,
    val rickshawProbability: Float,
    val busBreakdownChance: Float,
    val vipProtocolIntervalSec: Float,
    val backgroundTheme: String
)

object DhakaLevels {
    val levels = listOf(
        GameLevel(
            id = 1,
            nameBn = "ফার্মগেট ইন্টারসেকশন",
            nameEn = "Farmgate Cyber Junction",
            descriptionBn = "ঢাকার সবচেয়ে ব্যস্ত মোড়। রকেট-লেগুনার সিগন্যাল ভাঙা ঠেকান এবং ট্রাফিকের গতি ধরে রাখুন!",
            targetCarsToPass = 25,
            timeLimitSec = 90,
            spawnRateSec = 2.4f,
            legunaProbability = 0.35f,
            rickshawProbability = 0.25f,
            busBreakdownChance = 0.05f,
            vipProtocolIntervalSec = 35f,
            backgroundTheme = "farmgate"
        ),
        GameLevel(
            id = 2,
            nameBn = "শাহবাগ চত্বর",
            nameEn = "Shahbagh Hologram Circle",
            descriptionBn = "হাজারো জায়ান্ট রিকশা এবং নির্ভীক পথচারী। হর্ন মিটার বাড়লে ঢাকা কাঁপবে!",
            targetCarsToPass = 35,
            timeLimitSec = 110,
            spawnRateSec = 2.1f,
            legunaProbability = 0.20f,
            rickshawProbability = 0.50f,
            busBreakdownChance = 0.08f,
            vipProtocolIntervalSec = 40f,
            backgroundTheme = "shahbagh"
        ),
        GameLevel(
            id = 3,
            nameBn = "কুড়িল ফ্লাইওভার ২০৭৭",
            nameEn = "Kuril Hyper-Flyover",
            descriptionBn = "অতি উচ্চ গতি! হঠাৎ ভিআইপি প্রোটোকল সাইরেন বাজলে সব গাড়ি আটকাতে হবে!",
            targetCarsToPass = 45,
            timeLimitSec = 120,
            spawnRateSec = 1.8f,
            legunaProbability = 0.40f,
            rickshawProbability = 0.15f,
            busBreakdownChance = 0.10f,
            vipProtocolIntervalSec = 25f,
            backgroundTheme = "kuril"
        ),
        GameLevel(
            id = 4,
            nameBn = "মিরপুর ১০ গোলচত্বর",
            nameEn = "Mirpur 10 Nexus",
            descriptionBn = "মুড়ির টিন বাসের ধোঁয়া ও বিকল ইঞ্জিন! ড্রোন দিয়ে দ্রুত বিকল বাস সরিয়ে জ্যাম মুক্ত রাখুন।",
            targetCarsToPass = 50,
            timeLimitSec = 130,
            spawnRateSec = 1.7f,
            legunaProbability = 0.30f,
            rickshawProbability = 0.30f,
            busBreakdownChance = 0.22f,
            vipProtocolIntervalSec = 30f,
            backgroundTheme = "mirpur"
        ),
        GameLevel(
            id = 5,
            nameBn = "গুলশান ২ সাইবার স্কয়ার",
            nameEn = "Gulshan 2 Cyber Square",
            descriptionBn = "ভিআইপি কনভয় এবং অত্যাধুনিক সাইবার গাড়ির মিছিল। ট্রাফিক সামলানোর সর্বোচ্চ পরীক্ষা!",
            targetCarsToPass = 65,
            timeLimitSec = 150,
            spawnRateSec = 1.4f,
            legunaProbability = 0.25f,
            rickshawProbability = 0.10f,
            busBreakdownChance = 0.12f,
            vipProtocolIntervalSec = 20f,
            backgroundTheme = "gulshan"
        )
    )
}
