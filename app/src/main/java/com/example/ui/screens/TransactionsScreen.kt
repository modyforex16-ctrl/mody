package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.BudgetCategoryEntity
import com.example.data.local.TransactionEntity
import com.example.ui.theme.*
import com.example.viewmodel.FinancialViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    viewModel: FinancialViewModel,
    showAddDialogInitially: Boolean = false,
    initialType: String = "EXPENSE"
) {
    val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val budgetCategories by viewModel.budgetCategories.collectAsStateWithLifecycle()

    var selectedFilter by remember { mutableStateOf("ALL") } // "ALL", "EXPENSE", "INCOME"
    var showAddDialog by remember { mutableStateOf(showAddDialogInitially) }
    var dialogType by remember { mutableStateOf(initialType) }

    val filteredList = remember(allTransactions, selectedFilter) {
        when (selectedFilter) {
            "EXPENSE" -> allTransactions.filter { it.type == "EXPENSE" }
            "INCOME" -> allTransactions.filter { it.type == "INCOME" }
            else -> allTransactions
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    dialogType = "EXPENSE"
                    showAddDialog = true
                },
                containerColor = PurplePrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_transaction_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "إضافة معاملة")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Screen Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "سجل المعاملات المالية",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${filteredList.size} عملية",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Filter Tabs (الكل، المصروفات، الدخل)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == "ALL",
                    onClick = { selectedFilter = "ALL" },
                    label = { Text("الكل") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PurplePrimary,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.weight(1f).testTag("filter_all_btn")
                )
                FilterChip(
                    selected = selectedFilter == "EXPENSE",
                    onClick = { selectedFilter = "EXPENSE" },
                    label = { Text("المصروفات") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RoseRed,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.weight(1f).testTag("filter_expense_btn")
                )
                FilterChip(
                    selected = selectedFilter == "INCOME",
                    onClick = { selectedFilter = "INCOME" },
                    label = { Text("الدخل") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EmeraldDark,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.weight(1f).testTag("filter_income_btn")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "لا توجد معاملات مسجلة في هذا القسم",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredList, key = { it.id }) { tx ->
                        TransactionDetailCard(
                            transaction = tx,
                            budgetCategory = budgetCategories.find { it.id == tx.budgetCategoryId },
                            onDelete = { viewModel.deleteTransaction(tx) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddTransactionDialog(
            initialType = dialogType,
            budgetCategories = budgetCategories,
            onDismiss = { showAddDialog = false },
            onSave = { type, category, amount, note, method, budgetCatId ->
                if (type == "EXPENSE") {
                    viewModel.addExpense(category, amount, note, method, budgetCatId)
                } else {
                    viewModel.addIncome(category, amount, note, method)
                }
                showAddDialog = false
            }
        )
    }
}

@Composable
fun TransactionDetailCard(
    transaction: TransactionEntity,
    budgetCategory: BudgetCategoryEntity?,
    onDelete: () -> Unit
) {
    val isExpense = transaction.type == "EXPENSE"
    val dateStr = remember(transaction.date) {
        SimpleDateFormat("yyyy/MM/dd • hh:mm a", Locale.getDefault()).format(Date(transaction.date))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(if (isExpense) Color(0xFFFEE2E2) else Color(0xFFD1FAE5)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isExpense) Icons.Default.Payments else Icons.Default.Savings,
                        contentDescription = null,
                        tint = if (isExpense) RoseRed else EmeraldDark,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = transaction.category,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    if (transaction.note.isNotBlank()) {
                        Text(
                            text = transaction.note,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = dateStr,
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                        if (budgetCategory != null) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFEDE9FE))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = budgetCategory.name,
                                    fontSize = 10.sp,
                                    color = PurplePrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${if (isExpense) "-" else "+"}${String.format("%.2f", transaction.amount)} ر.س",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isExpense) RoseRed else EmeraldDark
                    )
                    Text(
                        text = transaction.paymentMethod,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "حذف",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
    initialType: String,
    budgetCategories: List<BudgetCategoryEntity>,
    onDismiss: () -> Unit,
    onSave: (type: String, category: String, amount: Double, note: String, method: String, budgetCatId: Long?) -> Unit
) {
    var type by remember { mutableStateOf(initialType) } // "EXPENSE" or "INCOME"
    var amountText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(if (type == "EXPENSE") "طعام ومشروبات" else "الراتب الشهري") }
    var note by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("بطاقة بنكية") }
    var selectedBudgetCatId by remember { mutableStateOf<Long?>(budgetCategories.firstOrNull()?.id) }

    val expenseCategories = listOf("طعام ومشروبات", "سكن وفواتير", "مواصلات", "تسوق ومشتريات", "صحة ودواء", "ترفيه", "تعليم", "أخرى")
    val incomeCategories = listOf("الراتب الشهري", "عمل حر (فريلانس)", "استثمارات وعوائد", "تجارة وأرباح", "مكافأة", "هدية", "أخرى")
    val paymentMethods = listOf("بطاقة بنكية", "نقد / كاش", "تحويل إلكتروني", "Apple/Mada Pay")

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    if (amount > 0) {
                        onSave(type, selectedCategory, amount, note, paymentMethod, if (type == "EXPENSE") selectedBudgetCatId else null)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                modifier = Modifier.testTag("save_transaction_button")
            ) {
                Text("حفظ المعاملة", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        },
        title = {
            Text(
                text = if (type == "EXPENSE") "إضافة مصروف جديد" else "تسجيل دخل جديد",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Type switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            type = "EXPENSE"
                            selectedCategory = "طعام ومشروبات"
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (type == "EXPENSE") RoseRed else Color(0xFFE2E8F0),
                            contentColor = if (type == "EXPENSE") Color.White else Color(0xFF475569)
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("مصروف 🔻")
                    }
                    Button(
                        onClick = {
                            type = "INCOME"
                            selectedCategory = "الراتب الشهري"
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (type == "INCOME") EmeraldDark else Color(0xFFE2E8F0),
                            contentColor = if (type == "INCOME") Color.White else Color(0xFF475569)
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("دخل 🔺")
                    }
                }

                // Amount
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { char -> char.isDigit() || char == '.' } },
                    label = { Text("المبلغ (ر.س)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().testTag("transaction_amount_input"),
                    singleLine = true
                )

                // Category chips and custom category input
                Text("الفئة / البند (اختر أو اكتب بنداً مخصصاً):", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = { selectedCategory = it },
                    label = { Text("اسم البند / الفئة") },
                    modifier = Modifier.fillMaxWidth().testTag("custom_category_input"),
                    singleLine = true
                )
                val currentCats = if (type == "EXPENSE") expenseCategories else incomeCategories
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    currentCats.take(3).forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }

                // Link to Budget Category (only for expense)
                if (type == "EXPENSE" && budgetCategories.isNotEmpty()) {
                    Text("ربط ببند الميزانية:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        budgetCategories.forEach { bCat ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { selectedBudgetCatId = bCat.id }
                                    .padding(vertical = 4.dp, horizontal = 6.dp)
                            ) {
                                RadioButton(
                                    selected = selectedBudgetCatId == bCat.id,
                                    onClick = { selectedBudgetCatId = bCat.id }
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(bCat.name, fontSize = 12.sp)
                            }
                        }
                    }
                }

                // Note
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("ملاحظة أو بيان (اختياري)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }
    )
}
