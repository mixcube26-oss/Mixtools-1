package com.example.ui.tools.calculator

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.text.format.DateFormat
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChangeCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.currency.CurrencyConversionState
import com.example.data.currency.CurrencyInfo
import com.example.data.currency.CurrencyRepository
import com.example.data.currency.SupportedCurrencies
import com.example.model.AppLanguage
import com.example.ui.components.OutlinedStateButton
import kotlinx.coroutines.launch
import java.text.DecimalFormat

@Composable
fun CurrencyConverterScreen(
    currencyRepository: CurrencyRepository,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var state by remember { mutableStateOf(CurrencyConversionState(isLoading = true)) }
    var amountText by remember { mutableStateOf("100") }
    var fromCurrencyCode by remember { mutableStateOf("USD") }
    var toCurrencyCode by remember { mutableStateOf("BDT") }
    var showFavoriteDialog by remember { mutableStateOf(false) }

    val recentConversions by currencyRepository.getRecentConversions()
        .collectAsStateWithLifecycle(initialValue = emptyList())

    val favoriteCurrencies by currencyRepository.favoriteCurrencies
        .collectAsStateWithLifecycle()

    fun fetchRates() {
        scope.launch {
            state = state.copy(isLoading = true)
            val res = currencyRepository.getExchangeRates()
            state = res
        }
    }

    LaunchedEffect(Unit) {
        fetchRates()
    }

    val fromInfo = SupportedCurrencies.get(fromCurrencyCode)
    val toInfo = SupportedCurrencies.get(toCurrencyCode)

    val df = remember { DecimalFormat("#,##0.00##") }

    val convertedAmount: Double? = run {
        val amt = amountText.toDoubleOrNull() ?: return@run null
        if (amt < 0) return@run null
        val rates = state.rates
        val fromRate = rates[fromCurrencyCode] ?: 1.0
        val toRate = rates[toCurrencyCode] ?: 1.0
        if (fromRate <= 0.0) return@run null
        (amt / fromRate) * toRate
    }

    val convertedFormatted = if (convertedAmount != null) df.format(convertedAmount) else "0.00"

    fun copyResult() {
        val text = "$amountText $fromCurrencyCode = $convertedFormatted $toCurrencyCode"
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("Currency Conversion", text))
        Toast.makeText(
            context,
            if (language == AppLanguage.BANGLA) "কপি করা হয়েছে!" else "Copied to clipboard!",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun saveConversionRecord() {
        val amt = amountText.toDoubleOrNull() ?: return
        if (amt <= 0) return
        val res = convertedAmount ?: return
        scope.launch {
            currencyRepository.saveConversion(fromCurrencyCode, toCurrencyCode, amt, res)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("currency_converter_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Status & Refresh Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (state.isOffline) {
                            if (language == AppLanguage.BANGLA) "অফলাইন ক্যাশ রেট" else "Cached Offline Rates"
                        } else {
                            if (language == AppLanguage.BANGLA) "লাইভ এক্সচেঞ্জ রেট" else "Live Exchange Rates"
                        },
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (state.isOffline) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                    )
                    if (state.lastUpdatedMillis > 0) {
                        val dateStr = DateFormat.format("dd MMM yyyy, hh:mm a", state.lastUpdatedMillis)
                        Text(
                            text = "${if (language == AppLanguage.BANGLA) "আপডেট:" else "Updated:"} $dateStr",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = { fetchRates() },
                    modifier = Modifier.testTag("refresh_rates_button")
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh Rates")
                    }
                }
            }
        }

        // Favourite Currencies row with manage action
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Favorites",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == AppLanguage.BANGLA) "প্রিয় কারেন্সি" else "Favourite Currencies",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(
                        onClick = { showFavoriteDialog = true },
                        modifier = Modifier.size(28.dp).testTag("manage_fav_currencies_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Manage Favorites",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    favoriteCurrencies.forEach { code ->
                        val isSelected = toCurrencyCode == code
                        OutlinedStateButton(
                            text = code,
                            onClick = {
                                if (fromCurrencyCode == code) {
                                    fromCurrencyCode = toCurrencyCode
                                }
                                toCurrencyCode = code
                                saveConversionRecord()
                            },
                            borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                            contentColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            paddingValues = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            testTag = "fav_curr_$code"
                        )
                    }
                }
            }
        }

        // Converter Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Amount Field
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = {
                            amountText = it
                            saveConversionRecord()
                        },
                        label = { Text(if (language == AppLanguage.BANGLA) "টাকার পরিমাণ" else "Amount to Convert") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("currency_amount_input"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // From Currency Picker
                    CurrencySelectorRow(
                        label = if (language == AppLanguage.BANGLA) "উৎস কারেন্সি (From)" else "From Currency",
                        selectedInfo = fromInfo,
                        onSelect = {
                            fromCurrencyCode = it.code
                            saveConversionRecord()
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Swap Button
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = {
                                val temp = fromCurrencyCode
                                fromCurrencyCode = toCurrencyCode
                                toCurrencyCode = temp
                                saveConversionRecord()
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .testTag("swap_currency_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChangeCircle,
                                contentDescription = "Swap Currencies",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // To Currency Picker
                    CurrencySelectorRow(
                        label = if (language == AppLanguage.BANGLA) "গন্তব্য কারেন্সি (To)" else "To Currency",
                        selectedInfo = toInfo,
                        onSelect = {
                            toCurrencyCode = it.code
                            saveConversionRecord()
                        }
                    )
                }
            }
        }

        // Conversion Result Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$amountText ${fromInfo.code} =",
                            style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        IconButton(
                            onClick = { copyResult() },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "$convertedFormatted ${toInfo.symbol}",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )

                    val singleRate = run {
                        val r1 = state.rates[fromCurrencyCode] ?: 1.0
                        val r2 = state.rates[toCurrencyCode] ?: 1.0
                        if (r1 > 0) df.format(r2 / r1) else "1.0"
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "1 $fromCurrencyCode = $singleRate $toCurrencyCode",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Recent Conversions History
        if (recentConversions.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "সাম্প্রতিক রূপান্তর" else "Recent Conversions",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    OutlinedStateButton(
                        text = if (language == AppLanguage.BANGLA) "মুছুন" else "Clear",
                        onClick = { scope.launch { currencyRepository.clearRecentConversions() } },
                        icon = Icons.Default.Delete,
                        paddingValues = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            items(recentConversions.take(5)) { item ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            amountText = df.format(item.fromAmount).replace(",", "")
                            fromCurrencyCode = item.fromCurrency
                            toCurrencyCode = item.toCurrency
                        },
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${df.format(item.fromAmount)} ${item.fromCurrency} → ${df.format(item.toAmount)} ${item.toCurrency}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                text = if (language == AppLanguage.BANGLA) "পুনরায় ব্যবহার করতে ট্যাপ করুন" else "Tap to reload",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = DateFormat.format("hh:mm a", item.timestamp).toString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    // Favorite Currencies Management Dialog
    if (showFavoriteDialog) {
        AlertDialog(
            onDismissRequest = { showFavoriteDialog = false },
            title = {
                Text(
                    text = if (language == AppLanguage.BANGLA) "প্রিয় কারেন্সি তালিকা" else "Favourite Currencies"
                )
            },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth().height(320.dp)) {
                    items(SupportedCurrencies.list) { curr ->
                        val isFav = currencyRepository.isFavorite(curr.code)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { currencyRepository.toggleFavorite(curr.code) }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(curr.flagEmoji, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "${curr.code} (${curr.symbol})",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = if (language == AppLanguage.BANGLA) curr.nameBn else curr.nameEn,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            IconButton(
                                onClick = { currencyRepository.toggleFavorite(curr.code) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (isFav) Icons.Default.Star else Icons.Outlined.StarOutline,
                                    contentDescription = "Toggle Favorite",
                                    tint = if (isFav) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFavoriteDialog = false }) {
                    Text(if (language == AppLanguage.BANGLA) "সম্পন্ন" else "Done")
                }
            }
        )
    }
}

@Composable
private fun CurrencySelectorRow(
    label: String,
    selectedInfo: CurrencyInfo,
    onSelect: (CurrencyInfo) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(4.dp))
        Box {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { expanded = true },
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = selectedInfo.flagEmoji, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "${selectedInfo.code} - ${selectedInfo.nameEn}", fontWeight = FontWeight.Bold)
                    }
                    Text("▼", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.85f)
            ) {
                SupportedCurrencies.list.forEach { curr ->
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(curr.flagEmoji, fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("${curr.code} - ${curr.nameEn}", fontWeight = FontWeight.SemiBold)
                            }
                        },
                        onClick = {
                            onSelect(curr)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
