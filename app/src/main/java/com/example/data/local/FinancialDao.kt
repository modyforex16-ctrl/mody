package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FinancialDao {

    // --- Transactions ---
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date DESC")
    fun getTransactionsByType(type: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE budgetCategoryId = :categoryId ORDER BY date DESC")
    fun getTransactionsByBudgetCategory(categoryId: Long): Flow<List<TransactionEntity>>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'EXPENSE'")
    fun getTotalExpenses(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'INCOME'")
    fun getTotalIncome(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'EXPENSE' AND budgetCategoryId = :categoryId")
    fun getSpentAmountForCategory(categoryId: Long): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions")
    suspend fun clearAllTransactions()

    // --- Budget Categories ---
    @Query("SELECT * FROM budget_categories ORDER BY id ASC")
    fun getAllBudgetCategories(): Flow<List<BudgetCategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudgetCategory(category: BudgetCategoryEntity): Long

    @Update
    suspend fun updateBudgetCategory(category: BudgetCategoryEntity)

    @Delete
    suspend fun deleteBudgetCategory(category: BudgetCategoryEntity)

    @Query("SELECT COUNT(*) FROM budget_categories")
    suspend fun getBudgetCategoriesCount(): Int

    // --- Debts ---
    @Query("SELECT * FROM debts ORDER BY createdAt DESC")
    fun getAllDebts(): Flow<List<DebtEntity>>

    @Query("SELECT * FROM debts WHERE type = :type ORDER BY createdAt DESC")
    fun getDebtsByType(type: String): Flow<List<DebtEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebt(debt: DebtEntity): Long

    @Update
    suspend fun updateDebt(debt: DebtEntity)

    @Delete
    suspend fun deleteDebt(debt: DebtEntity)

    @Query("SELECT SUM(totalAmount - paidAmount) FROM debts WHERE type = 'OWED_BY_ME' AND isSettled = 0")
    fun getTotalDebtsOwedByMe(): Flow<Double?>

    @Query("SELECT SUM(totalAmount - paidAmount) FROM debts WHERE type = 'OWED_TO_ME' AND isSettled = 0")
    fun getTotalDebtsOwedToMe(): Flow<Double?>

    // --- Savings Challenges ---
    @Query("SELECT * FROM savings_challenges ORDER BY isCompleted ASC, id ASC")
    fun getAllChallenges(): Flow<List<SavingsChallengeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenge(challenge: SavingsChallengeEntity): Long

    @Update
    suspend fun updateChallenge(challenge: SavingsChallengeEntity)

    @Delete
    suspend fun deleteChallenge(challenge: SavingsChallengeEntity)

    @Query("SELECT COUNT(*) FROM savings_challenges")
    suspend fun getChallengesCount(): Int
}
