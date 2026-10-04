package com.example.data.local

data class RealEstateProperty(
    val id: String,
    val city: String,
    val district: String,
    val propertyType: String, // "شقة سكنية", "مكتب تجاري", "شقة ضيافة فندقية", "فيلا استثمارية"
    val purchasePrice: Double,
    val annualRent: Double,
    val rentalYieldPercentage: Double,
    val capitalGrowthRate: Double,
    val imageIcon: String,
    val description: String,
    var ownedUnits: Int = 0,
    var isFinanced: Boolean = false,
    var mortgageBalance: Double = 0.0
)

data class InvestmentSkillMission(
    val id: String,
    val title: String,
    val skillCategory: String, // "تحليل التدفق النقدي", "إدارة المخاطر", "اقتناص الفرص", "التنويع الذكي"
    val description: String,
    val targetGoal: Double,
    val currentProgress: Double,
    val rewardPoints: Int,
    val rewardXp: Int,
    val isCompleted: Boolean = false
)

data class OnlineInvestmentDeal(
    val id: String,
    val ownerName: String,
    val title: String,
    val category: String, // "عقار جماعي", "متجر إلكتروني", "مشروع تقني", "صكوك مرابحة"
    val cityOrPlatform: String,
    val totalValuation: Double,
    val sharePrice: Double,
    val availableShares: Int,
    val expectedAnnualRoi: Double,
    val minWealthLevel: Int,
    val investorCount: Int,
    val isJoined: Boolean = false,
    val userOwnedShares: Int = 0
)

data class WealthTier(
    val level: Int,
    val title: String,
    val minNetWorth: Double,
    val badgeIcon: String,
    val perks: String
)
