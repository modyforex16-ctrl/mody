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
import com.example.data.local.SavingsChallengeEntity
import com.example.ui.theme.*
import com.example.viewmodel.FinancialViewModel

@Composable
fun ChallengesScreen(
    viewModel: FinancialViewModel
) {
    val challenges by viewModel.allChallenges.collectAsStateWithLifecycle()
    var selectedChallengeForAdd by remember { mutableStateOf<SavingsChallengeEntity?>(null) }
    var showCreateChallengeDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateChallengeDialog = true },
                containerColor = PurplePrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("create_challenge_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "تحدي جديد")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(AmberLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = AmberGold, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "تحديات الادخار الممتعة 🏆",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = "حوّل عاداتك المالية إلى إنجازات مستمرة",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            items(challenges, key = { it.id }) { ch ->
                FullChallengeCard(
                    challenge = ch,
                    onAddSavings = { selectedChallengeForAdd = ch },
                    onDelete = { viewModel.deleteChallenge(ch) }
                )
            }
        }
    }

    selectedChallengeForAdd?.let { ch ->
        AddSavingsToChallengeDialog(
            challenge = ch,
            onDismiss = { selectedChallengeForAdd = null },
            onConfirm = { amount ->
                viewModel.updateChallengeProgress(ch, amount)
                viewModel.addExpense(
                    category = "ادخار وتحديات",
                    amount = amount,
                    note = "مساهمة في: ${ch.title}",
                    paymentMethod = "نقد",
                    budgetCategoryId = null
                )
                selectedChallengeForAdd = null
            }
        )
    }

    if (showCreateChallengeDialog) {
        CreateCustomChallengeDialog(
            onDismiss = { showCreateChallengeDialog = false },
            onSave = { title, target, days ->
                viewModel.addSavingsChallenge(title, "CUSTOM", target, days, "#10B981")
                showCreateChallengeDialog = false
            }
        )
    }
}

@Composable
fun FullChallengeCard(
    challenge: SavingsChallengeEntity,
    onAddSavings: () -> Unit,
    onDelete: () -> Unit
) {
    val progress = if (challenge.targetAmount > 0) (challenge.currentAmount / challenge.targetAmount).toFloat() else 1f
    val remaining = (challenge.targetAmount - challenge.currentAmount).coerceAtLeast(0.0)

    Card(
        modifier = Modifier.fillMaxWidth(),
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
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(PurplePrimary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (challenge.type) {
                                "DAILY" -> Icons.Default.CalendarToday
                                "WEEKLY_52" -> Icons.Default.MilitaryTech
                                "ROUND_UP" -> Icons.Default.FilterVintage
                                else -> Icons.Default.Flag
                            },
                            contentDescription = null,
                            tint = PurplePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = challenge.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "المدة: ${challenge.durationDays} يوماً",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (challenge.isCompleted) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFD1FAE5))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("مكتمل 🏆", color = EmeraldDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = onAddSavings,
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
                        ) {
                            Text("+ ادخار", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "حذف", tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (challenge.isCompleted) EmeraldGreen else PurplePrimary,
                trackColor = Color(0xFFE2E8F0)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "المحقق: ${challenge.currentAmount.toInt()} من ${challenge.targetAmount.toInt()} ر.س (${(progress * 100).toInt()}%)",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (challenge.isCompleted) "تم تحقيق الهدف!" else "متبقي: ${remaining.toInt()} ر.س",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (challenge.isCompleted) EmeraldDark else PurplePrimary
                )
            }
        }
    }
}

@Composable
fun AddSavingsToChallengeDialog(
    challenge: SavingsChallengeEntity,
    onDismiss: () -> Unit,
    onConfirm: (amount: Double) -> Unit
) {
    var amountText by remember { mutableStateOf("25") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    if (amount > 0) {
                        onConfirm(amount)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
            ) {
                Text("إيداع في التحدي", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        },
        title = { Text("إضافة مبلغ للتحدي", fontWeight = FontWeight.Bold, fontSize = 17.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("تحدي: ${challenge.title}", fontSize = 13.sp)
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("مبلغ الادخار المضاف (ر.س)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }
    )
}

@Composable
fun CreateCustomChallengeDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, target: Double, days: Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var targetText by remember { mutableStateOf("") }
    var daysText by remember { mutableStateOf("30") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val target = targetText.toDoubleOrNull() ?: 0.0
                    val days = daysText.toIntOrNull() ?: 30
                    if (title.isNotBlank() && target > 0) {
                        onSave(title, target, days)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
            ) {
                Text("إنشاء التحدي", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        },
        title = { Text("إنشاء تحدي ادخار جديد", fontWeight = FontWeight.Bold, fontSize = 17.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان التحدي (مثلاً: ادخار تذكرة سفر)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = targetText,
                    onValueChange = { targetText = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("المبلغ المستهدف (ر.س)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = daysText,
                    onValueChange = { daysText = it.filter { c -> c.isDigit() } },
                    label = { Text("مدة التحدي بالأيام") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }
    )
}
