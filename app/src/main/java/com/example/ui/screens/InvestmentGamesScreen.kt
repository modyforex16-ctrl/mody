package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.data.local.InvestmentSkillMission
import com.example.data.local.OnlineInvestmentDeal
import com.example.data.local.RealEstateProperty
import com.example.ui.theme.*
import com.example.viewmodel.FinancialViewModel

@Composable
fun InvestmentGamesScreen(
    viewModel: FinancialViewModel
) {
    val gameState by viewModel.investmentGameState.collectAsStateWithLifecycle()

    var activeTab by remember { mutableStateOf("PORTFOLIO") } // "PORTFOLIO", "REAL_ESTATE", "ONLINE_DEALS", "MISSIONS"
    var selectedCityFilter by remember { mutableStateOf("الكل") }
    var notificationMessage by remember { mutableStateOf<String?>(null) }
    var selectedPropertyForDetail by remember { mutableStateOf<RealEstateProperty?>(null) }
    var showBuyMortgageDialog by remember { mutableStateOf(false) }

    val filteredProps = remember(gameState.properties, selectedCityFilter) {
        if (selectedCityFilter == "الكل") gameState.properties
        else gameState.properties.filter { it.city == selectedCityFilter }
    }

    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
        ) {
            // Hero Wealth Dashboard Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("investment_game_banner"),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF0F766E), Color(0xFF065F46), Color(0xFF1E1B4B))
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.MilitaryTech, contentDescription = null, tint = AmberGold, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "المستوى ${gameState.wealthTierLevel}: ${gameState.wealthTierTitle}",
                                        color = AmberGold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color.White.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "الجولة ${gameState.currentRound} (الربع السنوي)",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "صافي قيمة ثروتك الإجمالية:",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 12.sp
                            )
                            Text(
                                text = "${String.format("%.1f", gameState.totalPortfolioValue)} ر.س",
                                color = Color.White,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "العائد الكلي: ${String.format("%+.1f", gameState.roiPercentage)}%",
                                    color = if (gameState.roiPercentage >= 0) EmeraldLight else RoseLight,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "السيولة الحرة: ${String.format("%.0f", gameState.cashBalance)} ر.س",
                                    color = AmberLight,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "خبرة XP: ${gameState.experienceXp}",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            // Navigation Tabs for Simulation
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = activeTab == "PORTFOLIO",
                        onClick = { activeTab = "PORTFOLIO" },
                        label = { Text("المحفظة", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldDark,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.weight(1f).testTag("tab_game_portfolio")
                    )

                    FilterChip(
                        selected = activeTab == "REAL_ESTATE",
                        onClick = { activeTab = "REAL_ESTATE" },
                        label = { Text("سوق العقار 🏢", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PurplePrimary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.weight(1f).testTag("tab_game_real_estate")
                    )

                    FilterChip(
                        selected = activeTab == "ONLINE_DEALS",
                        onClick = { activeTab = "ONLINE_DEALS" },
                        label = { Text("صفقات أونلاين 🌐", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFD97706),
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.weight(1f).testTag("tab_game_online_deals")
                    )

                    FilterChip(
                        selected = activeTab == "MISSIONS",
                        onClick = { activeTab = "MISSIONS" },
                        label = { Text("المهارات ⭐", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF2563EB),
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.weight(1f).testTag("tab_game_missions")
                    )
                }
            }

            // Notification / Alert Message
            notificationMessage?.let { msg ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFD1FAE5))
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldDark)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(msg, fontSize = 12.sp, color = EmeraldDark, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // ==================== TAB 1: PORTFOLIO & MACRO SCENARIOS ====================
            if (activeTab == "PORTFOLIO") {
                // Latest Market Scenario Event
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE9FE))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Campaign, contentDescription = null, tint = PurplePrimary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("سيناريو وحالة السوق الحالية:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PurpleDark)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = gameState.latestEventHeadline,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF1E1B4B),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                // Quick Assets List
                item {
                    Text("أصول الأسواق المالية والنقدية:", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }

                item {
                    AssetInvestmentCard(
                        name = "صناديق المؤشرات والأسهم (تاسي / S&P500)",
                        amount = gameState.stocksAmount,
                        returnText = "+12% سنوي متوسط",
                        onBuy = { viewModel.buyAsset("STOCKS", 1000.0) },
                        onSell = { viewModel.sellAsset("STOCKS", 1000.0) }
                    )
                }

                item {
                    AssetInvestmentCard(
                        name = "سبائك الذهب الاستثماري عيار 24",
                        amount = gameState.goldAmount,
                        returnText = "+9% حفظ القيمة وملاذ آمن للتضخم",
                        onBuy = { viewModel.buyAsset("GOLD", 1000.0) },
                        onSell = { viewModel.sellAsset("GOLD", 1000.0) }
                    )
                }

                item {
                    AssetInvestmentCard(
                        name = "صكوك المرابحة والودائع الإسلامية",
                        amount = gameState.sukukAmount,
                        returnText = "+7.5% عائد آمن دوري موثق",
                        onBuy = { viewModel.buyAsset("SUKUK", 1000.0) },
                        onSell = { viewModel.sellAsset("SUKUK", 1000.0) }
                    )
                }

                // Advance Quarter Button
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            val pts = viewModel.advanceInvestmentGameRound()
                            notificationMessage = if (pts > 0)
                                "انتقلت للربع المالي التالي! تم تحصيل الإيجارات وربحت +$pts نقطة حقيقية لمحفظتك!"
                            else
                                "انتقلت للربع المالي التالي وتغيرت مؤشرات السوق وحُصّلت الإيجارات!"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("advance_game_quarter_btn")
                    ) {
                        Icon(Icons.Default.FastForward, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("التقدم إلى الربع الاقتصادي التالي وتحصيل العوائد 🚀", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedButton(
                        onClick = {
                            viewModel.resetInvestmentGame()
                            notificationMessage = "تمت إعادة تعيين اللعبة برأس مال 15,000 ر.س جديد."
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("إعادة بدء اللعبة بمحفظة جديدة", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // ==================== TAB 2: REAL ESTATE MARKET BY CITIES ====================
            if (activeTab == "REAL_ESTATE") {
                // Rental Income Summary & Collect Button
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("إجمالي الدخل الإيجاري السنوي:", fontSize = 11.sp, color = EmeraldDark)
                                Text(
                                    "${String.format("%.0f", gameState.annualRentalIncome)} ر.س / سنوياً",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = EmeraldDark
                                )
                            }

                            Button(
                                onClick = {
                                    val collected = viewModel.collectRentalIncome()
                                    notificationMessage = if (collected > 0)
                                        "تم تحصيل ${String.format("%.0f", collected)} ر.س إيجارات نقدية لهذا الربع!"
                                    else
                                        "لا توجد وحدات عقارية مؤجرة حالياً لتوليد إيجار."
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("تحصيل الإيجار الآن", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // City Filter Chips
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(listOf("الكل", "الرياض", "جدة", "الخبر", "مكة المكرمة")) { city ->
                            FilterChip(
                                selected = selectedCityFilter == city,
                                onClick = { selectedCityFilter = city },
                                label = { Text(city, fontSize = 11.sp) }
                            )
                        }
                    }
                }

                // Properties List
                items(filteredProps, key = { it.id }) { prop ->
                    RealEstatePropertyCard(
                        property = prop,
                        canAffordCash = gameState.cashBalance >= prop.purchasePrice,
                        canAffordMortgage = gameState.cashBalance >= (prop.purchasePrice * 0.20),
                        onBuyCash = {
                            val success = viewModel.buyRealEstate(prop.id, isFinanced = false)
                            notificationMessage = if (success)
                                "مبارك! اشتريت ${prop.propertyType} في ${prop.district} كاش بنجاح!"
                            else
                                "سيولتك غير كافية لشراء هذا العقار كاش!"
                        },
                        onBuyMortgage = {
                            val success = viewModel.buyRealEstate(prop.id, isFinanced = true)
                            notificationMessage = if (success)
                                "تم التمويل العقاري بـ 20% دفعة أولى وتملك ${prop.propertyType} بنجاح!"
                            else
                                "سيولتك غير كافية لسداد الدفعة الأولى (20%)!"
                        },
                        onSell = {
                            val success = viewModel.sellRealEstate(prop.id)
                            notificationMessage = if (success)
                                "تم بيع وحدة من عقار ${prop.district} واسترداد القيمة النقدية!"
                            else
                                "لا تملك وحدات من هذا العقار لبيعها!"
                        }
                    )
                }
            }

            // ==================== TAB 3: ONLINE CO-INVESTMENT & DEALS ====================
            if (activeTab == "ONLINE_DEALS") {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7))
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Hub, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "سوق الشراكة والتمويل الجماعي أونلاين 🌐",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF92400E)
                                )
                                Text(
                                    text = "اشترِ حصصاً تبدأ من 250 ر.س في مشاريع وعقارات تجارية مع مستثمرين آخرين.",
                                    fontSize = 11.sp,
                                    color = Color(0xFF78350F)
                                )
                            }
                        }
                    }
                }

                items(gameState.onlineDeals, key = { it.id }) { deal ->
                    OnlineDealCard(
                        deal = deal,
                        canAfford = gameState.cashBalance >= deal.sharePrice,
                        onBuyShare = {
                            val success = viewModel.investInOnlineDeal(deal.id, 1)
                            notificationMessage = if (success)
                                "تم شراء حصة في '${deal.title}' بنجاح وأصبحت شريكاً في المشروع!"
                            else
                                "سيولتك النقدية غير كافية لشراء حصة!"
                        }
                    )
                }
            }

            // ==================== TAB 4: SKILLS ACADEMY & MISSIONS ====================
            if (activeTab == "MISSIONS") {
                item {
                    Text(
                        text = "مهمات تطوير المهارات المالية وبناء الثروة ⭐",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                items(gameState.skillMissions, key = { it.id }) { mission ->
                    SkillMissionCard(
                        mission = mission,
                        onClaim = {
                            val success = viewModel.claimMissionReward(mission.id)
                            notificationMessage = if (success)
                                "تم استلام جائزة المهمة: +${mission.rewardPoints} نقطة و +${mission.rewardXp} XP!"
                            else
                                "لم تكتمل شروط هذه المهمة بعد!"
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RealEstatePropertyCard(
    property: RealEstateProperty,
    canAffordCash: Boolean,
    canAffordMortgage: Boolean,
    onBuyCash: () -> Unit,
    onBuyMortgage: () -> Unit,
    onSell: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = property.propertyType,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "📍 ${property.city} - ${property.district}",
                        fontSize = 12.sp,
                        color = PurplePrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (property.ownedUnits > 0) {
                    Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFD1FAE5)) {
                        Text(
                            text = "تملك: ${property.ownedUnits} وحدة ✓",
                            color = EmeraldDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = property.description,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Pricing & Yield metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("سعر الشراء", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${String.format("%.0f", property.purchasePrice)} ر.س", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("الإيجار السنوي", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${String.format("%.0f", property.annualRent)} ر.س", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EmeraldDark)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("العائد التأجيري", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${property.rentalYieldPercentage}% سنوياً", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = PurplePrimary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Purchase / Sell buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onBuyCash,
                    enabled = canAffordCash,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                ) {
                    Text("شراء كاش", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onBuyMortgage,
                    enabled = canAffordMortgage,
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                ) {
                    Text("تمويل 20% دفعة", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                if (property.ownedUnits > 0) {
                    OutlinedButton(
                        onClick = onSell,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text("بيع وحدة", fontSize = 11.sp, color = RoseRed)
                    }
                }
            }
        }
    }
}

@Composable
fun OnlineDealCard(
    deal: OnlineInvestmentDeal,
    canAfford: Boolean,
    onBuyShare: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = deal.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(
                        text = "المؤسس: ${deal.ownerName} • ${deal.category}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFEF3C7)
                ) {
                    Text(
                        text = "عائد متوقع: ${deal.expectedAnnualRoi}%",
                        color = Color(0xFF92400E),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("سعر الحصة: ${deal.sharePrice.toInt()} ر.س", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PurplePrimary)
                Text("المستثمرون: ${deal.investorCount} شريك 👥", fontSize = 11.sp, color = Color.Gray)
                if (deal.userOwnedShares > 0) {
                    Text("حصصك: ${deal.userOwnedShares} حصة", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EmeraldDark)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onBuyShare,
                enabled = canAfford && deal.availableShares > 0,
                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("شراء حصة شراكة (${deal.sharePrice.toInt()} ر.س)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun SkillMissionCard(
    mission: InvestmentSkillMission,
    onClaim: () -> Unit
) {
    val progressRatio = (mission.currentProgress / mission.targetGoal).toFloat().coerceIn(0f, 1f)
    val isReadyToClaim = !mission.isCompleted && progressRatio >= 1f

    Card(
        modifier = Modifier.fillMaxWidth(),
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
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEDE9FE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = PurplePrimary, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = mission.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(text = "مهارة: ${mission.skillCategory}", fontSize = 11.sp, color = PurplePrimary)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFEF3C7)
                ) {
                    Text(
                        text = "+${mission.rewardPoints}⭐ (+${mission.rewardXp} XP)",
                        color = Color(0xFF92400E),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = mission.description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progressRatio },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (progressRatio >= 1f) EmeraldGreen else PurplePrimary,
                trackColor = Color(0xFFE2E8F0)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "التقدم: ${mission.currentProgress.toInt()} / ${mission.targetGoal.toInt()}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )

                if (mission.isCompleted) {
                    Text("تم استلام الجائزة ✓", color = EmeraldDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                } else {
                    Button(
                        onClick = onClaim,
                        enabled = isReadyToClaim,
                        colors = ButtonDefaults.buttonColors(containerColor = AmberGold, contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(if (isReadyToClaim) "استلام الجائزة 🎁" else "قيد الإنجاز", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AssetInvestmentCard(
    name: String,
    amount: Double,
    returnText: String,
    onBuy: () -> Unit,
    onSell: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onBackground)
                Text(returnText, fontSize = 11.sp, color = EmeraldDark, fontWeight = FontWeight.SemiBold)
                Text("المستثمر: ${String.format("%.0f", amount)} ر.س", fontSize = 12.sp, color = PurplePrimary, fontWeight = FontWeight.Bold)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = onBuy,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD1FAE5), contentColor = EmeraldDark),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("+ شراء", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onSell,
                    enabled = amount > 0,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2), contentColor = RoseRed),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("- بيع", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
