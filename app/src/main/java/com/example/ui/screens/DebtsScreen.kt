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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.DebtEntity
import com.example.ui.theme.*
import com.example.viewmodel.FinancialViewModel

@Composable
fun DebtsScreen(
    viewModel: FinancialViewModel,
    showAddInitially: Boolean = false
) {
    val allDebts by viewModel.allDebts.collectAsStateWithLifecycle()
    val totalOwedByMe by viewModel.totalDebtsOwedByMe.collectAsStateWithLifecycle()
    val totalOwedToMe by viewModel.totalDebtsOwedToMe.collectAsStateWithLifecycle()

    var activeTab by remember { mutableStateOf("OWED_BY_ME") } // "OWED_BY_ME" (عليّ) or "OWED_TO_ME" (لي)
    var showAddDialog by remember { mutableStateOf(showAddInitially) }
    var selectedDebtForPayment by remember { mutableStateOf<DebtEntity?>(null) }

    val filteredDebts = remember(allDebts, activeTab) {
        allDebts.filter { it.type == activeTab }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = PurplePrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_debt_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "تسجيل دين")
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

            // Debt Summary Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ArrowOutward, contentDescription = null, tint = RoseRed, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ديون عليّ للآخرين", fontSize = 11.sp, color = RoseRed, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${String.format("%.1f", totalOwedByMe)} ر.س",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF991B1B)
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFD1FAE5))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.SouthEast, contentDescription = null, tint = EmeraldDark, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ديون لي عند الغير", fontSize = 11.sp, color = EmeraldDark, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${String.format("%.1f", totalOwedToMe)} ر.س",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = EmeraldDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tab switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = activeTab == "OWED_BY_ME",
                    onClick = { activeTab = "OWED_BY_ME" },
                    label = { Text("ديون عليّ (التزامات)", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RoseRed,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.weight(1f).testTag("debts_owed_by_me_tab")
                )
                FilterChip(
                    selected = activeTab == "OWED_TO_ME",
                    onClick = { activeTab = "OWED_TO_ME" },
                    label = { Text("ديون لي (مستحقات)", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EmeraldDark,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.weight(1f).testTag("debts_owed_to_me_tab")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredDebts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CheckCircleOutline,
                            contentDescription = null,
                            tint = EmeraldGreen,
                            modifier = Modifier.size(52.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (activeTab == "OWED_BY_ME") "لا توجد ديون عليك حالياً، ممتاز!" else "لا توجد ديون لك عند الآخرين",
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
                    items(filteredDebts, key = { it.id }) { debt ->
                        DebtItemCard(
                            debt = debt,
                            onRecordPayment = { selectedDebtForPayment = debt },
                            onDelete = { viewModel.deleteDebt(debt) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddDebtDialog(
            defaultType = activeTab,
            onDismiss = { showAddDialog = false },
            onSave = { person, type, amount, notes ->
                viewModel.addDebt(person, type, amount, notes)
                showAddDialog = false
            }
        )
    }

    selectedDebtForPayment?.let { debt ->
        RecordPaymentDialog(
            debt = debt,
            onDismiss = { selectedDebtForPayment = null },
            onConfirm = { amount ->
                viewModel.recordDebtPayment(debt, amount)
                selectedDebtForPayment = null
            }
        )
    }
}

@Composable
fun DebtItemCard(
    debt: DebtEntity,
    onRecordPayment: () -> Unit,
    onDelete: () -> Unit
) {
    val remaining = (debt.totalAmount - debt.paidAmount).coerceAtLeast(0.0)
    val progress = if (debt.totalAmount > 0) (debt.paidAmount / debt.totalAmount).toFloat() else 1f
    val isOwedByMe = debt.type == "OWED_BY_ME"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (isOwedByMe) Color(0xFFFEE2E2) else Color(0xFFD1FAE5)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = if (isOwedByMe) RoseRed else EmeraldDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = debt.personName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        if (debt.notes.isNotBlank()) {
                            Text(debt.notes, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (debt.isSettled) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFD1FAE5))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("مسدد بالكامل ✓", color = EmeraldDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = onRecordPayment,
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
                        ) {
                            Text(if (isOwedByMe) "سداد دفعة" else "تحصيل دفعة", fontSize = 11.sp)
                        }
                    }

                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "حذف", tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (debt.isSettled) EmeraldGreen else AmberGold,
                trackColor = Color(0xFFE2E8F0)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "سُدد: ${String.format("%.1f", debt.paidAmount)} من ${String.format("%.1f", debt.totalAmount)} ر.س",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "المتبقي: ${String.format("%.1f", remaining)} ر.س",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (debt.isSettled) EmeraldDark else RoseRed
                )
            }
        }
    }
}

@Composable
fun AddDebtDialog(
    defaultType: String,
    onDismiss: () -> Unit,
    onSave: (person: String, type: String, amount: Double, notes: String) -> Unit
) {
    var personName by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(defaultType) }
    var amountText by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    if (personName.isNotBlank() && amount > 0) {
                        onSave(personName, type, amount, notes)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
            ) {
                Text("حفظ الدين", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        },
        title = { Text("تسجيل دين جديد", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { type = "OWED_BY_ME" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (type == "OWED_BY_ME") RoseRed else Color(0xFFE2E8F0),
                            contentColor = if (type == "OWED_BY_ME") Color.White else Color(0xFF475569)
                        ),
                        modifier = Modifier.weight(1f)
                    ) { Text("دين عليّ 🔴") }

                    Button(
                        onClick = { type = "OWED_TO_ME" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (type == "OWED_TO_ME") EmeraldDark else Color(0xFFE2E8F0),
                            contentColor = if (type == "OWED_TO_ME") Color.White else Color(0xFF475569)
                        ),
                        modifier = Modifier.weight(1f)
                    ) { Text("دين لي 🟢") }
                }

                OutlinedTextField(
                    value = personName,
                    onValueChange = { personName = it },
                    label = { Text("اسم الشخص أو الجهة") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("إجمالي المبلغ (ر.س)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("ملاحظات (السبب أو الموعد)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }
    )
}

@Composable
fun RecordPaymentDialog(
    debt: DebtEntity,
    onDismiss: () -> Unit,
    onConfirm: (amount: Double) -> Unit
) {
    val remaining = (debt.totalAmount - debt.paidAmount).coerceAtLeast(0.0)
    var paymentText by remember { mutableStateOf(remaining.toInt().toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val amount = paymentText.toDoubleOrNull() ?: 0.0
                    if (amount > 0) {
                        onConfirm(amount)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark)
            ) {
                Text("تأكيد السداد", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        },
        title = { Text("تسجيل سداد دفعة", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("سداد جزء أو كل الدين الخاص بـ: ${debt.personName}", fontSize = 13.sp)
                Text("المبلغ المتبقي حالياً: ${String.format("%.1f", remaining)} ر.س", fontSize = 12.sp, color = PurplePrimary, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = paymentText,
                    onValueChange = { paymentText = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("مبلغ السداد المدفوع (ر.س)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }
    )
}
