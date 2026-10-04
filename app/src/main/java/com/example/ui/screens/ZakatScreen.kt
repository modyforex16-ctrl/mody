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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.ZakatInstallmentPlan
import com.example.ui.theme.*
import com.example.viewmodel.FinancialViewModel

@Composable
fun ZakatScreen(
    viewModel: FinancialViewModel
) {
    val zakatState by viewModel.zakatState.collectAsStateWithLifecycle()
    val goldRates by viewModel.liveGoldRates.collectAsStateWithLifecycle()
    val isFetchingGold by viewModel.isFetchingGold.collectAsStateWithLifecycle()
    val zakatPlan by viewModel.zakatInstallmentPlan.collectAsStateWithLifecycle()

    var isHijriYear by remember { mutableStateOf(true) } // true: 2.5%, false: 2.577% (Solar/Gregorian)

    var gold24kText by remember { mutableStateOf("") }
    var gold21kText by remember { mutableStateOf("") }
    var silverText by remember { mutableStateOf("") }
    var cashText by remember { mutableStateOf("") }
    var merchandiseText by remember { mutableStateOf("") }
    var debtsOwedByMeText by remember { mutableStateOf("") }

    var showSuccessSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }
    var showInstallmentDialog by remember { mutableStateOf(false) }

    // Recompute Zakat whenever inputs or calendar type changes
    LaunchedEffect(
        gold24kText, gold21kText, silverText, cashText,
        merchandiseText, debtsOwedByMeText,
        goldRates, isHijriYear
    ) {
        viewModel.updateZakatInputs(
            goldGrams24k = gold24kText.toDoubleOrNull() ?: 0.0,
            goldPricePerGram24k = goldRates.gram24k,
            goldGrams21k = gold21kText.toDoubleOrNull() ?: 0.0,
            silverGrams = silverText.toDoubleOrNull() ?: 0.0,
            silverPricePerGram = goldRates.silverGram,
            cashOnHandAndBank = cashText.toDoubleOrNull() ?: 0.0,
            businessMerchandise = merchandiseText.toDoubleOrNull() ?: 0.0,
            debtsDueImmediately = debtsOwedByMeText.toDoubleOrNull() ?: 0.0,
            isHijriCalendar = isHijriYear
        )
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
            // Live Gold & Silver Rates Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("gold_rates_card"),
                    shape = RoundedCornerShape(18.dp),
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
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(AmberLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = AmberGold, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "أسعار الذهب والفضة التلقائية المحدثة",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "آخر تحديث: ${goldRates.lastUpdated}",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            IconButton(
                                onClick = { viewModel.refreshGoldRates() },
                                enabled = !isFetchingGold,
                                modifier = Modifier.size(32.dp).testTag("refresh_gold_rates_btn")
                            ) {
                                if (isFetchingGold) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                } else {
                                    Icon(Icons.Default.Refresh, contentDescription = "تحديث الأسعار", tint = PurplePrimary)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            GoldRateChip(title = "عيار 24", price = "${goldRates.gram24k} ر.س")
                            GoldRateChip(title = "عيار 22", price = "${goldRates.gram22k} ر.س")
                            GoldRateChip(title = "عيار 21", price = "${goldRates.gram21k} ر.س")
                            GoldRateChip(title = "الفضة", price = "${goldRates.silverGram} ر.س")
                        }
                    }
                }
            }

            // Calendar Selection: Hijri (2.5%) vs Gregorian (2.577%)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "نوع الحول المالي المعتمد في الحساب:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = isHijriYear,
                                onClick = { isHijriYear = true },
                                label = { Text("السنة الهجرية (الحول القمري 2.50%)", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EmeraldDark,
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier.weight(1f).testTag("hijri_calendar_chip")
                            )
                            FilterChip(
                                selected = !isHijriYear,
                                onClick = { isHijriYear = false },
                                label = { Text("السنة الميلادية (الشمسي 2.577%)", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PurplePrimary,
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier.weight(1f).testTag("gregorian_calendar_chip")
                            )
                        }
                        Text(
                            text = if (isHijriYear)
                                "✓ وفق الشريعة: الحول الهجري 354 يوماً يُخرج عنه 2.5% (ربع العُشر)."
                            else
                                "✓ وفق مجمع الفقه الإسلامي: الحول الميلادي 365 يوماً يُخرج عنه 2.577% لتعويض فارق الـ 11 يوماً.",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            }

            // Result Banner Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("zakat_result_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (zakatState.isNisabReached) EmeraldDark else PurpleDark
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isHijriYear) "الزكاة الشرعية الواجبة (حول هجري 2.5%) 🕌" else "الزكاة الشرعية الواجبة (حول ميلادي 2.577%) 🕌",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "${String.format("%.2f", zakatState.zakatAmountDue)} ر.س",
                            color = AmberGold,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Text(
                            text = if (zakatState.isNisabReached) "بلغ المال النصاب الشرعي وحال عليه الحول" else "لم يبلغ المال النصاب بعد (لا زكاة واجبة)",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.White.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "نصاب الذهب (85غ عيار 24): ${String.format("%.0f", zakatState.nisabThreshold)} ر.س",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "• وعاء الزكاة الصافي: ${String.format("%.0f", zakatState.zakatableTotal)} ر.س",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        if (zakatState.zakatAmountDue > 0) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.addExpense(
                                            category = "زكاة وصدقات",
                                            amount = zakatState.zakatAmountDue,
                                            note = "إخراج زكاة المال كاملة (${if (isHijriYear) "هجري 2.5%" else "ميلادي 2.577%"})",
                                            paymentMethod = "تحويل إلكتروني",
                                            budgetCategoryId = null
                                        )
                                        snackbarMessage = "تم تسجيل مبلغ الزكاة كاملاً كمصروف خيري بنجاح!"
                                        showSuccessSnackbar = true
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AmberGold, contentColor = Color.Black),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f).testTag("record_zakat_expense_btn")
                                ) {
                                    Icon(Icons.Default.VolunteerActivism, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("دفع كامل المبلغ", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }

                                Button(
                                    onClick = { showInstallmentDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = PurplePrimary),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f).testTag("setup_zakat_installments_btn")
                                ) {
                                    Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("تقسيط الزكاة 🗓️", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            // ZAKAT INSTALLMENT PLAN CARD (Active or Configured)
            if (zakatPlan.isActive) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("zakat_installment_plan_card"),
                        shape = RoundedCornerShape(18.dp),
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
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFEDE9FE)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Schedule, contentDescription = null, tint = PurplePrimary, modifier = Modifier.size(18.dp))
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "خطة تقسيط الزكاة الشرعية 🗓️",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = when (zakatPlan.frequency) {
                                                "MONTHLY" -> "نظام شهري (${zakatPlan.totalInstallments} قسطاً)"
                                                "QUARTERLY" -> "نظام ربع سنوي (4 أقساط)"
                                                "SEMI_ANNUALLY" -> "نظام نصف سنوي (قسطان)"
                                                else -> "نظام مخصص (${zakatPlan.totalInstallments} قسطاً)"
                                            },
                                            fontSize = 11.sp,
                                            color = PurplePrimary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (zakatPlan.remainingAmount <= 0) Color(0xFFD1FAE5) else Color(0xFFFEF3C7)
                                ) {
                                    Text(
                                        text = if (zakatPlan.remainingAmount <= 0) "مكتمل بالكامل ✓" else "جاري السداد",
                                        color = if (zakatPlan.remainingAmount <= 0) EmeraldDark else Color(0xFFB45309),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Installment Amount & Remaining Stats
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("مقدار الدفعة الواحدة", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(
                                        "${String.format("%.2f", zakatPlan.installmentAmount)} ر.س",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = PurplePrimary
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("المدفوع حتى الآن", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(
                                        "${String.format("%.2f", zakatPlan.paidAmount)} ر.س",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldDark
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("المبلغ المتبقي", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(
                                        "${String.format("%.2f", zakatPlan.remainingAmount)} ر.س",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (zakatPlan.remainingAmount > 0) RoseRed else EmeraldDark
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Progress Bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "نسبة التقدم: ${(zakatPlan.progressPercentage * 100).toInt()}%",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = "${zakatPlan.paidInstallments} من ${zakatPlan.totalInstallments} قسط",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            LinearProgressIndicator(
                                progress = { zakatPlan.progressPercentage },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(5.dp)),
                                color = if (zakatPlan.progressPercentage >= 1f) EmeraldGreen else PurplePrimary,
                                trackColor = Color(0xFFE2E8F0)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Action Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (zakatPlan.remainingAmount > 0) {
                                    Button(
                                        onClick = {
                                            viewModel.payZakatInstallment(zakatPlan.installmentAmount)
                                            snackbarMessage = "تم سداد دفعة القسط (${String.format("%.2f", zakatPlan.installmentAmount)} ر.س) بنجاح وتقييدها بالمعاملات!"
                                            showSuccessSnackbar = true
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f).testTag("pay_zakat_installment_btn")
                                    ) {
                                        Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("سداد قسط الآن (${String.format("%.1f", zakatPlan.installmentAmount)} ر.س)", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                }

                                OutlinedButton(
                                    onClick = { showInstallmentDialog = true },
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("تعديل", fontSize = 11.sp)
                                }

                                TextButton(
                                    onClick = { viewModel.resetZakatInstallmentPlan() }
                                ) {
                                    Text("إلغاء الخطة", color = RoseRed, fontSize = 11.sp)
                                }
                            }

                            Text(
                                text = "💡 فتوى شرعية: يجوز تعجيل إخراج الزكاة وتوزيعها على أقساط منتظمة خلال الحول تيسيراً على المزكي وتحقيقاً للمصلحة المستمرة للمستحقين.",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 10.dp),
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }

            if (showSuccessSnackbar) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFD1FAE5))
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldDark)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(snackbarMessage, fontSize = 12.sp, color = EmeraldDark)
                        }
                    }
                }
            }

            // Calculation inputs header
            item {
                Text(
                    text = "بنود الأموال الزكوية",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Cash and bank accounts
            item {
                ZakatInputField(
                    title = "السيولة النقدية والحسابات البنكية",
                    description = "الأموال المودعة في الحسابات الجارية والتوفير والنقد اليدوي",
                    value = cashText,
                    onValueChange = { cashText = it },
                    icon = Icons.Default.AccountBalance,
                    unit = "ر.س"
                )
            }

            // Gold and Silver
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = AmberGold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("الذهب والفضة (السبائك والمقتنيات الاستثمارية)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = gold24kText,
                                onValueChange = { gold24kText = it.filter { c -> c.isDigit() || c == '.' } },
                                label = { Text("ذهب 24 (غ)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = gold21kText,
                                onValueChange = { gold21kText = it.filter { c -> c.isDigit() || c == '.' } },
                                label = { Text("ذهب 21 (غ)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = silverText,
                                onValueChange = { silverText = it.filter { c -> c.isDigit() || c == '.' } },
                                label = { Text("فضة (غ)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }
                }
            }

            // Trade Merchandise
            item {
                ZakatInputField(
                    title = "عروض التجارة والبضائع المعدة للبيع",
                    description = "قيمة البضائع بسعر السوق الحالي في يوم إخراج الزكاة",
                    value = merchandiseText,
                    onValueChange = { merchandiseText = it },
                    icon = Icons.Default.Storefront,
                    unit = "ر.س"
                )
            }

            // Deductions: Immediate debts
            item {
                ZakatInputField(
                    title = "خصم الديون المستحقة عليك حالاً",
                    description = "الديون والالتزامات المالية التي يجب عليك سدادها فوراً وتخصم من الوعاء",
                    value = debtsOwedByMeText,
                    onValueChange = { debtsOwedByMeText = it },
                    icon = Icons.Default.RemoveCircleOutline,
                    unit = "ر.س"
                )
            }
        }
    }

    if (showInstallmentDialog) {
        ZakatInstallmentSetupDialog(
            currentTotalZakat = if (zakatState.zakatAmountDue > 0) zakatState.zakatAmountDue else if (zakatPlan.totalZakat > 0) zakatPlan.totalZakat else 1200.0,
            initialPlan = zakatPlan,
            onDismiss = { showInstallmentDialog = false },
            onConfirm = { total, frequency, count ->
                viewModel.createZakatInstallmentPlan(total, frequency, count)
                showInstallmentDialog = false
                snackbarMessage = "تم إنشاء خطة تقسيط الزكاة بنجاح ($count أقساط)!"
                showSuccessSnackbar = true
            }
        )
    }
}

@Composable
fun ZakatInstallmentSetupDialog(
    currentTotalZakat: Double,
    initialPlan: ZakatInstallmentPlan,
    onDismiss: () -> Unit,
    onConfirm: (total: Double, frequency: String, count: Int) -> Unit
) {
    var totalZakatText by remember { mutableStateOf(String.format("%.2f", currentTotalZakat)) }
    var selectedFrequency by remember { mutableStateOf(if (initialPlan.isActive) initialPlan.frequency else "MONTHLY") }
    var customCountText by remember { mutableStateOf(if (initialPlan.isActive) initialPlan.totalInstallments.toString() else "6") }

    val calculatedCount = when (selectedFrequency) {
        "MONTHLY" -> 12
        "QUARTERLY" -> 4
        "SEMI_ANNUALLY" -> 2
        else -> customCountText.toIntOrNull()?.coerceAtLeast(1) ?: 6
    }

    val total = totalZakatText.toDoubleOrNull() ?: currentTotalZakat
    val installmentAmount = if (calculatedCount > 0) total / calculatedCount else 0.0

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(total, selectedFrequency, calculatedCount)
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                modifier = Modifier.testTag("confirm_zakat_plan_btn")
            ) {
                Text("اعتماد وبدء خطة التقسيط", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = PurplePrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("تقسيط الزكاة المستحقة", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "حدد نظام التقسيط الدوري المناسب لك لحساب مقدار كل دفعة ومتابعة تقدم السداد:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = totalZakatText,
                    onValueChange = { totalZakatText = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("إجمالي مبلغ الزكاة المراد تقسيطه (ر.س)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text("دورية السداد المطلوبة:", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilterChip(
                        selected = selectedFrequency == "MONTHLY",
                        onClick = { selectedFrequency = "MONTHLY" },
                        label = { Text("شهري (12 قسط)", fontSize = 10.sp) },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = selectedFrequency == "QUARTERLY",
                        onClick = { selectedFrequency = "QUARTERLY" },
                        label = { Text("ربع سنوي (4)", fontSize = 10.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilterChip(
                        selected = selectedFrequency == "SEMI_ANNUALLY",
                        onClick = { selectedFrequency = "SEMI_ANNUALLY" },
                        label = { Text("نصف سنوي (2)", fontSize = 10.sp) },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = selectedFrequency == "CUSTOM",
                        onClick = { selectedFrequency = "CUSTOM" },
                        label = { Text("مخصص (أقساط)", fontSize = 10.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }

                if (selectedFrequency == "CUSTOM") {
                    OutlinedTextField(
                        value = customCountText,
                        onValueChange = { customCountText = it.filter { c -> c.isDigit() } },
                        label = { Text("عدد الأقساط المطلوب") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE9FE))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("مقدار الدفعة في كل قسط:", fontSize = 12.sp, color = PurpleDark, fontWeight = FontWeight.Bold)
                            Text("${String.format("%.2f", installmentAmount)} ر.س", fontSize = 14.sp, color = PurplePrimary, fontWeight = FontWeight.ExtraBold)
                        }
                        Text("إجمالي الأقساط: $calculatedCount دفعة", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    )
}

@Composable
fun GoldRateChip(title: String, price: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFEDE9FE))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, fontSize = 10.sp, color = PurplePrimary, fontWeight = FontWeight.Bold)
            Text(price, fontSize = 11.sp, color = Color(0xFF1E1B4B), fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
fun ZakatInputField(
    title: String,
    description: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    unit: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(PurplePrimary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = PurplePrimary, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(text = description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = value,
                onValueChange = { onValueChange(it.filter { c -> c.isDigit() || c == '.' }) },
                label = { Text("المبلغ ($unit)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }
}
