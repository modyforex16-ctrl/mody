package com.example.data.repository

import com.example.data.local.*
import kotlinx.coroutines.flow.Flow

class FinancialRepository(
    private val dao: FinancialDao,
    val adminSettings: AdminSettingsManager
) {
    // Transactions
    val allTransactions: Flow<List<TransactionEntity>> = dao.getAllTransactions()
    val totalExpenses: Flow<Double?> = dao.getTotalExpenses()
    val totalIncome: Flow<Double?> = dao.getTotalIncome()

    suspend fun addTransaction(transaction: TransactionEntity): Long = dao.insertTransaction(transaction)
    suspend fun deleteTransaction(transaction: TransactionEntity) = dao.deleteTransaction(transaction)
    suspend fun clearTransactions() = dao.clearAllTransactions()

    // Budget
    val budgetCategories: Flow<List<BudgetCategoryEntity>> = dao.getAllBudgetCategories()
    fun getSpentForBudgetCategory(categoryId: Long): Flow<Double?> = dao.getSpentAmountForCategory(categoryId)

    suspend fun addBudgetCategory(category: BudgetCategoryEntity): Long = dao.insertBudgetCategory(category)
    suspend fun updateBudgetCategory(category: BudgetCategoryEntity) = dao.updateBudgetCategory(category)
    suspend fun deleteBudgetCategory(category: BudgetCategoryEntity) = dao.deleteBudgetCategory(category)

    // Debts
    val allDebts: Flow<List<DebtEntity>> = dao.getAllDebts()
    val totalDebtsOwedByMe: Flow<Double?> = dao.getTotalDebtsOwedByMe()
    val totalDebtsOwedToMe: Flow<Double?> = dao.getTotalDebtsOwedToMe()

    suspend fun addDebt(debt: DebtEntity): Long = dao.insertDebt(debt)
    suspend fun updateDebt(debt: DebtEntity) = dao.updateDebt(debt)
    suspend fun deleteDebt(debt: DebtEntity) = dao.deleteDebt(debt)

    // Challenges
    val allChallenges: Flow<List<SavingsChallengeEntity>> = dao.getAllChallenges()
    suspend fun addChallenge(challenge: SavingsChallengeEntity): Long = dao.insertChallenge(challenge)
    suspend fun updateChallenge(challenge: SavingsChallengeEntity) = dao.updateChallenge(challenge)
    suspend fun deleteChallenge(challenge: SavingsChallengeEntity) = dao.deleteChallenge(challenge)
}
