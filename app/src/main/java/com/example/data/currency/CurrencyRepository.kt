package com.example.data.currency

import android.content.Context
import android.content.SharedPreferences
import com.example.data.db.CurrencyConversionEntity
import com.example.data.db.ExchangeRateCacheEntity
import com.example.data.db.MixToolsDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class CurrencyRepository(
    private val database: MixToolsDatabase,
    private val context: Context? = null
) {
    private val prefs: SharedPreferences? = context?.getSharedPreferences("mixtools_currency_prefs", Context.MODE_PRIVATE)

    private val defaultFavorites = setOf("BDT", "USD", "EUR", "SAR", "GBP", "AED", "INR")
    private val _favoriteCurrencies = MutableStateFlow(loadFavorites())
    val favoriteCurrencies: StateFlow<Set<String>> = _favoriteCurrencies.asStateFlow()

    private fun loadFavorites(): Set<String> {
        val saved = prefs?.getStringSet("fav_currencies", null)
        return saved ?: defaultFavorites
    }

    fun toggleFavorite(currencyCode: String) {
        val current = _favoriteCurrencies.value.toMutableSet()
        if (current.contains(currencyCode)) {
            if (current.size > 1) {
                current.remove(currencyCode)
            }
        } else {
            current.add(currencyCode)
        }
        prefs?.edit()?.putStringSet("fav_currencies", current)?.apply()
        _favoriteCurrencies.value = current
    }

    fun isFavorite(currencyCode: String): Boolean {
        return _favoriteCurrencies.value.contains(currencyCode)
    }

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .build()

    // Baseline fallback rates if network has never been reached and database is empty
    private val baselineRates = mapOf(
        "USD" to 1.0,
        "BDT" to 121.75,
        "EUR" to 0.92,
        "GBP" to 0.79,
        "INR" to 85.80,
        "SAR" to 3.75,
        "AED" to 3.67,
        "CAD" to 1.38,
        "AUD" to 1.54,
        "JPY" to 154.20,
        "CNY" to 7.24,
        "MYR" to 4.42,
        "SGD" to 1.35,
        "KWD" to 0.31,
        "QAR" to 3.64,
        "OMR" to 0.38,
        "BHD" to 0.38,
        "PKR" to 278.50
    )

    suspend fun getExchangeRates(): CurrencyConversionState = withContext(Dispatchers.IO) {
        // Try network first
        try {
            val request = Request.Builder()
                .url("https://open.er-api.com/v6/latest/USD")
                .header("Accept", "application/json")
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val body = response.body?.string()
                if (!body.isNullOrBlank()) {
                    val json = JSONObject(body)
                    if (json.optString("result") == "success" || json.has("rates")) {
                        val ratesJson = json.getJSONObject("rates")
                        val timeUnix = json.optLong("time_last_update_unix", System.currentTimeMillis() / 1000)
                        val timestamp = timeUnix * 1000

                        val ratesMap = mutableMapOf<String, Double>()
                        val entitiesToCache = mutableListOf<ExchangeRateCacheEntity>()

                        SupportedCurrencies.list.forEach { curr ->
                            if (ratesJson.has(curr.code)) {
                                val rate = ratesJson.getDouble(curr.code)
                                ratesMap[curr.code] = rate
                                entitiesToCache.add(
                                    ExchangeRateCacheEntity(
                                        currencyCode = curr.code,
                                        rateAgainstUsd = rate,
                                        lastUpdatedMillis = timestamp
                                    )
                                )
                            }
                        }

                        // Persist to local database
                        database.exchangeRateDao().insertRates(entitiesToCache)

                        return@withContext CurrencyConversionState(
                            rates = ratesMap,
                            isOffline = false,
                            lastUpdatedMillis = timestamp,
                            isLoading = false
                        )
                    }
                }
            }
        } catch (_: Exception) {
            // Network failed or offline - fall through to cached rates
        }

        // Load from local database cache
        val cached = database.exchangeRateDao().getAllCachedRates()
        if (cached.isNotEmpty()) {
            val ratesMap = cached.associate { it.currencyCode to it.rateAgainstUsd }
            val lastUpdate = cached.maxOfOrNull { it.lastUpdatedMillis } ?: System.currentTimeMillis()
            return@withContext CurrencyConversionState(
                rates = ratesMap,
                isOffline = true,
                lastUpdatedMillis = lastUpdate,
                isLoading = false,
                errorMessage = "Offline mode: Using cached exchange rates"
            )
        }

        // If no cache in DB yet, seed with baseline rates
        val baselineEntities = baselineRates.map {
            ExchangeRateCacheEntity(it.key, it.value, System.currentTimeMillis() - 86400000L)
        }
        database.exchangeRateDao().insertRates(baselineEntities)

        return@withContext CurrencyConversionState(
            rates = baselineRates,
            isOffline = true,
            lastUpdatedMillis = System.currentTimeMillis() - 86400000L,
            isLoading = false,
            errorMessage = "Offline: Showing default baseline exchange rates"
        )
    }

    suspend fun saveConversion(
        fromCurrency: String,
        toCurrency: String,
        fromAmount: Double,
        toAmount: Double
    ) = withContext(Dispatchers.IO) {
        database.currencyConversionDao().insertConversion(
            CurrencyConversionEntity(
                fromCurrency = fromCurrency,
                toCurrency = toCurrency,
                fromAmount = fromAmount,
                toAmount = toAmount,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    fun getRecentConversions(): Flow<List<CurrencyConversionEntity>> {
        return database.currencyConversionDao().getRecentConversions()
    }

    suspend fun clearRecentConversions() = withContext(Dispatchers.IO) {
        database.currencyConversionDao().clearConversions()
    }
}
