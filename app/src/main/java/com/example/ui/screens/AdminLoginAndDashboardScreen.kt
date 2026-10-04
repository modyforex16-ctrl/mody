package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*
import com.example.viewmodel.FinancialViewModel

@Composable
fun AdminLoginAndDashboardScreen(
    viewModel: FinancialViewModel,
    onBack: () -> Unit
) {
    var isAuthenticated by remember { mutableStateOf(false) }

    if (!isAuthenticated) {
        AdminLoginView(
            onLoginSuccess = { isAuthenticated = true },
            onBack = onBack,
            viewModel = viewModel
        )
    } else {
        AdminDashboardView(
            viewModel = viewModel,
            onLogout = { isAuthenticated = false }
        )
    }
}

@Composable
fun AdminLoginView(
    onLoginSuccess: () -> Unit,
    onBack: () -> Unit,
    viewModel: FinancialViewModel
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(PurplePrimary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AdminPanelSettings,
                contentDescription = null,
                tint = PurplePrimary,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "تسجيل دخول لوحة تحكم المدير",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "الوصول محمي لإدارة إعدادات ومعرفات جوجل آداموب",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                errorMessage = null
            },
            label = { Text("اسم المستخدم (Username)") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            modifier = Modifier.fillMaxWidth().testTag("admin_username_input"),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                errorMessage = null
            },
            label = { Text("كلمة المرور (Password)") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            modifier = Modifier.fillMaxWidth().testTag("admin_password_input"),
            singleLine = true
        )

        errorMessage?.let { error ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                color = RoseRed,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                val isValid = viewModel.adminSettings.authenticate(username, password)
                if (isValid) {
                    onLoginSuccess()
                } else {
                    errorMessage = "اسم المستخدم أو كلمة المرور غير صحيحة! تأكد من إدخال mody81 و 527149"
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("admin_login_submit_btn")
        ) {
            Text("تسجيل الدخول للوحة التحكم", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = onBack) {
            Text("العودة للتطبيق", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun AdminDashboardView(
    viewModel: FinancialViewModel,
    onLogout: () -> Unit
) {
    val settings = viewModel.adminSettings

    val currentAppId by settings.admobAppId.collectAsStateWithLifecycle()
    val currentBannerTop by settings.bannerTopId.collectAsStateWithLifecycle()
    val currentBannerBottom by settings.bannerBottomId.collectAsStateWithLifecycle()
    val currentRewarded by settings.rewardedAdId.collectAsStateWithLifecycle()
    val currentInterstitial by settings.interstitialAdId.collectAsStateWithLifecycle()
    val currentAdsEnabled by settings.adsEnabled.collectAsStateWithLifecycle()
    val currentTestMode by settings.testMode.collectAsStateWithLifecycle()

    val currentMarketCost by settings.marketplaceServiceCost.collectAsStateWithLifecycle()
    val currentReferralReward by settings.referralRewardPoints.collectAsStateWithLifecycle()
    val currentStreakReward by settings.dailyStreakReward.collectAsStateWithLifecycle()
    val currentMinTx by settings.minDailyTransactions.collectAsStateWithLifecycle()

    var appIdInput by remember(currentAppId) { mutableStateOf(currentAppId) }
    var bannerTopInput by remember(currentBannerTop) { mutableStateOf(currentBannerTop) }
    var bannerBottomInput by remember(currentBannerBottom) { mutableStateOf(currentBannerBottom) }
    var rewardedInput by remember(currentRewarded) { mutableStateOf(currentRewarded) }
    var interstitialInput by remember(currentInterstitial) { mutableStateOf(currentInterstitial) }
    var adsEnabledInput by remember(currentAdsEnabled) { mutableStateOf(currentAdsEnabled) }
    var testModeInput by remember(currentTestMode) { mutableStateOf(currentTestMode) }

    var marketCostInput by remember(currentMarketCost) { mutableStateOf(currentMarketCost.toString()) }
    var referralRewardInput by remember(currentReferralReward) { mutableStateOf(currentReferralReward.toString()) }
    var streakRewardInput by remember(currentStreakReward) { mutableStateOf(currentStreakReward.toString()) }
    var minTxInput by remember(currentMinTx) { mutableStateOf(currentMinTx.toString()) }

    var showSuccessMessage by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "لوحة تحكم المدير (mody81)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "إدارة كاملة لمعرفات Google AdMob والإعلانات",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = onLogout,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2), contentColor = RoseRed),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("خروج", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (showSuccessMessage) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFD1FAE5))
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldDark)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("تم حفظ معرفات AdMob وتحديث الإعدادات بنجاح!", fontSize = 13.sp, color = EmeraldDark, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Toggles Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("التحكم في حالة الإعلانات", fontWeight = FontWeight.Bold, fontSize = 14.sp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("تفعيل ظهور الإعلانات بالتطبيق", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Text("إظهار البانر العلوي والسفلي وإعلانات المكافآت", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = adsEnabledInput,
                            onCheckedChange = { adsEnabledInput = it },
                            modifier = Modifier.testTag("toggle_ads_switch")
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("وضع الإعلانات التجريبية (Test Mode)", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Text("استخدام معرفات الاختبار الرسمية من جوجل لمنع الحظر", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = testModeInput,
                            onCheckedChange = { testModeInput = it },
                            modifier = Modifier.testTag("toggle_test_mode_switch")
                        )
                    }
                }
            }
        }

        // AdMob IDs Form
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("معرفات جوجل آداموب (Google AdMob IDs)", fontWeight = FontWeight.Bold, fontSize = 14.sp)

                    OutlinedTextField(
                        value = appIdInput,
                        onValueChange = { appIdInput = it },
                        label = { Text("معرف التطبيق (AdMob App ID)") },
                        modifier = Modifier.fillMaxWidth().testTag("admob_app_id_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = bannerTopInput,
                        onValueChange = { bannerTopInput = it },
                        label = { Text("معرف البانر العلوي (Top Banner ID)") },
                        modifier = Modifier.fillMaxWidth().testTag("admob_banner_top_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = bannerBottomInput,
                        onValueChange = { bannerBottomInput = it },
                        label = { Text("معرف البانر السفلي (Bottom Banner ID)") },
                        modifier = Modifier.fillMaxWidth().testTag("admob_banner_bottom_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = rewardedInput,
                        onValueChange = { rewardedInput = it },
                        label = { Text("معرف إعلان المكافأة (Rewarded Video ID)") },
                        modifier = Modifier.fillMaxWidth().testTag("admob_rewarded_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = interstitialInput,
                        onValueChange = { interstitialInput = it },
                        label = { Text("معرف الإعلان البيني (Interstitial Ad ID)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }
        }

        // Points & Marketplace Control Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Storefront, contentDescription = null, tint = PurplePrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("إعدادات سوق الخدمات ونقاط الذكاء الاصطناعي", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    OutlinedTextField(
                        value = marketCostInput,
                        onValueChange = { marketCostInput = it.filter { c -> c.isDigit() } },
                        label = { Text("تكلفة الاستعلام في سوق الخدمات بالنقاط") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("admin_marketplace_cost_input"),
                        singleLine = true
                    )
                }
            }
        }

        // Daily Strike & Referral Rules Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = AmberGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("قواعد الـ Strike والمتابعة اليومية والإحالة", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    OutlinedTextField(
                        value = minTxInput,
                        onValueChange = { minTxInput = it.filter { c -> c.isDigit() } },
                        label = { Text("الحد الأدنى للمصاريف اليومية لاحتساب الحضور والـ Strike") },
                        supportingText = { Text("المطلوب: 3 مصاريف على الأقل يومياً لزيادة الـ Strike") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("admin_min_tx_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = streakRewardInput,
                        onValueChange = { streakRewardInput = it.filter { c -> c.isDigit() } },
                        label = { Text("مكافأة الحضور اليومي المستمر (نقاط الـ Strike)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("admin_streak_reward_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = referralRewardInput,
                        onValueChange = { referralRewardInput = it.filter { c -> c.isDigit() } },
                        label = { Text("مكافأة إحالة ودعوة صديق بالنقاط") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("admin_referral_reward_input"),
                        singleLine = true
                    )
                }
            }
        }

        // Points & Database Manager
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("إدارة نقاط المستخدمين", fontWeight = FontWeight.Bold, fontSize = 14.sp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { settings.addPoints(100) },
                            colors = ButtonDefaults.buttonColors(containerColor = AmberGold, contentColor = Color.Black),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("+100 نقطة تجريبية", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { settings.resetPoints() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("تصفير النقاط", fontSize = 11.sp, color = RoseRed)
                        }
                    }
                }
            }
        }

        // Save Button
        item {
            Button(
                onClick = {
                    settings.updateAdSettings(
                        appId = appIdInput.trim(),
                        bannerTop = bannerTopInput.trim(),
                        bannerBottom = bannerBottomInput.trim(),
                        rewarded = rewardedInput.trim(),
                        interstitial = interstitialInput.trim(),
                        enabled = adsEnabledInput,
                        isTest = testModeInput
                    )
                    settings.updateAdminMarketplaceAndStreakSettings(
                        marketCost = marketCostInput.toIntOrNull() ?: 25,
                        referralReward = referralRewardInput.toIntOrNull() ?: 100,
                        streakReward = streakRewardInput.toIntOrNull() ?: 30,
                        minTx = minTxInput.toIntOrNull() ?: 3
                    )
                    showSuccessMessage = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_admin_settings_btn")
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("حفظ التعديلات وتطبيق الإعدادات فوراً", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}
