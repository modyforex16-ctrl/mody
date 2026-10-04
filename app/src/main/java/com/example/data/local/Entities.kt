package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // "EXPENSE" or "INCOME"
    val category: String,
    val amount: Double,
    val note: String = "",
    val date: Long = System.currentTimeMillis(),
    val paymentMethod: String = "نقد",
    val budgetCategoryId: Long? = null
)

@Entity(tableName = "budget_categories")
data class BudgetCategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val allocatedAmount: Double,
    val allocationPercentage: Double,
    val colorHex: String = "#6B46C1",
    val iconName: String = "category"
)

@Entity(tableName = "debts")
data class DebtEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val personName: String,
    val type: String, // "OWED_BY_ME" (دين عليّ) or "OWED_TO_ME" (دين لي)
    val totalAmount: Double,
    val paidAmount: Double = 0.0,
    val dueDate: Long? = null,
    val notes: String = "",
    val isSettled: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "savings_challenges")
data class SavingsChallengeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val type: String, // "DAILY", "WEEKLY_52", "ROUND_UP", "CUSTOM"
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val durationDays: Int = 30,
    val startDate: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false,
    val colorHex: String = "#6B46C1"
)
