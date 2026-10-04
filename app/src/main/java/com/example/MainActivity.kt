package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.BottomFixedAdBanner
import com.example.ui.components.TopFixedAdBanner
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PurplePrimary
import com.example.viewmodel.FinancialViewModel
import com.example.viewmodel.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                // Ensure RTL layout for authentic Arabic financial experience
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    MillionaireApp()
                }
            }
        }
    }
}

@Composable
fun MillionaireApp(
    viewModel: FinancialViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val bannerTopId by viewModel.adminSettings.bannerTopId.collectAsStateWithLifecycle()
    val bannerBottomId by viewModel.adminSettings.bannerBottomId.collectAsStateWithLifecycle()
    val adsEnabled by viewModel.adminSettings.adsEnabled.collectAsStateWithLifecycle()
    val isTestMode by viewModel.adminSettings.testMode.collectAsStateWithLifecycle()

    var showExpenseDialog by remember { mutableStateOf(false) }
    var showIncomeDialog by remember { mutableStateOf(false) }
    var showDebtDialog by remember { mutableStateOf(false) }

    val budgetCategories by viewModel.budgetCategories.collectAsStateWithLifecycle()

    // Handle back button for sub-screens
    BackHandler(enabled = currentScreen != Screen.Home) {
        viewModel.navigateBack()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            Column {
                // Top Fixed Banner Ad: Never overlaps content
                TopFixedAdBanner(
                    bannerId = bannerTopId,
                    isEnabled = adsEnabled,
                    isTestMode = isTestMode
                )
            }
        },
        bottomBar = {
            Column {
                // Bottom Fixed Banner Ad: Fixed height, above Navigation bar, never overlaps content
                BottomFixedAdBanner(
                    bannerId = bannerBottomId,
                    isEnabled = adsEnabled,
                    isTestMode = isTestMode
                )

                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    NavigationBarItem(
                        selected = currentScreen == Screen.Home,
                        onClick = { viewModel.navigateTo(Screen.Home) },
                        icon = {
                            Icon(
                                if (currentScreen == Screen.Home) Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = "الرئيسية"
                            )
                        },
                        label = { Text("الرئيسية", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PurplePrimary,
                            selectedTextColor = PurplePrimary,
                            indicatorColor = Color(0xFFEDE9FE)
                        ),
                        modifier = Modifier.testTag("nav_home")
                    )

                    NavigationBarItem(
                        selected = currentScreen == Screen.Transactions,
                        onClick = { viewModel.navigateTo(Screen.Transactions) },
                        icon = {
                            Icon(
                                if (currentScreen == Screen.Transactions) Icons.Filled.ReceiptLong else Icons.Outlined.ReceiptLong,
                                contentDescription = "المعاملات"
                            )
                        },
                        label = { Text("المعاملات", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PurplePrimary,
                            selectedTextColor = PurplePrimary,
                            indicatorColor = Color(0xFFEDE9FE)
                        ),
                        modifier = Modifier.testTag("nav_transactions")
                    )

                    NavigationBarItem(
                        selected = currentScreen == Screen.Budget,
                        onClick = { viewModel.navigateTo(Screen.Budget) },
                        icon = {
                            Icon(
                                if (currentScreen == Screen.Budget) Icons.Filled.PieChart else Icons.Outlined.PieChart,
                                contentDescription = "الميزانية"
                            )
                        },
                        label = { Text("الميزانية", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PurplePrimary,
                            selectedTextColor = PurplePrimary,
                            indicatorColor = Color(0xFFEDE9FE)
                        ),
                        modifier = Modifier.testTag("nav_budget")
                    )

                    NavigationBarItem(
                        selected = currentScreen == Screen.Debts,
                        onClick = { viewModel.navigateTo(Screen.Debts) },
                        icon = {
                            Icon(
                                if (currentScreen == Screen.Debts) Icons.Filled.AccountBalance else Icons.Outlined.AccountBalance,
                                contentDescription = "الديون"
                            )
                        },
                        label = { Text("الديون", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PurplePrimary,
                            selectedTextColor = PurplePrimary,
                            indicatorColor = Color(0xFFEDE9FE)
                        ),
                        modifier = Modifier.testTag("nav_debts")
                    )

                    NavigationBarItem(
                        selected = currentScreen == Screen.Zakat,
                        onClick = { viewModel.navigateTo(Screen.Zakat) },
                        icon = {
                            Icon(
                                if (currentScreen == Screen.Zakat) Icons.Filled.Mosque else Icons.Outlined.Mosque,
                                contentDescription = "الزكاة"
                            )
                        },
                        label = { Text("الزكاة", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PurplePrimary,
                            selectedTextColor = PurplePrimary,
                            indicatorColor = Color(0xFFEDE9FE)
                        ),
                        modifier = Modifier.testTag("nav_zakat")
                    )

                    NavigationBarItem(
                        selected = currentScreen == Screen.AiAdvisor,
                        onClick = { viewModel.navigateTo(Screen.AiAdvisor) },
                        icon = {
                            Icon(
                                if (currentScreen == Screen.AiAdvisor) Icons.Filled.AutoAwesome else Icons.Outlined.AutoAwesome,
                                contentDescription = "المستشار"
                            )
                        },
                        label = { Text("المستشار", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PurplePrimary,
                            selectedTextColor = PurplePrimary,
                            indicatorColor = Color(0xFFEDE9FE)
                        ),
                        modifier = Modifier.testTag("nav_ai")
                    )

                    NavigationBarItem(
                        selected = currentScreen == Screen.Rewards,
                        onClick = { viewModel.navigateTo(Screen.Rewards) },
                        icon = {
                            Icon(
                                if (currentScreen == Screen.Rewards) Icons.Filled.Stars else Icons.Outlined.Stars,
                                contentDescription = "المكافآت"
                            )
                        },
                        label = { Text("المكافآت", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PurplePrimary,
                            selectedTextColor = PurplePrimary,
                            indicatorColor = Color(0xFFEDE9FE)
                        ),
                        modifier = Modifier.testTag("nav_rewards")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (currentScreen) {
                is Screen.Home -> HomeScreen(
                    viewModel = viewModel,
                    onOpenAddExpense = { showExpenseDialog = true },
                    onOpenAddIncome = { showIncomeDialog = true },
                    onOpenAddDebt = { showDebtDialog = true }
                )
                is Screen.Transactions -> TransactionsScreen(viewModel = viewModel)
                is Screen.Budget -> BudgetScreen(viewModel = viewModel)
                is Screen.Debts -> DebtsScreen(viewModel = viewModel)
                is Screen.Zakat -> ZakatScreen(viewModel = viewModel)
                is Screen.AiAdvisor -> AiAdvisorScreen(viewModel = viewModel)
                is Screen.Challenges -> ChallengesScreen(viewModel = viewModel)
                is Screen.Rewards -> RewardsScreen(viewModel = viewModel)
                is Screen.PointsMarket -> PointsMarketplaceScreen(viewModel = viewModel)
                is Screen.OnlineChallenges -> OnlineChallengesScreen(viewModel = viewModel)
                is Screen.InvestmentGames -> InvestmentGamesScreen(viewModel = viewModel)
                is Screen.Admin -> AdminLoginAndDashboardScreen(
                    viewModel = viewModel,
                    onBack = { viewModel.navigateTo(Screen.Home) }
                )
            }
        }
    }

    // Modal dialogs triggered from Quick Actions on Home
    if (showExpenseDialog) {
        AddTransactionDialog(
            initialType = "EXPENSE",
            budgetCategories = budgetCategories,
            onDismiss = { showExpenseDialog = false },
            onSave = { type, cat, amount, note, method, budgetCatId ->
                viewModel.addExpense(cat, amount, note, method, budgetCatId)
                showExpenseDialog = false
            }
        )
    }

    if (showIncomeDialog) {
        AddTransactionDialog(
            initialType = "INCOME",
            budgetCategories = budgetCategories,
            onDismiss = { showIncomeDialog = false },
            onSave = { type, cat, amount, note, method, budgetCatId ->
                viewModel.addIncome(cat, amount, note, method)
                showIncomeDialog = false
            }
        )
    }

    if (showDebtDialog) {
        AddDebtDialog(
            defaultType = "OWED_BY_ME",
            onDismiss = { showDebtDialog = false },
            onSave = { person, type, amount, notes ->
                viewModel.addDebt(person, type, amount, notes)
                showDebtDialog = false
            }
        )
    }
}
