package com.example.data.currency

data class CurrencyInfo(
    val code: String,
    val nameEn: String,
    val nameBn: String,
    val symbol: String,
    val flagEmoji: String
)

data class CurrencyConversionState(
    val rates: Map<String, Double> = emptyMap(),
    val isOffline: Boolean = false,
    val lastUpdatedMillis: Long = 0L,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

object SupportedCurrencies {
    val list = listOf(
        CurrencyInfo("USD", "US Dollar", "মার্কিন ডলার", "$", "🇺🇸"),
        CurrencyInfo("BDT", "Bangladeshi Taka", "বাংলাদেশি টাকা", "৳", "🇧🇩"),
        CurrencyInfo("EUR", "Euro", "ইউরো", "€", "🇪🇺"),
        CurrencyInfo("GBP", "British Pound", "ব্রিটিশ পাউন্ড", "£", "🇬🇧"),
        CurrencyInfo("INR", "Indian Rupee", "ভারতীয় রুপি", "₹", "🇮🇳"),
        CurrencyInfo("SAR", "Saudi Riyal", "সৌদি রিয়াল", "﷼", "🇸🇦"),
        CurrencyInfo("AED", "UAE Dirham", "সংযুক্ত আরব আমিরাত দিরহাম", "د.إ", "🇦🇪"),
        CurrencyInfo("CAD", "Canadian Dollar", "কানাডিয়ান ডলার", "CA$", "🇨🇦"),
        CurrencyInfo("AUD", "Australian Dollar", "অস্ট্রেলিয়ান ডলার", "AU$", "🇦🇺"),
        CurrencyInfo("JPY", "Japanese Yen", "জাপানি ইয়েন", "¥", "🇯🇵"),
        CurrencyInfo("CNY", "Chinese Yuan", "চীনা ইউয়ান", "¥", "🇨🇳"),
        CurrencyInfo("MYR", "Malaysian Ringgit", "মালয়েশিয়ান রিঙ্গিত", "RM", "🇲🇾"),
        CurrencyInfo("SGD", "Singapore Dollar", "সিঙ্গাপুর ডলার", "S$", "🇸🇬"),
        CurrencyInfo("KWD", "Kuwaiti Dinar", "কুয়েতি দিনার", "KD", "🇰🇼"),
        CurrencyInfo("QAR", "Qatari Riyal", "কাতারি রিয়াল", "QR", "🇶🇦"),
        CurrencyInfo("OMR", "Omani Rial", "ওমানি রিয়াল", "OMR", "🇴🇲"),
        CurrencyInfo("BHD", "Bahraini Dinar", "বাহরাইনি দিনার", "BD", "🇧🇭"),
        CurrencyInfo("PKR", "Pakistani Rupee", "পাকিস্তানি রুপি", "Rs", "🇵🇰")
    )

    fun get(code: String): CurrencyInfo {
        return list.find { it.code.equals(code, ignoreCase = true) }
            ?: CurrencyInfo(code, code, code, code, "🌐")
    }
}
