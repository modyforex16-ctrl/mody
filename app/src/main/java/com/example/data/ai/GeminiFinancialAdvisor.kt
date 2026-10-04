package com.example.data.ai

import com.example.BuildConfig
import com.example.data.local.BudgetCategoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiFinancialAdvisor {

    private const val MODEL_NAME = "gemini-2.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(45, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .writeTimeout(45, TimeUnit.SECONDS)
        .build()

    private const val SYSTEM_PROMPT = """
أنت المستشار المالي والمهني الذكي لتطبيق 'مليونير'.
مهمتك مساعدة المستخدم في:
1. إعداد خطط ادخار 10% إلى 20% على الأقل من الراتب شهرياً واستثمارها بحكمة.
2. اقتراح ميزانيات ذكية مقسمة رقمياً بدقة.
3. البحث الذكي عن وظائف مناسبة، وتوليد مقترحات مشاريع أونلاين برؤوس أموال صغيرة، ومسارات تعلم ذاتي لرفع الدخل.
4. الإجابة باللغة العربية الفصحى المنظمة والمشجعة.
"""

    private suspend fun callGeminiApi(prompt: String): String? = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Exception) { "" }
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") return@withContext null

        try {
            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply { put("text", prompt) })
                        })
                    })
                })
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", SYSTEM_PROMPT) })
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.7)
                })
            }

            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val resString = response.body?.string() ?: ""
                val resJson = JSONObject(resString)
                val candidates = resJson.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val content = candidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        return@withContext parts.getJSONObject(0).optString("text")
                    }
                }
            }
        } catch (ignored: Exception) {}
        null
    }

    suspend fun getFinancialAdvice(
        totalIncome: Double,
        totalExpenses: Double,
        debtsOwed: Double,
        salaryEstimate: Double,
        customQuestion: String? = null
    ): String = withContext(Dispatchers.IO) {
        val prompt = if (!customQuestion.isNullOrBlank()) {
            "الدخل: $totalIncome, المصروفات: $totalExpenses, الديون: $debtsOwed. سؤال المستخدم: $customQuestion. قدم خطة عملية لادخار 10% على الأقل وتنمية هذا المبلغ."
        } else {
            "الدخل: $totalIncome, المصروفات: $totalExpenses, الديون: $debtsOwed. قدم خطة ادخار 10% من الراتب شهرياً، ومقترحات استثمار آمنة ومرابحة، وخطة لتقليص النفقات."
        }

        callGeminiApi(prompt) ?: generateExpertLocalPlan(totalIncome, totalExpenses, debtsOwed, salaryEstimate)
    }

    suspend fun generateSmartBudgetProposal(
        salary: Double,
        savingsPercent: Double
    ): List<BudgetCategoryEntity> = withContext(Dispatchers.IO) {
        val baseSalary = if (salary > 0) salary else 3000.0
        val targetSavingsPct = savingsPercent.coerceIn(10.0, 50.0)

        // Split remainder into Essentials (50%), Bills (15%), Lifestyle/Wants (remaining)
        val essentialsPct = 50.0
        val billsPct = 15.0
        val wantsPct = (100.0 - targetSavingsPct - essentialsPct - billsPct).coerceAtLeast(5.0)

        listOf(
            BudgetCategoryEntity(
                name = "الاحتياجات الأساسية (طعام وسكن)",
                allocatedAmount = baseSalary * (essentialsPct / 100.0),
                allocationPercentage = essentialsPct,
                colorHex = "#3B82F6",
                iconName = "home"
            ),
            BudgetCategoryEntity(
                name = "الادخار والاستثمار الذكي ($targetSavingsPct% الإلزامي)",
                allocatedAmount = baseSalary * (targetSavingsPct / 100.0),
                allocationPercentage = targetSavingsPct,
                colorHex = "#10B981",
                iconName = "savings"
            ),
            BudgetCategoryEntity(
                name = "الفواتير والمواصلات",
                allocatedAmount = baseSalary * (billsPct / 100.0),
                allocationPercentage = billsPct,
                colorHex = "#F59E0B",
                iconName = "bolt"
            ),
            BudgetCategoryEntity(
                name = "الرغبات والتسوق والترفيه",
                allocatedAmount = baseSalary * (wantsPct / 100.0),
                allocationPercentage = wantsPct,
                colorHex = "#EC4899",
                iconName = "shopping"
            )
        )
    }

    suspend fun searchCareerOpportunities(
        queryOrSkills: String,
        currentIncome: Double
    ): String = withContext(Dispatchers.IO) {
        val prompt = """
المستخدم يطلب بحثاً ذكياً عن وظائف وفرص عمل مناسبة لمهاراته أو رغبته.
المدخلات: $queryOrSkills
الدخل الحالي: $currentIncome

المطلوب إعداد تقرير وظيفي ذكي يحتوي على:
1. أفضل 3 إلى 5 مجالات وظائف (عن بعد / أونلاين أو محلية) تناسب هذا الوصف.
2. المنصات والمواقع الموصى بها للتقديم فوراً (Upwork, Freelancer, منصات التوظيف عن بعد العربية والعالمية).
3. متوسط الدخل المتوقع بالدولار أو العملة المحلية.
4. خطة سريعة من 3 خطوات للقبول وبناء ملف شخصي (Portfolio) قوي.
"""
        callGeminiApi(prompt) ?: """
🚀 **نتائج البحث الذكي عن الوظائف والفرص المناسبة:**

🎯 **أفضل المجالات المقترحة لطلبك:**
1. **العمل الحر عن بعد (Freelancing):** في مجالات الكتابة وصناعة المحتوى، الترجمة، المساعد الافتراضي، أو التصميم والبرمجة.
2. **إدارة المتاجر الإلكترونية وحملات الإعلانات:** مطلوب بكثرة من الشركات الناشئة مع إمكانية العمل بنظام الساعات.
3. **الدعم الفني وخدمة العملاء عن بعد (Customer Support):** وظائف برواتب شهرية ثابتة تتيح العمل من المنزل.

🌐 **أبرز المنصات الموصى بها:**
• **عربياً:** مستقل (Mostaql)، خمسات (Khamsat)، بعيد (Baeed).
• **عالمياً:** Upwork، Fiverr، RemoteOK، We Work Remotely.

💰 **الدخل المتوقع:** يتراوح بين 500 إلى 2,500 دولار شهرياً بناءً على ساعات العمل ومستوى التخصص.

📝 **خطتك العملية للانطلاق اليوم:**
• جهز معرض أعمال مصغر (3 نماذج فقط تثبت كفاءتك).
• قدّم على 5 عروض يومياً بعرض مقنع ومخصص.
• طوّر مهارة التحدث بالإنجليزية عبر التعلم الذاتي لمضاعفة أسعارك.
"""
    }

    suspend fun generateOnlineProjectIdea(
        budgetOrField: String
    ): String = withContext(Dispatchers.IO) {
        val prompt = """
اقترح مشروعاً أونلاين ذكياً ومبتكراً بناءً على هذا الطلب: $budgetOrField.
المطلوب:
1. فكرة المشروع ونموذج العمل (Business Model).
2. رأس المال المطلوب وكيفية البدء بأقل تكلفة (Lean Startup).
3. خطة التسويق الرقمي واستقطاب أول 10 عملاء.
4. مسار التعلم الذاتي الموصى به لإتقان أدوات هذا المشروع.
"""
        callGeminiApi(prompt) ?: """
💡 **مقترح مشروع أونلاين ذكي (نموذج عمل عالي الربحية):**

🏢 **فكرة المشروع: وكالة خدمات رقمية مصغرة (Micro Agency) أو متجر دروب شيبينغ مخصص**
• **نموذج العمل:** تقديم خدمات مطلوبة لأصحاب المشاريع (مثل تصميم المنشورات، كتابة الإعلانات، أو إنشاء صفحات الهبوط) أو بيع منتجات حلول لمشاكل شائعة دون الحاجة لمخزون كبير.
• **رأس المال المقترح:** من 200 إلى 800 ر.س فقط (لتغطية النطاق وأداة التصميم أو إعلانات اختبار بسيطة).

📈 **خطة استقطاب أول 10 عملاء:**
1. البحث عن حسابات تجارية على إنستغرام أو تيك توك تعاني من ضعف التصميم أو المحتوى.
2. إرسال نموذج تجريبي مجاني بدون مقابل كإثبات لقيمتك.
3. تحويل العميل التجريبي إلى باقة شهرية ثابتة.

📚 **مسار التعلم الذاتي المقترح:**
• أتقن أدوات الذكاء الاصطناعي التوليدي مثل Canva، ChatGPT، و Midjourney.
• تعلم أساسيات الإعلانات الممولة (Meta & TikTok Ads).
"""
    }

    private fun generateExpertLocalPlan(
        income: Double,
        expenses: Double,
        debts: Double,
        salary: Double
    ): String {
        val baseIncome = if (income > 0) income else if (salary > 0) salary else 2500.0
        val targetSavings10 = baseIncome * 0.10
        val balance = baseIncome - expenses

        val debtStatus = if (debts > 0) {
            "🔴 لديك التزامات ديون بقيمة ${String.format("%.1f", debts)}. خصص 5% إضافية لتسريع سداد أصغر دين أولاً."
        } else {
            "🟢 وضع الديون ممتاز. استغل الفائض مباشرة في تنمية المحفظة الاستثمارية."
        }

        return """
🌟 **خطة المستشار المالي الذكي (تطبيق مليونير)**

📊 **الوضع المالي:**
• إجمالي الدخل: ${String.format("%.1f", baseIncome)} ر.س
• إجمالي المصروفات: ${String.format("%.1f", expenses)} ر.س
• الفائض المالي: ${String.format("%.1f", balance)} ر.س

💡 **خطة ادخار 10% الإلزامية:**
• المبلغ المستهدف ادخاره شهرياً (10%): **${String.format("%.1f", targetSavings10)} ر.س**
• استقطع هذا المبلغ فور نزول الراتب في حساب فرعي استثماري مستقل.

🚀 **أين تستثمر هذا المبلغ؟**
1. **صندوق طوارئ عالي السيولة:** 30% من المدخرات.
2. **سبائك الذهب عيار 24:** 25% لحفظ القيمة.
3. **صناديق الاستثمار المؤشرية والمرابحة:** 45% للتراكم المركب.

⚖️ **الديون:**
$debtStatus
"""
    }
}
