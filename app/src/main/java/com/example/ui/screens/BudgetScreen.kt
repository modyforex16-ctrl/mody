package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.BudgetCategoryEntity
import com.example.ui.theme.*
import com.example.viewmodel.FinancialViewModel

@Composable
fun BudgetScreen(
    viewModel: FinancialViewModel
) {
    val budgetCategories by viewModel.budgetCategories.collectAsStateWithLifecycle()
    val transactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val totalIncome by viewModel.totalIncome.collectAsStateWithLifecycle()
    val isSmartBudgetLoading by viewModel.isSmartBudgetLoading.collectAsStateWithLifecycle()

    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var showAiBudgetProposalDialog by remember { mutableStateOf(false) }

    val totalAllocated = remember(budgetCategories) {
        budgetCategories.sumOf { it.allocatedAmount }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddCategoryDialog = true },
                containerColor = PurplePrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_budget_category_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "إضافة بند ميزانية")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
        ) {
            // Header card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "الميزانية الشهرية المقسمة",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = "ربط مباشر مع المصروفات والدخل 🎯",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(PurplePrimary.copy(alpha = 0.1f))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "${budgetCategories.size} بنود",
                                    color = PurplePrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("إجمالي المخصص للميزانية", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${String.format("%.1f", totalAllocated)} ر.س", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PurplePrimary)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("إجمالي الدخل المتوفر", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${String.format("%.1f", totalIncome)} ر.س", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = EmeraldDark)
                            }
                        }
                    }
                }
            }

            // AI Smart Budget Proposal Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("ai_smart_budget_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(PurpleGradientStart, Color(0xFF4F46E5))
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AmberGold)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "اقتراح ميزانية ذكية بالذكاء الاصطناعي",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "تقسيم مثالي يضمن ادخار 10% إلى 20% شهرياً",
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Button(
                                onClick = { showAiBudgetProposalDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = AmberGold, contentColor = Color.Black),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("open_ai_budget_btn")
                            ) {
                                Text("توليد ميزانية", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Explanatory Banner: Direct expense-budget linkage
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE9FE))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = null,
                            tint = PurplePrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "جميع مصروفاتك اليومية ترتبط تلقائياً بهذه البنود لتحديث المبلغ المتبقي فوريًا وتنبيهك قبل تجاوز الحد!",
                            fontSize = 12.sp,
                            color = PurpleDark,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Categories list
            items(budgetCategories, key = { it.id }) { cat ->
                val spent = remember(transactions, cat.id) {
                    transactions.filter { it.type == "EXPENSE" && it.budgetCategoryId == cat.id }
                        .sumOf { it.amount }
                }
                BudgetCategoryProgressCard(
                    category = cat,
                    spentAmount = spent,
                    onDelete = { viewModel.deleteBudgetCategory(cat) }
                )
            }
        }
    }

    if (showAddCategoryDialog) {
        AddBudgetCategoryDialog(
            onDismiss = { showAddCategoryDialog = false },
            onSave = { name, amount, percentage ->
                viewModel.addBudgetCategory(name, amount, percentage, "#6B46C1")
                showAddCategoryDialog = false
            }
        )
    }

    if (showAiBudgetProposalDialog) {
        AiBudgetProposalDialog(
            currentIncome = if (totalIncome > 0) totalIncome else 3000.0,
            isLoading = isSmartBudgetLoading,
            onDismiss = { showAiBudgetProposalDialog = false },
            onGenerateAndApply = { salary, savingsPct ->
                viewModel.generateAndApplySmartBudget(salary, savingsPct)
                showAiBudgetProposalDialog = false
            }
        )
    }
}

@Composable
fun AiBudgetProposalDialog(
    currentIncome: Double,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onGenerateAndApply: (salary: Double, savingsPct: Double) -> Unit
) {
    var salaryText by remember { mutableStateOf(currentIncome.toInt().toString()) }
    var savingsPct by remember { mutableStateOf(15.0) } // Default 15% savings

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val salary = salaryText.toDoubleOrNull() ?: 3000.0
                    onGenerateAndApply(salary, savingsPct)
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                modifier = Modifier.testTag("apply_ai_budget_btn")
            ) {
                Text("تطبيق الميزانية المقترحة فوراً", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AmberGold)
                Spacer(modifier = Modifier.width(6.dp))
                Text("المولد الذكي للميزانية", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "سيقوم الذكاء الاصطناعي بتقسيم راتبك الشهري لتغطية احتياجاتك مع ضمان ادخار النسبة التي تختارها وتوزيع الفائض بدقة:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = salaryText,
                    onValueChange = { salaryText = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("الراتب / الدخل الشهري (ر.س)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text(
                    text = "نسبة الادخار الإلزامية المستهدفة: ${savingsPct.toInt()}%",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = PurplePrimary
                )

                Slider(
                    value = savingsPct.toFloat(),
                    onValueChange = { savingsPct = it.toDouble() },
                    valueRange = 10f..40f,
                    steps = 5,
                    colors = SliderDefaults.colors(thumbColor = PurplePrimary, activeTrackColor = PurplePrimary)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("10% (الحد الأدنى)", fontSize = 10.sp, color = Color.Gray)
                    Text("20% (المثالي)", fontSize = 10.sp, color = EmeraldDark, fontWeight = FontWeight.Bold)
                    Text("40% (تحدي المليونير)", fontSize = 10.sp, color = AmberGold, fontWeight = FontWeight.Bold)
                }
            }
        }
    )
}

@Composable
fun BudgetCategoryProgressCard(
    category: BudgetCategoryEntity,
    spentAmount: Double,
    onDelete: () -> Unit
) {
    val progress = if (category.allocatedAmount > 0) {
        (spentAmount / category.allocatedAmount).toFloat()
    } else 0f

    val remaining = category.allocatedAmount - spentAmount
    val isOverBudget = remaining < 0

    val barColor = when {
        isOverBudget -> RoseRed
        progress > 0.85f -> AmberGold
        else -> EmeraldGreen
    }

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
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(barColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isOverBudget) Icons.Default.Warning else Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = barColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = category.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "المخصص: ${category.allocatedAmount.toInt()} ر.س (${category.allocationPercentage.toInt()}%)",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "حذف",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = barColor,
                trackColor = Color(0xFFE2E8F0)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "تم صرف: ${String.format("%.1f", spentAmount)} ر.س",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (isOverBudget) "تجاوزت بـ ${String.format("%.1f", -remaining)} ر.س!" else "المتبقي: ${String.format("%.1f", remaining)} ر.س",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isOverBudget) RoseRed else EmeraldDark
                )
            }
        }
    }
}

@Composable
fun AddBudgetCategoryDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, amount: Double, percentage: Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var percentageText by remember { mutableStateOf("20") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    val pct = percentageText.toDoubleOrNull() ?: 20.0
                    if (name.isNotBlank() && amount > 0) {
                        onSave(name, amount, pct)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                modifier = Modifier.testTag("save_budget_category_btn")
            ) {
                Text("حفظ البند", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        },
        title = {
            Text("إضافة بند ميزانية مخصص", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم البند (مثلاً: رغبات، ادخار)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("المبلغ المخصص (ر.س)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = percentageText,
                    onValueChange = { percentageText = it.filter { c -> c.isDigit() } },
                    label = { Text("النسبة المئوية التقريبية (%)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }
    )
}
