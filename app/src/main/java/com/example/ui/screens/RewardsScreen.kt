package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.RewardedAdDialog
import com.example.ui.theme.*
import com.example.viewmodel.FinancialViewModel

@Composable
fun RewardsScreen(
    viewModel: FinancialViewModel
) {
    val userPoints by viewModel.adminSettings.userPoints.collectAsStateWithLifecycle()
    val dailyStreak by viewModel.adminSettings.dailyStreak.collectAsStateWithLifecycle()
    val rewardedAdId by viewModel.adminSettings.rewardedAdId.collectAsStateWithLifecycle()
    val userReferralCode by viewModel.adminSettings.userReferralCode.collectAsStateWithLifecycle()
    val referralReward by viewModel.adminSettings.referralRewardPoints.collectAsStateWithLifecycle()
    val referralsCount by viewModel.adminSettings.referralsCount.collectAsStateWithLifecycle()
    val todayTxCount by viewModel.adminSettings.todayTransactionsCount.collectAsStateWithLifecycle()
    val minTxRequired by viewModel.adminSettings.minDailyTransactions.collectAsStateWithLifecycle()

    val context = androidx.compose.ui.platform.LocalContext.current

    var showRewardedAdDialog by remember { mutableStateOf(false) }
    var checkInMessage by remember { mutableStateOf<String?>(null) }
    var friendReferralCodeInput by remember { mutableStateOf("") }
    var referralStatusMessage by remember { mutableStateOf<String?>(null) }

    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
        ) {
            // Points Wallet Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("rewards_wallet_card"),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFFF59E0B), Color(0xFFD97706))
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Stars,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "محفظة النقاط والمكافآت",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.Black.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "المستوى الذهبي ⭐",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "$userPoints",
                                color = Color.White,
                                fontSize = 38.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "نقطة ولاء واستثمار نشطة",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "سلسلة الحضور اليومي: $dailyStreak أيام متتالية 🔥",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Daily Check-in & Strike Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("daily_checkin_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFEF3C7)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Whatshot, contentDescription = null, tint = AmberGold, modifier = Modifier.size(24.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "المتابعة اليومية والـ Strike 🔥",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Text(
                                        text = "تسجيل 3 مصاريف على الأقل يومياً لمضاعفة نقاطك",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    val (success, msg) = viewModel.performDailyCheckInWithRules()
                                    checkInMessage = msg
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("daily_checkin_button")
                            ) {
                                Text("تسجيل اليوم", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Daily Transactions progress toward streak
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "المصاريف المسجلة اليوم: $todayTxCount من $minTxRequired المطلوبة",
                                fontSize = 11.sp,
                                color = if (todayTxCount >= minTxRequired) EmeraldDark else PurplePrimary,
                                fontWeight = FontWeight.Bold
                            )
                            if (todayTxCount >= minTxRequired) {
                                Text("شرط الـ Strike محقق ✓", color = EmeraldDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Text("سجل ${minTxRequired - todayTxCount} مصاريف إضافية", color = Color.Gray, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }

            checkInMessage?.let { msg ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE9FE))
                    ) {
                        Text(
                            text = msg,
                            color = PurplePrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            // Referral & Invite Friends Program Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("referral_program_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEDE9FE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, tint = PurplePrimary, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "دعوة الأصدقاء (اكسب +$referralReward نقطة) 🎁",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "شارك كودك الخاص واكسب نقاطاً لك ولصديقك عند انضمامه",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // User's own referral code box
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("كود الإحالة الخاص بك:", fontSize = 11.sp, color = Color.Gray)
                                    Text(
                                        text = userReferralCode,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = PurplePrimary
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Button(
                                        onClick = {
                                            val sendIntent = android.content.Intent().apply {
                                                action = android.content.Intent.ACTION_SEND
                                                putExtra(
                                                    android.content.Intent.EXTRA_TEXT,
                                                    "حمّل تطبيق مليونير لإدارة الثروة والادخار واستخدم كود إحالتي [$userReferralCode] للحصول على 100 نقطة مجاناً! 🚀"
                                                )
                                                type = "text/plain"
                                            }
                                            context.startActivity(android.content.Intent.createChooser(sendIntent, "مشاركة كود الإحالة"))
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("مشاركة", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Enter friend's referral code
                        Text("هل لديك كود إحالة من صديق؟", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = friendReferralCodeInput,
                                onValueChange = { friendReferralCodeInput = it },
                                placeholder = { Text("أدخل الكود مثلاً: MIL-12345", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            Button(
                                onClick = {
                                    val (success, msg) = viewModel.applyReferralCode(friendReferralCodeInput)
                                    referralStatusMessage = msg
                                    if (success) friendReferralCodeInput = ""
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("تفعيل", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        referralStatusMessage?.let { rMsg ->
                            Text(
                                text = rMsg,
                                fontSize = 11.sp,
                                color = if (rMsg.contains("بنجاح")) EmeraldDark else RoseRed,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        }

                        Text(
                            text = "عدد الأصدقاء المنضمين عبرك حتى الآن: $referralsCount صديق 👥",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            // Google AdMob Rewarded Ad Watch Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("watch_reward_ad_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(PurplePrimary.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayCircleFilled,
                                    contentDescription = null,
                                    tint = PurplePrimary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "مشاهدة إعلانات جوجل آدسينس مكافآت",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = "شاهد إعلان فيديو قصير واكسب +50 نقطة فوراً!",
                                    fontSize = 12.sp,
                                    color = EmeraldDark,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "تتيح لك مشاهدة الإعلانات دعم التطبيق مع ربح نقاط مباشرة تُضاف لرصيدك لفتح ميزات المستشار المالي والشارات.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { showRewardedAdDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("start_watch_rewarded_ad_btn")
                        ) {
                            Icon(Icons.Default.OndemandVideo, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("شاهد الآن واكسب +50 نقطة ⭐", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }

            // Points Redemption & Badges
            item {
                Text(
                    text = "شارات وإنجازات النقاط",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    RewardBadgeItem(
                        title = "المدخر البرونزي",
                        pointsReq = 50,
                        currentPoints = userPoints,
                        icon = Icons.Default.Shield,
                        modifier = Modifier.weight(1f)
                    )
                    RewardBadgeItem(
                        title = "المستثمر الفضي",
                        pointsReq = 150,
                        currentPoints = userPoints,
                        icon = Icons.Default.WorkspacePremium,
                        modifier = Modifier.weight(1f)
                    )
                    RewardBadgeItem(
                        title = "المليونير الذهبي",
                        pointsReq = 300,
                        currentPoints = userPoints,
                        icon = Icons.Default.Diamond,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

    if (showRewardedAdDialog) {
        RewardedAdDialog(
            adUnitId = rewardedAdId,
            onRewardEarned = {
                viewModel.rewardUserForAd()
            },
            onDismiss = {
                showRewardedAdDialog = false
            }
        )
    }
}

@Composable
fun RewardBadgeItem(
    title: String,
    pointsReq: Int,
    currentPoints: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    val isUnlocked = currentPoints >= pointsReq

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) Color(0xFFFEF3C7) else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isUnlocked) AmberGold else Color(0xFF94A3B8),
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isUnlocked) Color(0xFF92400E) else Color(0xFF64748B)
            )
            Text(
                text = if (isUnlocked) "تم الفتح ✓" else "$pointsReq نقطة",
                fontSize = 10.sp,
                color = if (isUnlocked) EmeraldDark else Color(0xFF94A3B8)
            )
        }
    }
}
