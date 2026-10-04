package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TransactionEntity::class,
        BudgetCategoryEntity::class,
        DebtEntity::class,
        SavingsChallengeEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun financialDao(): FinancialDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "millionaire_finance_db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.financialDao())
                    }
                }
            }

            suspend fun populateInitialData(dao: FinancialDao) {
                // Initial Default Budget Categories (50/30/20 Rule + Fixed)
                val defaultCategories = listOf(
                    BudgetCategoryEntity(
                        name = "الاحتياجات الأساسية (طعام وسكن)",
                        allocatedAmount = 1500.0,
                        allocationPercentage = 50.0,
                        colorHex = "#3B82F6",
                        iconName = "home"
                    ),
                    BudgetCategoryEntity(
                        name = "الفواتير والمواصلات",
                        allocatedAmount = 400.0,
                        allocationPercentage = 15.0,
                        colorHex = "#F59E0B",
                        iconName = "bolt"
                    ),
                    BudgetCategoryEntity(
                        name = "الرغبات والتسوق والترفيه",
                        allocatedAmount = 450.0,
                        allocationPercentage = 15.0,
                        colorHex = "#EC4899",
                        iconName = "shopping"
                    ),
                    BudgetCategoryEntity(
                        name = "الادخار والاستثمار الذكي (10%+)",
                        allocatedAmount = 650.0,
                        allocationPercentage = 20.0,
                        colorHex = "#10B981",
                        iconName = "savings"
                    )
                )
                for (cat in defaultCategories) {
                    dao.insertBudgetCategory(cat)
                }

                // Initial Savings Challenges
                val defaultChallenges = listOf(
                    SavingsChallengeEntity(
                        title = "التحدي اليومي",
                        type = "DAILY",
                        targetAmount = 300.0,
                        currentAmount = 75.0,
                        durationDays = 30,
                        colorHex = "#10B981"
                    ),
                    SavingsChallengeEntity(
                        title = "تحدي 52 أسبوعاً",
                        type = "WEEKLY_52",
                        targetAmount = 1378.0,
                        currentAmount = 210.0,
                        durationDays = 365,
                        colorHex = "#F59E0B"
                    ),
                    SavingsChallengeEntity(
                        title = "تحدي التقريب لأقرب دينار",
                        type = "ROUND_UP",
                        targetAmount = 150.0,
                        currentAmount = 48.0,
                        durationDays = 60,
                        colorHex = "#EC4899"
                    ),
                    SavingsChallengeEntity(
                        title = "صندوق الطوارئ (هدف 3 أشهر)",
                        type = "CUSTOM",
                        targetAmount = 3000.0,
                        currentAmount = 900.0,
                        durationDays = 90,
                        colorHex = "#6366F1"
                    )
                )
                for (ch in defaultChallenges) {
                    dao.insertChallenge(ch)
                }

                // Initial seed transaction so user immediately sees rich data on first launch
                dao.insertTransaction(
                    TransactionEntity(
                        type = "INCOME",
                        category = "الراتب الشهري",
                        amount = 3000.0,
                        note = "راتب الشهر الحالي",
                        paymentMethod = "تحويل إلكتروني"
                    )
                )
                dao.insertTransaction(
                    TransactionEntity(
                        type = "EXPENSE",
                        category = "طعام ومشروبات",
                        amount = 120.0,
                        note = "مشتريات سوبرماركت أسبوعية",
                        paymentMethod = "بطاقة بنكية",
                        budgetCategoryId = 1
                    )
                )
                dao.insertTransaction(
                    TransactionEntity(
                        type = "EXPENSE",
                        category = "فواتير",
                        amount = 65.0,
                        note = "فاتورة الهاتف والإنترنت",
                        paymentMethod = "بطاقة بنكية",
                        budgetCategoryId = 2
                    )
                )
                dao.insertTransaction(
                    TransactionEntity(
                        type = "EXPENSE",
                        category = "ادخار واستثمار",
                        amount = 300.0,
                        note = "استقطاع ادخار 10% إلى المحفظة الاستثمارية",
                        paymentMethod = "تحويل إلكتروني",
                        budgetCategoryId = 4
                    )
                )

                // Initial Debt item
                dao.insertDebt(
                    DebtEntity(
                        personName = "أحمد خالد",
                        type = "OWED_BY_ME",
                        totalAmount = 500.0,
                        paidAmount = 200.0,
                        notes = "قسط جهاز الحاسوب",
                        isSettled = false
                    )
                )
            }
        }
    }
}
