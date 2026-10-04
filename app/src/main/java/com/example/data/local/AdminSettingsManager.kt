package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AdminSettingsManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("millionaire_admin_prefs", Context.MODE_PRIVATE)

    companion object {
        const val ADMIN_USERNAME = "mody81"
        const val ADMIN_PASSWORD = "527149"

        private const val KEY_ADMOB_APP_ID = "admob_app_id"
        private const val KEY_BANNER_TOP_ID = "banner_top_id"
        private const val KEY_BANNER_BOTTOM_ID = "banner_bottom_id"
        private const val KEY_REWARDED_AD_ID = "rewarded_ad_id"
        private const val KEY_INTERSTITIAL_AD_ID = "interstitial_ad_id"
        private const val KEY_ADS_ENABLED = "ads_enabled"
        private const val KEY_TEST_MODE = "ads_test_mode"
        private const val KEY_USER_POINTS = "user_points"
        private const val KEY_DAILY_STREAK = "daily_streak"
        private const val KEY_LAST_CHECKIN = "last_checkin_timestamp"

        private const val KEY_ZAKAT_PLAN_TOTAL = "zakat_plan_total"
        private const val KEY_ZAKAT_PLAN_PAID = "zakat_plan_paid"
        private const val KEY_ZAKAT_PLAN_COUNT = "zakat_plan_count"
        private const val KEY_ZAKAT_PLAN_PAID_COUNT = "zakat_plan_paid_count"
        private const val KEY_ZAKAT_PLAN_FREQUENCY = "zakat_plan_frequency"
        private const val KEY_ZAKAT_PLAN_ACTIVE = "zakat_plan_active"

        private const val KEY_MARKETPLACE_COST = "marketplace_service_cost"
        private const val KEY_REFERRAL_REWARD = "referral_reward_points"
        private const val KEY_DAILY_STREAK_REWARD = "daily_streak_reward"
        private const val KEY_MIN_DAILY_TRANSACTIONS = "min_daily_transactions"
        private const val KEY_SELECTED_COUNTRY = "selected_country"
        private const val KEY_SELECTED_CURRENCY = "selected_currency"
        private const val KEY_REFERRAL_CODE = "user_referral_code"
        private const val KEY_REDEEMED_REFERRAL = "redeemed_referral"
        private const val KEY_REFERRALS_COUNT = "user_referrals_count"
        private const val KEY_TODAY_TX_COUNT = "today_transactions_count"
        private const val KEY_TODAY_TX_DATE = "today_transactions_date"

        // Standard official Google AdMob test IDs for reference and fallback
        const val DEFAULT_TEST_APP_ID = "ca-app-pub-3940256099942544~3347511713"
        const val DEFAULT_TEST_BANNER_TOP_ID = "ca-app-pub-3940256099942544/6300978111"
        const val DEFAULT_TEST_BANNER_BOTTOM_ID = "ca-app-pub-3940256099942544/6300978111"
        const val DEFAULT_TEST_REWARDED_ID = "ca-app-pub-3940256099942544/5224354917"
        const val DEFAULT_TEST_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712"
    }

    private val _admobAppId = MutableStateFlow(prefs.getString(KEY_ADMOB_APP_ID, DEFAULT_TEST_APP_ID) ?: DEFAULT_TEST_APP_ID)
    val admobAppId: StateFlow<String> = _admobAppId.asStateFlow()

    private val _bannerTopId = MutableStateFlow(prefs.getString(KEY_BANNER_TOP_ID, DEFAULT_TEST_BANNER_TOP_ID) ?: DEFAULT_TEST_BANNER_TOP_ID)
    val bannerTopId: StateFlow<String> = _bannerTopId.asStateFlow()

    private val _bannerBottomId = MutableStateFlow(prefs.getString(KEY_BANNER_BOTTOM_ID, DEFAULT_TEST_BANNER_BOTTOM_ID) ?: DEFAULT_TEST_BANNER_BOTTOM_ID)
    val bannerBottomId: StateFlow<String> = _bannerBottomId.asStateFlow()

    private val _rewardedAdId = MutableStateFlow(prefs.getString(KEY_REWARDED_AD_ID, DEFAULT_TEST_REWARDED_ID) ?: DEFAULT_TEST_REWARDED_ID)
    val rewardedAdId: StateFlow<String> = _rewardedAdId.asStateFlow()

    private val _interstitialAdId = MutableStateFlow(prefs.getString(KEY_INTERSTITIAL_AD_ID, DEFAULT_TEST_INTERSTITIAL_ID) ?: DEFAULT_TEST_INTERSTITIAL_ID)
    val interstitialAdId: StateFlow<String> = _interstitialAdId.asStateFlow()

    private val _adsEnabled = MutableStateFlow(prefs.getBoolean(KEY_ADS_ENABLED, true))
    val adsEnabled: StateFlow<Boolean> = _adsEnabled.asStateFlow()

    private val _testMode = MutableStateFlow(prefs.getBoolean(KEY_TEST_MODE, true))
    val testMode: StateFlow<Boolean> = _testMode.asStateFlow()

    private val _userPoints = MutableStateFlow(prefs.getInt(KEY_USER_POINTS, 120))
    val userPoints: StateFlow<Int> = _userPoints.asStateFlow()

    private val _dailyStreak = MutableStateFlow(prefs.getInt(KEY_DAILY_STREAK, 3))
    val dailyStreak: StateFlow<Int> = _dailyStreak.asStateFlow()

    private val _marketplaceServiceCost = MutableStateFlow(prefs.getInt(KEY_MARKETPLACE_COST, 25))
    val marketplaceServiceCost: StateFlow<Int> = _marketplaceServiceCost.asStateFlow()

    private val _referralRewardPoints = MutableStateFlow(prefs.getInt(KEY_REFERRAL_REWARD, 100))
    val referralRewardPoints: StateFlow<Int> = _referralRewardPoints.asStateFlow()

    private val _dailyStreakReward = MutableStateFlow(prefs.getInt(KEY_DAILY_STREAK_REWARD, 30))
    val dailyStreakReward: StateFlow<Int> = _dailyStreakReward.asStateFlow()

    private val _minDailyTransactions = MutableStateFlow(prefs.getInt(KEY_MIN_DAILY_TRANSACTIONS, 3))
    val minDailyTransactions: StateFlow<Int> = _minDailyTransactions.asStateFlow()

    private val _selectedCountry = MutableStateFlow(prefs.getString(KEY_SELECTED_COUNTRY, "المملكة العربية السعودية") ?: "المملكة العربية السعودية")
    val selectedCountry: StateFlow<String> = _selectedCountry.asStateFlow()

    private val _selectedCurrency = MutableStateFlow(prefs.getString(KEY_SELECTED_CURRENCY, "ر.س") ?: "ر.س")
    val selectedCurrency: StateFlow<String> = _selectedCurrency.asStateFlow()

    private val _userReferralCode = MutableStateFlow(loadOrCreateReferralCode())
    val userReferralCode: StateFlow<String> = _userReferralCode.asStateFlow()

    private val _referralsCount = MutableStateFlow(prefs.getInt(KEY_REFERRALS_COUNT, 0))
    val referralsCount: StateFlow<Int> = _referralsCount.asStateFlow()

    private val _todayTransactionsCount = MutableStateFlow(loadTodayTransactionsCount())
    val todayTransactionsCount: StateFlow<Int> = _todayTransactionsCount.asStateFlow()

    private fun loadOrCreateReferralCode(): String {
        val existing = prefs.getString(KEY_REFERRAL_CODE, null)
        if (existing != null) return existing
        val newCode = "MIL-${(10000..99999).random()}"
        prefs.edit().putString(KEY_REFERRAL_CODE, newCode).apply()
        return newCode
    }

    private fun loadTodayTransactionsCount(): Int {
        val todayStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
        val savedDate = prefs.getString(KEY_TODAY_TX_DATE, "")
        return if (savedDate == todayStr) {
            prefs.getInt(KEY_TODAY_TX_COUNT, 0)
        } else {
            prefs.edit().putString(KEY_TODAY_TX_DATE, todayStr).putInt(KEY_TODAY_TX_COUNT, 0).apply()
            0
        }
    }

    fun recordDailyTransaction() {
        val todayStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
        val savedDate = prefs.getString(KEY_TODAY_TX_DATE, "")
        val current = if (savedDate == todayStr) prefs.getInt(KEY_TODAY_TX_COUNT, 0) else 0
        val updated = current + 1
        prefs.edit().putString(KEY_TODAY_TX_DATE, todayStr).putInt(KEY_TODAY_TX_COUNT, updated).apply()
        _todayTransactionsCount.value = updated
    }

    fun updateAdminMarketplaceAndStreakSettings(
        marketCost: Int,
        referralReward: Int,
        streakReward: Int,
        minTx: Int
    ) {
        prefs.edit()
            .putInt(KEY_MARKETPLACE_COST, marketCost)
            .putInt(KEY_REFERRAL_REWARD, referralReward)
            .putInt(KEY_DAILY_STREAK_REWARD, streakReward)
            .putInt(KEY_MIN_DAILY_TRANSACTIONS, minTx)
            .apply()
        _marketplaceServiceCost.value = marketCost
        _referralRewardPoints.value = referralReward
        _dailyStreakReward.value = streakReward
        _minDailyTransactions.value = minTx
    }

    fun setCountryAndCurrency(country: String, currency: String) {
        prefs.edit()
            .putString(KEY_SELECTED_COUNTRY, country)
            .putString(KEY_SELECTED_CURRENCY, currency)
            .apply()
        _selectedCountry.value = country
        _selectedCurrency.value = currency
    }

    fun applyReferralCode(code: String): Pair<Boolean, String> {
        val trimmed = code.trim().uppercase()
        if (trimmed == _userReferralCode.value) {
            return false to "لا يمكنك استخدام كود الإحالة الخاص بك!"
        }
        val isAlreadyRedeemed = prefs.getBoolean(KEY_REDEEMED_REFERRAL, false)
        if (isAlreadyRedeemed) {
            return false to "لقد استخدمت كود إحالة سابقاً بالفعل!"
        }
        if (trimmed.length < 5 || !trimmed.startsWith("MIL-")) {
            return false to "كود الإحالة غير صالح! تأكد من الصيغة (مثلاً: MIL-12345)"
        }

        // Grant referral points
        val reward = _referralRewardPoints.value
        addPoints(reward)
        val newRefCount = _referralsCount.value + 1
        prefs.edit()
            .putBoolean(KEY_REDEEMED_REFERRAL, true)
            .putInt(KEY_REFERRALS_COUNT, newRefCount)
            .apply()
        _referralsCount.value = newRefCount
        return true to "تم تفعيل كود الإحالة بنجاح وحصلت على +$reward نقطة!"
    }

    fun checkInDailyWithRules(): Pair<Boolean, String> {
        val now = System.currentTimeMillis()
        val lastCheckin = prefs.getLong(KEY_LAST_CHECKIN, 0L)
        val oneDayMillis = 20 * 60 * 60 * 1000L // 20 hours threshold for smooth daily check-in
        val minTxRequired = _minDailyTransactions.value
        val todayTx = loadTodayTransactionsCount()

        if (todayTx < minTxRequired) {
            return false to "للحفاظ على الـ Strike اليومي، يجب تسجيل $minTxRequired مصاريف أو معاملات على الأقل اليوم! (سجلت $todayTx حتى الآن)"
        }

        if (now - lastCheckin >= oneDayMillis) {
            val newStreak = _dailyStreak.value + 1
            val reward = _dailyStreakReward.value
            prefs.edit()
                .putLong(KEY_LAST_CHECKIN, now)
                .putInt(KEY_DAILY_STREAK, newStreak)
                .apply()
            _dailyStreak.value = newStreak
            addPoints(reward)
            return true to "رائع! أتممت متابعة اليوم وسجلت $todayTx مصاريف. تمت ترقية الـ Strike إلى $newStreak أيام وحصلت على +$reward نقطة!"
        } else {
            return false to "لقد سجلت حضورك اليوم بالفعل! عد غداً للمتابعة وزيادة الـ Strike."
        }
    }

    fun updateAdSettings(
        appId: String,
        bannerTop: String,
        bannerBottom: String,
        rewarded: String,
        interstitial: String,
        enabled: Boolean,
        isTest: Boolean
    ) {
        prefs.edit()
            .putString(KEY_ADMOB_APP_ID, appId)
            .putString(KEY_BANNER_TOP_ID, bannerTop)
            .putString(KEY_BANNER_BOTTOM_ID, bannerBottom)
            .putString(KEY_REWARDED_AD_ID, rewarded)
            .putString(KEY_INTERSTITIAL_AD_ID, interstitial)
            .putBoolean(KEY_ADS_ENABLED, enabled)
            .putBoolean(KEY_TEST_MODE, isTest)
            .apply()

        _admobAppId.value = appId
        _bannerTopId.value = bannerTop
        _bannerBottomId.value = bannerBottom
        _rewardedAdId.value = rewarded
        _interstitialAdId.value = interstitial
        _adsEnabled.value = enabled
        _testMode.value = isTest
    }

    fun addPoints(points: Int) {
        val newPoints = (_userPoints.value + points).coerceAtLeast(0)
        prefs.edit().putInt(KEY_USER_POINTS, newPoints).apply()
        _userPoints.value = newPoints
    }

    fun resetPoints() {
        prefs.edit().putInt(KEY_USER_POINTS, 0).apply()
        _userPoints.value = 0
    }

    fun checkInDaily(): Boolean {
        val now = System.currentTimeMillis()
        val lastCheckin = prefs.getLong(KEY_LAST_CHECKIN, 0L)
        val oneDayMillis = 24 * 60 * 60 * 1000L

        if (now - lastCheckin >= oneDayMillis) {
            val newStreak = _dailyStreak.value + 1
            prefs.edit()
                .putLong(KEY_LAST_CHECKIN, now)
                .putInt(KEY_DAILY_STREAK, newStreak)
                .apply()
            _dailyStreak.value = newStreak
            addPoints(25) // Daily streak reward points
            return true
        }
        return false
    }

    /**
     * Authenticate admin credentials.
     * Supports both Western Arabic (0-9) and Eastern Arabic / Indic (٠-٩) numerals for the password.
     */
    fun authenticate(usernameInput: String, passwordInput: String): Boolean {
        val normalizedUser = usernameInput.trim()
        val normalizedPass = normalizeArabicNumerals(passwordInput.trim())
        return normalizedUser == ADMIN_USERNAME && normalizedPass == ADMIN_PASSWORD
    }

    private fun normalizeArabicNumerals(input: String): String {
        val arabicToLatinMap = mapOf(
            '٠' to '0', '١' to '1', '٢' to '2', '٣' to '3', '٤' to '4',
            '٥' to '5', '٦' to '6', '٧' to '7', '٨' to '8', '٩' to '9'
        )
        return input.map { arabicToLatinMap[it] ?: it }.joinToString("")
    }

    // --- Zakat Installments Plan State & Persistence ---
    private val _zakatInstallmentPlan = MutableStateFlow(loadZakatPlan())
    val zakatInstallmentPlan: StateFlow<ZakatInstallmentPlan> = _zakatInstallmentPlan.asStateFlow()

    private fun loadZakatPlan(): ZakatInstallmentPlan {
        val total = prefs.getFloat(KEY_ZAKAT_PLAN_TOTAL, 0f).toDouble()
        val paid = prefs.getFloat(KEY_ZAKAT_PLAN_PAID, 0f).toDouble()
        val count = prefs.getInt(KEY_ZAKAT_PLAN_COUNT, 12).coerceAtLeast(1)
        val paidCount = prefs.getInt(KEY_ZAKAT_PLAN_PAID_COUNT, 0)
        val frequency = prefs.getString(KEY_ZAKAT_PLAN_FREQUENCY, "MONTHLY") ?: "MONTHLY"
        val isActive = prefs.getBoolean(KEY_ZAKAT_PLAN_ACTIVE, false)

        val installmentAmount = if (count > 0) total / count else 0.0
        val remaining = (total - paid).coerceAtLeast(0.0)
        val progress = if (total > 0) (paid / total).toFloat().coerceIn(0f, 1f) else 0f

        return ZakatInstallmentPlan(
            totalZakat = total,
            frequency = frequency,
            totalInstallments = count,
            paidInstallments = paidCount,
            paidAmount = paid,
            installmentAmount = installmentAmount,
            remainingAmount = remaining,
            progressPercentage = progress,
            isActive = isActive
        )
    }

    fun saveZakatPlan(total: Double, frequency: String, count: Int) {
        val countValid = count.coerceAtLeast(1)
        prefs.edit()
            .putFloat(KEY_ZAKAT_PLAN_TOTAL, total.toFloat())
            .putFloat(KEY_ZAKAT_PLAN_PAID, 0f)
            .putInt(KEY_ZAKAT_PLAN_COUNT, countValid)
            .putInt(KEY_ZAKAT_PLAN_PAID_COUNT, 0)
            .putString(KEY_ZAKAT_PLAN_FREQUENCY, frequency)
            .putBoolean(KEY_ZAKAT_PLAN_ACTIVE, true)
            .apply()
        _zakatInstallmentPlan.value = loadZakatPlan()
    }

    fun recordZakatPlanPayment(amount: Double): Boolean {
        val current = _zakatInstallmentPlan.value
        if (!current.isActive) return false

        val newPaid = (current.paidAmount + amount).coerceAtMost(current.totalZakat)
        val newPaidCount = (current.paidInstallments + 1).coerceAtMost(current.totalInstallments)

        prefs.edit()
            .putFloat(KEY_ZAKAT_PLAN_PAID, newPaid.toFloat())
            .putInt(KEY_ZAKAT_PLAN_PAID_COUNT, newPaidCount)
            .apply()

        _zakatInstallmentPlan.value = loadZakatPlan()
        return true
    }

    fun resetZakatPlan() {
        prefs.edit()
            .putFloat(KEY_ZAKAT_PLAN_TOTAL, 0f)
            .putFloat(KEY_ZAKAT_PLAN_PAID, 0f)
            .putInt(KEY_ZAKAT_PLAN_COUNT, 12)
            .putInt(KEY_ZAKAT_PLAN_PAID_COUNT, 0)
            .putBoolean(KEY_ZAKAT_PLAN_ACTIVE, false)
            .apply()
        _zakatInstallmentPlan.value = loadZakatPlan()
    }
}

data class ZakatInstallmentPlan(
    val totalZakat: Double = 0.0,
    val frequency: String = "MONTHLY", // "MONTHLY", "QUARTERLY", "SEMI_ANNUALLY", "CUSTOM"
    val totalInstallments: Int = 12,
    val paidInstallments: Int = 0,
    val paidAmount: Double = 0.0,
    val installmentAmount: Double = 0.0,
    val remainingAmount: Double = 0.0,
    val progressPercentage: Float = 0f,
    val isActive: Boolean = false
)
