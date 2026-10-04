package com.example.data.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

data class LiveGoldRates(
    val gram24k: Double,
    val gram22k: Double,
    val gram21k: Double,
    val gram18k: Double,
    val silverGram: Double,
    val currency: String = "ر.س",
    val lastUpdated: String,
    val isLive: Boolean = true
)

object GoldPriceService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    // Public Gold/Currency Exchange API endpoint
    private const val API_URL = "https://api.gold-api.com/price/XAU"
    private const val SILVER_API_URL = "https://api.gold-api.com/price/XAG"

    suspend fun fetchLiveGoldRates(): LiveGoldRates = withContext(Dispatchers.IO) {
        val now = SimpleDateFormat("yyyy/MM/dd - hh:mm a", Locale.getDefault()).format(Date())
        val usdToSar = 3.75

        try {
            // Gold per ounce (1 troy ounce = 31.1035 grams)
            val request = Request.Builder().url(API_URL).build()
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val body = response.body?.string() ?: ""
                val json = JSONObject(body)
                val priceUsdPerOunce = json.optDouble("price", 2650.0)

                // Calculate price per gram in SAR
                val gram24kUsd = priceUsdPerOunce / 31.1035
                val gram24kSar = gram24kUsd * usdToSar

                // Also fetch silver if possible
                var silverSar = 3.85
                try {
                    val silverReq = Request.Builder().url(SILVER_API_URL).build()
                    val silverRes = client.newCall(silverReq).execute()
                    if (silverRes.isSuccessful) {
                        val silverJson = JSONObject(silverRes.body?.string() ?: "")
                        val silverOunce = silverJson.optDouble("price", 32.0)
                        silverSar = (silverOunce / 31.1035) * usdToSar
                    }
                } catch (ignored: Exception) {}

                return@withContext LiveGoldRates(
                    gram24k = String.format(Locale.US, "%.2f", gram24kSar).toDouble(),
                    gram22k = String.format(Locale.US, "%.2f", gram24kSar * (22.0 / 24.0)).toDouble(),
                    gram21k = String.format(Locale.US, "%.2f", gram24kSar * (21.0 / 24.0)).toDouble(),
                    gram18k = String.format(Locale.US, "%.2f", gram24kSar * (18.0 / 24.0)).toDouble(),
                    silverGram = String.format(Locale.US, "%.2f", silverSar).toDouble(),
                    lastUpdated = now,
                    isLive = true
                )
            }
        } catch (e: Exception) {
            // Fallback to real-market reference prices
        }

        // Real Market Reference Baseline
        val default24k = 328.50
        LiveGoldRates(
            gram24k = default24k,
            gram22k = String.format(Locale.US, "%.2f", default24k * (22.0 / 24.0)).toDouble(),
            gram21k = String.format(Locale.US, "%.2f", default24k * (21.0 / 24.0)).toDouble(),
            gram18k = String.format(Locale.US, "%.2f", default24k * (18.0 / 24.0)).toDouble(),
            silverGram = 3.90,
            lastUpdated = "$now (سعر استرشادي)",
            isLive = false
        )
    }
}
