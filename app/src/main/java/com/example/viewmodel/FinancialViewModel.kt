package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.GeminiFinancialAdvisor
import com.example.data.api.GoldPriceService
import com.example.data.api.LiveGoldRates
import com.example.data.local.*
import com.example.data.repository.FinancialRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class Screen(val title: String) {
    object Home : Screen("الرئيسية")
    object Transactions : Screen("المعاملات")
    object Budget : Screen("الميزانية")
    object Debts : Screen("الديون")
    object Zakat : Screen("حاسبة الزكاة")
    object AiAdvisor : Screen("المستشار الذكي")
    object Challenges : Screen("تحديات الادخار")
    object Rewards : Screen("المكافآت")
    object Admin : Screen("لوحة الإدارة")
    object PointsMarket : Screen("سوق الخدمات")
    object OnlineChallenges : Screen("ساحة المسابقات")
    object InvestmentGames : Screen("ألعاب الاستثمار")
}

data class ZakatState(
    val goldGrams24k: Double = 0.0,
    val goldPricePerGram24k: Double = 328.0,
    val goldGrams21k: Double = 0.0,
    val silverGrams: Double = 0.0,
    val silverPricePerGram: Double = 3.90,
    val cashOnHandAndBank: Double = 0.0,
    val businessMerchandise: Double = 0.0,
    val debtsDueImmediately: Double = 0.0,
    val isHijriCalendar: Boolean = true,
    val zakatRate: Double = 0.025, // 2.5% for Hijri, 2.577% for Gregorian
    val zakatableTotal: Double = 0.0,
    val isNisabReached: Boolean = false,
    val nisabThreshold: Double = 0.0,
    val zakatAmountDue: Double = 0.0
)

data class OnlineTournament(
    val id: String,
    val title: String,
    val description: String,
    val participantCount: Int,
    val prizePoints: Int,
    val daysRemaining: Int,
    val isJoined: Boolean = false
)

data class InvestmentGameState(
    val currentRound: Int = 1,
    val cashBalance: Double = 15000.0,
    val stocksAmount: Double = 0.0,
    val goldAmount: Double = 0.0,
    val sukukAmount: Double = 0.0,
    val realEstateAmount: Double = 0.0,
    val totalPortfolioValue: Double = 15000.0,
    val roiPercentage: Double = 0.0,
    val latestEventHeadline: String = "بدء الجولة الأولى: وزّع رأس مالك المبدئي بحكمة بين الأسهم، الذهب، صكوك المرابحة، وسوق العقار الحقيقي.",
    val wealthTierLevel: Int = 1,
    val wealthTierTitle: String = "مبتدئ مالي",
    val experienceXp: Int = 50,
    val annualRentalIncome: Double = 0.0,
    val totalRentCollected: Double = 0.0,
    val properties: List<RealEstateProperty> = getInitialProperties(),
    val onlineDeals: List<OnlineInvestmentDeal> = getInitialOnlineDeals(),
    val skillMissions: List<InvestmentSkillMission> = getInitialSkillMissions(),
    val expansionCountries: List<InternationalExpansionCountry> = getInitialExpansionCountries()
)

fun getInitialProperties(): List<RealEstateProperty> = listOf(
    RealEstateProperty(
        id = "prop_riyadh_malqa",
        city = "الرياض",
        district = "حي الملقا (شمال الرياض)",
        propertyType = "شقة سكنية ذكية 3 غرف",
        purchasePrice = 520000.0,
        annualRent = 46000.0,
        rentalYieldPercentage = 8.85,
        capitalGrowthRate = 11.2,
        imageIcon = "apartment",
        description = "موقع استراتيجي بالقرب من بوليفارد الرياض والدرعية مع طلب تأجيري مرتفع جداً."
    ),
    RealEstateProperty(
        id = "prop_riyadh_narjis",
        city = "الرياض",
        district = "حي النرجس",
        propertyType = "استوديو استثماري مؤجر",
        purchasePrice = 310000.0,
        annualRent = 27500.0,
        rentalYieldPercentage = 8.87,
        capitalGrowthRate = 9.8,
        imageIcon = "domain",
        description = "مؤجر بعقد سنوي موثق عبر منصة إيجار لشركة تقنية ناشئة."
    ),
    RealEstateProperty(
        id = "prop_jeddah_obhur",
        city = "جدة",
        district = "أبحر الشمالية (واجهة بحرية)",
        propertyType = "شقة ضيافة فندقية مرخصة",
        purchasePrice = 430000.0,
        annualRent = 38000.0,
        rentalYieldPercentage = 8.84,
        capitalGrowthRate = 8.5,
        imageIcon = "villa",
        description = "قريبة من الكورنيش الشمالي مجهزة للتأجير السياحي عبر منصات الضيافة."
    ),
    RealEstateProperty(
        id = "prop_khobar_corniche",
        city = "الخبر",
        district = "حي الكورنيش / العزيزية",
        propertyType = "مكتب تجاري حديث",
        purchasePrice = 460000.0,
        annualRent = 40000.0,
        rentalYieldPercentage = 8.70,
        capitalGrowthRate = 7.9,
        imageIcon = "business",
        description = "برج أعمال مرخص مع مرافق متكاملة ونسبة إشغال تفوق 95%."
    ),
    RealEstateProperty(
        id = "prop_makkah_central",
        city = "مكة المكرمة",
        district = "المنطقة المركزية (قرب الحرم)",
        propertyType = "جناح ضيافة لمواسم الحج والعمرة",
        purchasePrice = 690000.0,
        annualRent = 72000.0,
        rentalYieldPercentage = 10.43,
        capitalGrowthRate = 12.5,
        imageIcon = "hotel",
        description = "عوائد استثنائية في مواسم رمضان والحج مع إدارة تشغيلية فندقية."
    )
)

fun getInitialOnlineDeals(): List<OnlineInvestmentDeal> = listOf(
    OnlineInvestmentDeal(
        id = "deal_riyadh_crowd",
        ownerName = "صندوق الملقا للفرص العقارية",
        title = "تطوير مجمع سكني ذكي بالرياض",
        category = "عقار جماعي",
        cityOrPlatform = "الرياض",
        totalValuation = 1500000.0,
        sharePrice = 500.0,
        availableShares = 420,
        expectedAnnualRoi = 12.8,
        minWealthLevel = 1,
        investorCount = 68
    ),
    OnlineInvestmentDeal(
        id = "deal_ecommerce_ai",
        ownerName = "فهد القحطاني ومستثمرون",
        title = "تمويل متجر إلكتروني بنظام الذكاء الاصطناعي",
        category = "متجر إلكتروني",
        cityOrPlatform = "أونلاين (سلة)",
        totalValuation = 180000.0,
        sharePrice = 250.0,
        availableShares = 110,
        expectedAnnualRoi = 18.5,
        minWealthLevel = 1,
        investorCount = 34
    ),
    OnlineInvestmentDeal(
        id = "deal_sukuk_murabaha",
        ownerName = "منصة صكوك الاستثمار المرخصة",
        title = "صكوك مرابحة تجارية لتمويل عقود حكومية",
        category = "صكوك مرابحة",
        cityOrPlatform = "السعودية",
        totalValuation = 5000000.0,
        sharePrice = 1000.0,
        availableShares = 850,
        expectedAnnualRoi = 9.2,
        minWealthLevel = 2,
        investorCount = 142
    ),
    OnlineInvestmentDeal(
        id = "deal_agency_marketing",
        ownerName = "وكالة 'نمو' للحلول الرقمية",
        title = "توسيع وكالة إعلانات أداء وتجارة إلكترونية",
        category = "مشروع تقني",
        cityOrPlatform = "عن بعد",
        totalValuation = 320000.0,
        sharePrice = 400.0,
        availableShares = 75,
        expectedAnnualRoi = 21.0,
        minWealthLevel = 2,
        investorCount = 19
    )
)

fun getInitialSkillMissions(): List<InvestmentSkillMission> = listOf(
    InvestmentSkillMission(
        id = "mission_cashflow",
        title = "توليد أول تدفق نقدي سلبي",
        skillCategory = "تحليل التدفق النقدي",
        description = "احصل على تدفق إيجاري أو عوائد سنوية لا تقل عن 5,000 ر.س في محفظتك.",
        targetGoal = 5000.0,
        currentProgress = 0.0,
        rewardPoints = 40,
        rewardXp = 100,
        isCompleted = false
    ),
    InvestmentSkillMission(
        id = "mission_diversify",
        title = "التنويع الاستثماري المتوازن",
        skillCategory = "إدارة المخاطر",
        description = "وزّع استثماراتك في 3 فئات أصول مختلفة (أسهم، ذهب، وصكوك أو عقار).",
        targetGoal = 3.0,
        currentProgress = 1.0,
        rewardPoints = 50,
        rewardXp = 120,
        isCompleted = false
    ),
    InvestmentSkillMission(
        id = "mission_real_estate",
        title = "دخول سوق العقارات السعودي",
        skillCategory = "اقتناص الفرص",
        description = "تملك أول عقار استثماري (كاش أو بالتمويل العقاري 20% دفعة أولى).",
        targetGoal = 1.0,
        currentProgress = 0.0,
        rewardPoints = 70,
        rewardXp = 180,
        isCompleted = false
    ),
    InvestmentSkillMission(
        id = "mission_crowd_invest",
        title = "المستثمر الشريك أونلاين",
        skillCategory = "التجارة الجماعية",
        description = "اشترِ حصصاً في صفقة جماعية للمستثمرين لتطوير الشراكات الرقمية.",
        targetGoal = 1.0,
        currentProgress = 0.0,
        rewardPoints = 50,
        rewardXp = 100,
        isCompleted = false
    ),
    InvestmentSkillMission(
        id = "mission_scale_wealth",
        title = "بلوغ صافي ثروة 50,000 ر.س",
        skillCategory = "بناء الثروة",
        description = "نمِّ قيمة محفظتك الإجمالية لتتجاوز 50 ألف ريال والترقية للمستوى الثاني.",
        targetGoal = 50000.0,
        currentProgress = 15000.0,
        rewardPoints = 100,
        rewardXp = 250,
        isCompleted = false
    )
)

class FinancialViewModel(application: Application) : AndroidViewModel(application) {

    val adminSettings: AdminSettingsManager = AdminSettingsManager(application)
    private val repository: FinancialRepository = FinancialRepository(
        AppDatabase.getDatabase(application, viewModelScope).financialDao(),
        adminSettings
    )

    private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val screenStack = mutableListOf<Screen>(Screen.Home)

    // --- Financial Data Flows ---
    val allTransactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalExpenses: StateFlow<Double> = repository.totalExpenses
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalIncome: StateFlow<Double> = repository.totalIncome
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val netBalance: StateFlow<Double> = combine(totalIncome, totalExpenses) { inc, exp ->
        inc - exp
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val budgetCategories: StateFlow<List<BudgetCategoryEntity>> = repository.budgetCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDebts: StateFlow<List<DebtEntity>> = repository.allDebts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalDebtsOwedByMe: StateFlow<Double> = repository.totalDebtsOwedByMe
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalDebtsOwedToMe: StateFlow<Double> = repository.totalDebtsOwedToMe
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val allChallenges: StateFlow<List<SavingsChallengeEntity>> = repository.allChallenges
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Live Gold Rates State ---
    private val _liveGoldRates = MutableStateFlow(
        LiveGoldRates(
            gram24k = 328.0,
            gram22k = 300.6,
            gram21k = 287.0,
            gram18k = 246.0,
            silverGram = 3.90,
            lastUpdated = "جاري التحديث...",
            isLive = false
        )
    )
    val liveGoldRates: StateFlow<LiveGoldRates> = _liveGoldRates.asStateFlow()

    private val _isFetchingGold = MutableStateFlow(false)
    val isFetchingGold: StateFlow<Boolean> = _isFetchingGold.asStateFlow()

    // --- Zakat Calculator State ---
    private val _zakatState = MutableStateFlow(ZakatState())
    val zakatState: StateFlow<ZakatState> = _zakatState.asStateFlow()

    // --- AI Advisor & Smart Budget State ---
    private val _aiAdvice = MutableStateFlow<String>("")
    val aiAdvice: StateFlow<String> = _aiAdvice.asStateFlow()

    private val _isAiLoading = MutableStateFlow<Boolean>(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    private val _isSmartBudgetLoading = MutableStateFlow(false)
    val isSmartBudgetLoading: StateFlow<Boolean> = _isSmartBudgetLoading.asStateFlow()

    // --- Points Marketplace State ---
    private val _isMarketplaceLoading = MutableStateFlow(false)
    val isMarketplaceLoading: StateFlow<Boolean> = _isMarketplaceLoading.asStateFlow()

    private val _marketplaceResult = MutableStateFlow("")
    val marketplaceResult: StateFlow<String> = _marketplaceResult.asStateFlow()

    // --- Online Tournaments State ---
    private val _onlineTournaments = MutableStateFlow(
        listOf(
            OnlineTournament(
                id = "tour_1",
                title = "تحدي فرسان الادخار الأسبوعي (ادخار 300+ ر.س)",
                description = "مسابقة أسبوعية بين 1,420 متسابقاً. المتسابقون الذين يخفضون مصروفاتهم بنسبة 15%+ يربحون نقاطاً مميزة.",
                participantCount = 1420,
                prizePoints = 350,
                daysRemaining = 4,
                isJoined = true
            ),
            OnlineTournament(
                id = "tour_2",
                title = "ماراثون 'صفر قهوة خارجية' لمدة 7 أيام",
                description = "تحدي مجتمعي للامتناع عن شراء القهوة الجاهزة وتحويل المبالغ اليومية لمحفظة التحدي.",
                participantCount = 890,
                prizePoints = 200,
                daysRemaining = 6,
                isJoined = false
            ),
            OnlineTournament(
                id = "tour_3",
                title = "كأس المستثمر الذكي (أفضل عائد استثماري)",
                description = "تنافس على تحقيق أفضل نمو لمحفظة الاستثمار في لعبة بناء الثروة.",
                participantCount = 2100,
                prizePoints = 500,
                daysRemaining = 12,
                isJoined = false
            )
        )
    )
    val onlineTournaments: StateFlow<List<OnlineTournament>> = _onlineTournaments.asStateFlow()

    // --- Investment Game State ---
    private val _investmentGameState = MutableStateFlow(InvestmentGameState())
    val investmentGameState: StateFlow<InvestmentGameState> = _investmentGameState.asStateFlow()

    // --- Zakat Installments Plan State ---
    val zakatInstallmentPlan: StateFlow<ZakatInstallmentPlan> = adminSettings.zakatInstallmentPlan

    init {
        // Fetch live gold rates initially now that all fields are guaranteed initialized
        refreshGoldRates()
    }

    fun createZakatInstallmentPlan(totalZakat: Double, frequency: String, count: Int) {
        adminSettings.saveZakatPlan(totalZakat, frequency, count)
    }

    fun payZakatInstallment(amount: Double) {
        val success = adminSettings.recordZakatPlanPayment(amount)
        if (success) {
            val current = zakatInstallmentPlan.value
            addExpense(
                category = "زكاة وصدقات",
                amount = amount,
                note = "سداد قسط الزكاة الشرعية (${current.paidInstallments} من ${current.totalInstallments})",
                paymentMethod = "تحويل إلكتروني",
                budgetCategoryId = null
            )
        }
    }

    fun resetZakatInstallmentPlan() {
        adminSettings.resetZakatPlan()
    }

    fun refreshGoldRates() {
        viewModelScope.launch {
            _isFetchingGold.value = true
            val rates = GoldPriceService.fetchLiveGoldRates()
            _liveGoldRates.value = rates
            _isFetchingGold.value = false
            // Update Zakat with new rate
            updateZakatInputs(goldPricePerGram24k = rates.gram24k, silverPricePerGram = rates.silverGram)
        }
    }

    fun joinOnlineTournament(tourId: String) {
        _onlineTournaments.value = _onlineTournaments.value.map {
            if (it.id == tourId) it.copy(isJoined = true, participantCount = it.participantCount + 1) else it
        }
        adminSettings.addPoints(20) // Reward for joining community competition
    }

    fun buyAsset(assetType: String, amount: Double) {
        val current = _investmentGameState.value
        if (current.cashBalance < amount) return

        val newCash = current.cashBalance - amount
        val updated = when (assetType) {
            "STOCKS" -> current.copy(cashBalance = newCash, stocksAmount = current.stocksAmount + amount)
            "GOLD" -> current.copy(cashBalance = newCash, goldAmount = current.goldAmount + amount)
            "SUKUK" -> current.copy(cashBalance = newCash, sukukAmount = current.sukukAmount + amount)
            "REAL_ESTATE" -> current.copy(cashBalance = newCash, realEstateAmount = current.realEstateAmount + amount)
            else -> current
        }
        recalculateGamePortfolio(updated)
    }

    fun sellAsset(assetType: String, amount: Double) {
        val current = _investmentGameState.value
        val updated = when (assetType) {
            "STOCKS" -> {
                val toSell = amount.coerceAtMost(current.stocksAmount)
                current.copy(cashBalance = current.cashBalance + toSell, stocksAmount = current.stocksAmount - toSell)
            }
            "GOLD" -> {
                val toSell = amount.coerceAtMost(current.goldAmount)
                current.copy(cashBalance = current.cashBalance + toSell, goldAmount = current.goldAmount - toSell)
            }
            "SUKUK" -> {
                val toSell = amount.coerceAtMost(current.sukukAmount)
                current.copy(cashBalance = current.cashBalance + toSell, sukukAmount = current.sukukAmount - toSell)
            }
            "REAL_ESTATE" -> {
                val toSell = amount.coerceAtMost(current.realEstateAmount)
                current.copy(cashBalance = current.cashBalance + toSell, realEstateAmount = current.realEstateAmount - toSell)
            }
            else -> current
        }
        recalculateGamePortfolio(updated)
    }

    private fun recalculateGamePortfolio(state: InvestmentGameState) {
        // Calculate equity in properties
        val propertyEquity = state.properties.sumOf { prop ->
            if (prop.ownedUnits > 0) {
                val propertyTotalVal = prop.purchasePrice * prop.ownedUnits
                (propertyTotalVal - prop.mortgageBalance).coerceAtLeast(0.0)
            } else 0.0
        }

        // Calculate online crowd deals value
        val crowdDealsVal = state.onlineDeals.sumOf { it.userOwnedShares * it.sharePrice }

        val total = state.cashBalance + state.stocksAmount + state.goldAmount + state.sukukAmount + state.realEstateAmount + propertyEquity + crowdDealsVal
        val initialCapital = 15000.0
        val roi = ((total - initialCapital) / initialCapital) * 100.0

        // Calculate wealth tier based on net worth
        val (tierLevel, tierTitle) = when {
            total >= 2000000.0 -> 5 to "نادي المليونيرات وصناديق التحوط"
            total >= 500000.0 -> 4 to "مستثمر ملائكي ومحفظة خاصة"
            total >= 100000.0 -> 3 to "شريك أعمال ورائد عقاري"
            total >= 30000.0 -> 2 to "مستثمر صاعد"
            else -> 1 to "مبتدئ مالي"
        }

        // Calculate annual rental income
        val totalRent = state.properties.sumOf { it.ownedUnits * it.annualRent }

        _investmentGameState.value = state.copy(
            totalPortfolioValue = total,
            roiPercentage = roi,
            wealthTierLevel = tierLevel,
            wealthTierTitle = tierTitle,
            annualRentalIncome = totalRent
        )
    }

    fun buyRealEstate(propertyId: String, isFinanced: Boolean): Boolean {
        val current = _investmentGameState.value
        val property = current.properties.find { it.id == propertyId } ?: return false

        val requiredDownPayment = if (isFinanced) property.purchasePrice * 0.20 else property.purchasePrice
        if (current.cashBalance < requiredDownPayment) return false

        val newCash = current.cashBalance - requiredDownPayment
        val mortgageAmount = if (isFinanced) property.purchasePrice * 0.80 else 0.0

        val updatedProperties = current.properties.map {
            if (it.id == propertyId) {
                it.copy(
                    ownedUnits = it.ownedUnits + 1,
                    isFinanced = isFinanced || it.isFinanced,
                    mortgageBalance = it.mortgageBalance + mortgageAmount
                )
            } else it
        }

        // Update mission progress for real estate
        val updatedMissions = current.skillMissions.map { m ->
            if (m.id == "mission_real_estate") m.copy(currentProgress = (m.currentProgress + 1).coerceAtMost(m.targetGoal)) else m
        }

        val updatedState = current.copy(
            cashBalance = newCash,
            properties = updatedProperties,
            skillMissions = updatedMissions,
            experienceXp = current.experienceXp + 60
        )
        recalculateGamePortfolio(updatedState)
        return true
    }

    fun sellRealEstate(propertyId: String): Boolean {
        val current = _investmentGameState.value
        val property = current.properties.find { it.id == propertyId } ?: return false
        if (property.ownedUnits <= 0) return false

        // Calculate net sale proceeds after paying 1 unit's portion of mortgage
        val unitMortgage = if (property.ownedUnits > 0) property.mortgageBalance / property.ownedUnits else 0.0
        val saleProceeds = property.purchasePrice - unitMortgage

        val updatedProperties = current.properties.map {
            if (it.id == propertyId) {
                it.copy(
                    ownedUnits = it.ownedUnits - 1,
                    mortgageBalance = (it.mortgageBalance - unitMortgage).coerceAtLeast(0.0)
                )
            } else it
        }

        val updatedState = current.copy(
            cashBalance = current.cashBalance + saleProceeds,
            properties = updatedProperties
        )
        recalculateGamePortfolio(updatedState)
        return true
    }

    fun collectRentalIncome(): Double {
        val current = _investmentGameState.value
        // Collect quarterly rent (annual rent / 4)
        val quarterlyRent = current.properties.sumOf { it.ownedUnits * (it.annualRent / 4.0) }
        if (quarterlyRent <= 0) return 0.0

        val newCash = current.cashBalance + quarterlyRent
        val totalCollected = current.totalRentCollected + quarterlyRent

        // Update cashflow mission
        val updatedMissions = current.skillMissions.map { m ->
            if (m.id == "mission_cashflow") m.copy(currentProgress = totalCollected.coerceAtMost(m.targetGoal)) else m
        }

        val updatedState = current.copy(
            cashBalance = newCash,
            totalRentCollected = totalCollected,
            skillMissions = updatedMissions,
            experienceXp = current.experienceXp + 25
        )
        recalculateGamePortfolio(updatedState)
        return quarterlyRent
    }

    fun investInOnlineDeal(dealId: String, shares: Int): Boolean {
        val current = _investmentGameState.value
        val deal = current.onlineDeals.find { it.id == dealId } ?: return false
        val cost = deal.sharePrice * shares
        if (current.cashBalance < cost) return false

        val updatedDeals = current.onlineDeals.map {
            if (it.id == dealId) {
                it.copy(
                    userOwnedShares = it.userOwnedShares + shares,
                    availableShares = (it.availableShares - shares).coerceAtLeast(0),
                    investorCount = it.investorCount + 1,
                    isJoined = true
                )
            } else it
        }

        val updatedMissions = current.skillMissions.map { m ->
            if (m.id == "mission_crowd_invest") m.copy(currentProgress = 1.0) else m
        }

        val updatedState = current.copy(
            cashBalance = current.cashBalance - cost,
            onlineDeals = updatedDeals,
            skillMissions = updatedMissions,
            experienceXp = current.experienceXp + 40
        )
        recalculateGamePortfolio(updatedState)
        return true
    }

    fun claimMissionReward(missionId: String): Boolean {
        val current = _investmentGameState.value
        val mission = current.skillMissions.find { it.id == missionId } ?: return false
        if (mission.isCompleted || mission.currentProgress < mission.targetGoal) return false

        adminSettings.addPoints(mission.rewardPoints)

        val updatedMissions = current.skillMissions.map {
            if (it.id == missionId) it.copy(isCompleted = true) else it
        }

        val updatedState = current.copy(
            skillMissions = updatedMissions,
            experienceXp = current.experienceXp + mission.rewardXp
        )
        recalculateGamePortfolio(updatedState)
        return true
    }

    fun launchInternationalExpansion(countryId: String, isCoOp: Boolean): Pair<Boolean, String> {
        val current = _investmentGameState.value
        val country = current.expansionCountries.find { it.id == countryId } ?: return false to "البلد غير موجود"
        if (country.isLaunched) return false to "تم التوسع وتأسيس هذا الكيان الدولي بالفعل!"

        val requiredBalance = country.minBankBalanceRequired
        if (current.cashBalance < requiredBalance) {
            return false to "رصيدك النقدي (${current.cashBalance.toInt()} ر.س) لا يحقق شرط الرصيد البنكي المطلوب (${requiredBalance.toInt()} ر.س) للتأشيرة وتأسيس النشاط في ${country.countryName}!"
        }

        val actualCost = if (isCoOp) country.totalRequiredCost * 0.50 else country.totalRequiredCost
        if (current.cashBalance < actualCost) {
            return false to "السيولة النقدية الحرة غير كافية لتغطية تكاليف التأسيس وتذاكر الطيران والفيزا والمكتب (${actualCost.toInt()} ر.س)!"
        }

        val newCash = current.cashBalance - actualCost
        val updatedCountries = current.expansionCountries.map {
            if (it.id == countryId) {
                it.copy(
                    isLaunched = true,
                    isCoOpFunded = isCoOp,
                    coOpPartnersCount = if (isCoOp) 3 else 0,
                    userInvestedAmount = actualCost
                )
            } else it
        }

        val updatedState = current.copy(
            cashBalance = newCash,
            expansionCountries = updatedCountries,
            experienceXp = current.experienceXp + 250
        )
        recalculateGamePortfolio(updatedState)
        adminSettings.addPoints(100) // Global expansion reward points
        return true to if (isCoOp)
            "تم إكمال سيناريو التوسع والشراكة أونلاين في ${country.countryName} بنجاح! تم استخراج الفيزا، ترخيص الشركة، وتوظيف الفريق بالمشاركة!"
        else
            "مبارك! تم إطلاق وتأسيس فرعك الدولي في ${country.countryName} بالكامل بنجاح! ستبدأ في توليد إيرادات دولية كل ربع سنة!"
    }

    fun advanceInvestmentGameRound(): Int {
        val current = _investmentGameState.value
        val round = current.currentRound + 1

        val events = listOf(
            "طفرة الذكاء الاصطناعي وإكسبو 2030: قفزة بنسبة +24% في أسهم التقنية والعقارات السكنية بالرياض!",
            "موسم الحج والعمرة والضيافة: قفزة إيجارية +18% في عقارات مكة وجدة ونمو الصكوك!",
            "ارتفاع التضخم العالمي وتراجع الفائدة: صعود سبائك الذهب بنسبة +16% كأصل ملاذ آمن!",
            "توزيعات أرباح استثنائية: تدفقات الصكوك الإسلامية ومشاريع المتاجر حققت أرباحاً +12%!",
            "تصحيح طفيف في أسواق الأسهم: هبوط -5% في الأسهم وثبات استثنائي للعقارات المدرّة للدخل!"
        )

        val stockMultiplier = when (round % 5) {
            1 -> 1.24
            2 -> 1.03
            3 -> 1.02
            4 -> 1.07
            else -> 0.95
        }

        val goldMultiplier = when (round % 5) {
            1 -> 1.02
            2 -> 1.04
            3 -> 1.16
            4 -> 1.03
            else -> 1.08
        }

        val sukukMultiplier = 1.038
        val reitMultiplier = 1.045

        // Property capital appreciation
        val updatedProperties = current.properties.map { prop ->
            val growth = (prop.capitalGrowthRate / 4.0) / 100.0 // quarterly capital growth
            prop.copy(purchasePrice = prop.purchasePrice * (1.0 + growth))
        }

        // Automatic quarterly rent credit to cash
        val quarterlyRent = updatedProperties.sumOf { it.ownedUnits * (it.annualRent / 4.0) }
        val internationalRev = current.expansionCountries.filter { it.isLaunched }.sumOf {
            if (it.isCoOpFunded) it.expectedQuarterlyRevenue * 0.50 else it.expectedQuarterlyRevenue
        }
        val totalQuarterlyInflow = quarterlyRent + internationalRev

        val newStocks = current.stocksAmount * stockMultiplier
        val newGold = current.goldAmount * goldMultiplier
        val newSukuk = current.sukukAmount * sukukMultiplier
        val newReit = current.realEstateAmount * reitMultiplier

        val headline = events[(round - 1) % events.size]

        // Update missions for net worth
        val updatedMissions = current.skillMissions.map { m ->
            if (m.id == "mission_scale_wealth") {
                m.copy(currentProgress = current.totalPortfolioValue.coerceAtMost(m.targetGoal))
            } else m
        }

        val newState = current.copy(
            currentRound = round,
            cashBalance = current.cashBalance + totalQuarterlyInflow,
            stocksAmount = newStocks,
            goldAmount = newGold,
            sukukAmount = newSukuk,
            realEstateAmount = newReit,
            properties = updatedProperties,
            skillMissions = updatedMissions,
            latestEventHeadline = headline,
            totalRentCollected = current.totalRentCollected + quarterlyRent,
            experienceXp = current.experienceXp + 35
        )
        recalculateGamePortfolio(newState)

        var pointsReward = 0
        if (newState.roiPercentage > 5.0) {
            pointsReward = 40
            adminSettings.addPoints(pointsReward)
        }
        return pointsReward
    }

    fun resetInvestmentGame() {
        _investmentGameState.value = InvestmentGameState()
    }

    // --- Navigation ---
    fun navigateTo(screen: Screen) {
        if (_currentScreen.value != screen) {
            screenStack.add(screen)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (screenStack.size > 1) {
            screenStack.removeAt(screenStack.lastIndex)
            _currentScreen.value = screenStack.last()
            return true
        }
        return false
    }

    // --- Transaction Actions ---
    fun addExpense(
        category: String,
        amount: Double,
        note: String,
        paymentMethod: String,
        budgetCategoryId: Long?
    ) {
        viewModelScope.launch {
            adminSettings.recordDailyTransaction()
            repository.addTransaction(
                TransactionEntity(
                    type = "EXPENSE",
                    category = category,
                    amount = amount,
                    note = note,
                    paymentMethod = paymentMethod,
                    budgetCategoryId = budgetCategoryId
                )
            )
        }
    }

    fun addIncome(
        category: String,
        amount: Double,
        note: String,
        paymentMethod: String
    ) {
        viewModelScope.launch {
            adminSettings.recordDailyTransaction()
            repository.addTransaction(
                TransactionEntity(
                    type = "INCOME",
                    category = category,
                    amount = amount,
                    note = note,
                    paymentMethod = paymentMethod
                )
            )
        }
    }

    fun setCountryAndCurrency(country: String, currency: String) {
        adminSettings.setCountryAndCurrency(country, currency)
    }

    fun applyReferralCode(code: String): Pair<Boolean, String> {
        return adminSettings.applyReferralCode(code)
    }

    fun performDailyCheckInWithRules(): Pair<Boolean, String> {
        return adminSettings.checkInDailyWithRules()
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    // --- Budget Category Actions ---
    fun addBudgetCategory(name: String, allocatedAmount: Double, percentage: Double, colorHex: String) {
        viewModelScope.launch {
            repository.addBudgetCategory(
                BudgetCategoryEntity(
                    name = name,
                    allocatedAmount = allocatedAmount,
                    allocationPercentage = percentage,
                    colorHex = colorHex
                )
            )
        }
    }

    fun generateAndApplySmartBudget(salary: Double, savingsPct: Double) {
        viewModelScope.launch {
            _isSmartBudgetLoading.value = true
            val proposals = GeminiFinancialAdvisor.generateSmartBudgetProposal(salary, savingsPct)
            for (prop in proposals) {
                repository.addBudgetCategory(prop)
            }
            _isSmartBudgetLoading.value = false
        }
    }

    fun deleteBudgetCategory(category: BudgetCategoryEntity) {
        viewModelScope.launch {
            repository.deleteBudgetCategory(category)
        }
    }

    // --- Debt Actions ---
    fun addDebt(
        personName: String,
        type: String,
        totalAmount: Double,
        notes: String,
        dueDate: Long? = null
    ) {
        viewModelScope.launch {
            repository.addDebt(
                DebtEntity(
                    personName = personName,
                    type = type,
                    totalAmount = totalAmount,
                    paidAmount = 0.0,
                    dueDate = dueDate,
                    notes = notes,
                    isSettled = false
                )
            )
        }
    }

    fun recordDebtPayment(debt: DebtEntity, paymentAmount: Double, createTransaction: Boolean = true) {
        viewModelScope.launch {
            val updatedPaid = (debt.paidAmount + paymentAmount).coerceAtMost(debt.totalAmount)
            val isSettled = updatedPaid >= debt.totalAmount
            repository.updateDebt(debt.copy(paidAmount = updatedPaid, isSettled = isSettled))

            if (createTransaction) {
                if (debt.type == "OWED_BY_ME") {
                    repository.addTransaction(
                        TransactionEntity(
                            type = "EXPENSE",
                            category = "سداد ديون",
                            amount = paymentAmount,
                            note = "سداد دين إلى: ${debt.personName}"
                        )
                    )
                } else {
                    repository.addTransaction(
                        TransactionEntity(
                            type = "INCOME",
                            category = "استرداد دين",
                            amount = paymentAmount,
                            note = "دفعة دين مستردة من: ${debt.personName}"
                        )
                    )
                }
            }
        }
    }

    fun deleteDebt(debt: DebtEntity) {
        viewModelScope.launch {
            repository.deleteDebt(debt)
        }
    }

    // --- Challenge Actions ---
    fun updateChallengeProgress(challenge: SavingsChallengeEntity, addedAmount: Double) {
        viewModelScope.launch {
            val newAmount = (challenge.currentAmount + addedAmount).coerceAtLeast(0.0)
            val completed = newAmount >= challenge.targetAmount
            repository.updateChallenge(challenge.copy(currentAmount = newAmount, isCompleted = completed))
        }
    }

    fun addSavingsChallenge(title: String, type: String, targetAmount: Double, durationDays: Int, colorHex: String) {
        viewModelScope.launch {
            repository.addChallenge(
                SavingsChallengeEntity(
                    title = title,
                    type = type,
                    targetAmount = targetAmount,
                    durationDays = durationDays,
                    colorHex = colorHex
                )
            )
        }
    }

    fun deleteChallenge(challenge: SavingsChallengeEntity) {
        viewModelScope.launch {
            repository.deleteChallenge(challenge)
        }
    }

    // --- Zakat Calculator Calculation ---
    fun updateZakatInputs(
        goldGrams24k: Double = _zakatState.value.goldGrams24k,
        goldPricePerGram24k: Double = _zakatState.value.goldPricePerGram24k,
        goldGrams21k: Double = _zakatState.value.goldGrams21k,
        silverGrams: Double = _zakatState.value.silverGrams,
        silverPricePerGram: Double = _zakatState.value.silverPricePerGram,
        cashOnHandAndBank: Double = _zakatState.value.cashOnHandAndBank,
        businessMerchandise: Double = _zakatState.value.businessMerchandise,
        debtsDueImmediately: Double = _zakatState.value.debtsDueImmediately,
        isHijriCalendar: Boolean = _zakatState.value.isHijriCalendar
    ) {
        val equivalent24kGoldFrom21k = goldGrams21k * (21.0 / 24.0)
        val total24kGoldGrams = goldGrams24k + equivalent24kGoldFrom21k
        val goldValue = total24kGoldGrams * goldPricePerGram24k

        val silverValue = silverGrams * silverPricePerGram

        // Nisab threshold = 85 grams of 24k gold
        val nisabThreshold = 85.0 * goldPricePerGram24k

        val totalGrossWealth = goldValue + silverValue + cashOnHandAndBank + businessMerchandise
        val netZakatable = (totalGrossWealth - debtsDueImmediately).coerceAtLeast(0.0)

        val isNisabReached = netZakatable >= nisabThreshold

        // Sharia rate: Hijri (2.500%), Gregorian (2.577%)
        val rate = if (isHijriCalendar) 0.025 else 0.02577
        val zakatDue = if (isNisabReached) netZakatable * rate else 0.0

        _zakatState.value = ZakatState(
            goldGrams24k = goldGrams24k,
            goldPricePerGram24k = goldPricePerGram24k,
            goldGrams21k = goldGrams21k,
            silverGrams = silverGrams,
            silverPricePerGram = silverPricePerGram,
            cashOnHandAndBank = cashOnHandAndBank,
            businessMerchandise = businessMerchandise,
            debtsDueImmediately = debtsDueImmediately,
            isHijriCalendar = isHijriCalendar,
            zakatRate = rate,
            zakatableTotal = netZakatable,
            isNisabReached = isNisabReached,
            nisabThreshold = nisabThreshold,
            zakatAmountDue = zakatDue
        )
    }

    // --- AI Advisor Actions ---
    fun fetchAiAdvice(customQuestion: String? = null) {
        viewModelScope.launch {
            _isAiLoading.value = true
            val advice = GeminiFinancialAdvisor.getFinancialAdvice(
                totalIncome = totalIncome.value,
                totalExpenses = totalExpenses.value,
                debtsOwed = totalDebtsOwedByMe.value,
                salaryEstimate = if (totalIncome.value > 0) totalIncome.value else 3000.0,
                customQuestion = customQuestion
            )
            _aiAdvice.value = advice
            _isAiLoading.value = false
        }
    }

    // --- Points Marketplace Service Execution ---
    fun executeMarketplaceService(type: String, query: String) {
        viewModelScope.launch {
            _isMarketplaceLoading.value = true
            val cost = adminSettings.marketplaceServiceCost.value
            adminSettings.addPoints(-cost)

            val result = when (type) {
                "JOBS" -> GeminiFinancialAdvisor.searchCareerOpportunities(query, totalIncome.value)
                "PROJECTS" -> GeminiFinancialAdvisor.generateOnlineProjectIdea(query)
                else -> GeminiFinancialAdvisor.generateOnlineProjectIdea("خطة تعلم ذاتي لـ: $query")
            }
            _marketplaceResult.value = result
            _isMarketplaceLoading.value = false
        }
    }

    // --- Rewards / Points Actions ---
    fun rewardUserForAd() {
        adminSettings.addPoints(50)
    }

    fun performDailyCheckIn(): Boolean {
        return adminSettings.checkInDaily()
    }
}
