package com.example.data.local

data class InternationalExpansionCountry(
    val id: String,
    val countryName: String,
    val countryFlag: String,
    val cityHub: String,
    val currency: String,
    val minBankBalanceRequired: Double,
    val visaAndTravelCost: Double,
    val licenseAndRegistrationCost: Double,
    val officeRentQuarterly: Double,
    val staffSalariesQuarterly: Double,
    val inventorySupplyCost: Double,
    val expectedQuarterlyRevenue: Double,
    val economicCondition: String,
    val totalRequiredCost: Double,
    val isLaunched: Boolean = false,
    val isCoOpFunded: Boolean = false,
    val coOpPartnersCount: Int = 0,
    val userInvestedAmount: Double = 0.0
)

fun getInitialExpansionCountries(): List<InternationalExpansionCountry> = listOf(
    InternationalExpansionCountry(
        id = "exp_uae_dubai",
        countryName = "الإمارات العربية المتحدة",
        countryFlag = "🇦🇪",
        cityHub = "دبي (مركز دبي المالي العالمي والمناطق الحرة)",
        currency = "د.إ",
        minBankBalanceRequired = 75000.0,
        visaAndTravelCost = 7500.0,
        licenseAndRegistrationCost = 21000.0,
        officeRentQuarterly = 14000.0,
        staffSalariesQuarterly = 22000.0,
        inventorySupplyCost = 25000.0,
        expectedQuarterlyRevenue = 115000.0,
        economicCondition = "بيئة أعمال معفاة من الضرائب بنسبة 100% في المناطق الحرة، وبوابة تجارية عالمية.",
        totalRequiredCost = 89500.0
    ),
    InternationalExpansionCountry(
        id = "exp_egypt_cairo",
        countryName = "جمهورية مصر العربية",
        countryFlag = "🇪🇬",
        cityHub = "القاهرة (التجمع الخامس والعاصمة الإدارية)",
        currency = "ج.م",
        minBankBalanceRequired = 35000.0,
        visaAndTravelCost = 3000.0,
        licenseAndRegistrationCost = 8500.0,
        officeRentQuarterly = 6500.0,
        staffSalariesQuarterly = 9000.0,
        inventorySupplyCost = 15000.0,
        expectedQuarterlyRevenue = 48000.0,
        economicCondition = "سوق استهلاكي عملاق يفوق 105 مليون نسمة، وتكاليف تشغيل منخفضة مع هوامش ربح عالية.",
        totalRequiredCost = 42000.0
    ),
    InternationalExpansionCountry(
        id = "exp_usa_delaware",
        countryName = "الولايات المتحدة الأمريكية",
        countryFlag = "🇺🇸",
        cityHub = "ديلاوير / نيويورك (شركة LLC وتجارة رقمية)",
        currency = "$",
        minBankBalanceRequired = 140000.0,
        visaAndTravelCost = 12000.0,
        licenseAndRegistrationCost = 18000.0,
        officeRentQuarterly = 15000.0,
        staffSalariesQuarterly = 32000.0,
        inventorySupplyCost = 40000.0,
        expectedQuarterlyRevenue = 175000.0,
        economicCondition = "أكبر اقتصاد وأكبر قوة شرائية في العالم، وأرباح مباشرة بالدولار الأمريكي.",
        totalRequiredCost = 117000.0
    ),
    InternationalExpansionCountry(
        id = "exp_uk_london",
        countryName = "المملكة المتحدة",
        countryFlag = "🇬🇧",
        cityHub = "لندن (الخدمات الرقمية والتجارة الأوروبية)",
        currency = "£",
        minBankBalanceRequired = 110000.0,
        visaAndTravelCost = 10000.0,
        licenseAndRegistrationCost = 16000.0,
        officeRentQuarterly = 18000.0,
        staffSalariesQuarterly = 28000.0,
        inventorySupplyCost = 30000.0,
        expectedQuarterlyRevenue = 135000.0,
        economicCondition = "مركز مالي عالمي مرموق وتشريعات تجارية مرنة لخدمة السوق الأوروبي والبريطاني.",
        totalRequiredCost = 102000.0
    )
)
