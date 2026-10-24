package com.example.ui.tools.calculator

import android.app.DatePickerDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.calendar.CalendarCalculators
import com.example.model.AppLanguage
import com.example.ui.components.OutlinedStateButton
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun AgeCalculatorScreen(
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var birthDate by remember { mutableStateOf(LocalDate.of(2000, 1, 1)) }
    val today = remember { LocalDate.now() }

    val ageResult = remember(birthDate) {
        CalendarCalculators.calculateAge(birthDate, today)
    }

    val dateFormatter = DateTimeFormatter.ofPattern("dd MMMM, yyyy")

    fun showDatePicker() {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                birthDate = LocalDate.of(year, month + 1, dayOfMonth)
            },
            birthDate.year,
            birthDate.monthValue - 1,
            birthDate.dayOfMonth
        ).apply {
            datePicker.maxDate = System.currentTimeMillis()
            show()
        }
    }

    fun buildSummaryText(): String {
        return buildString {
            append("MixTools Age Summary:\n")
            append("Born: ${birthDate.format(dateFormatter)} (${ageResult.dayOfWeekEn})\n")
            append("Age: ${ageResult.years} Years, ${ageResult.months} Months, ${ageResult.days} Days\n")
            append("Total Days Lived: ${ageResult.totalDays}\n")
            append("Next Birthday: ${ageResult.nextBirthdayDaysRemaining} days remaining\n")
            append("Bengali Date: ${ageResult.birthBanglaDate.formatEn()}\n")
            append("Hijri Date: ${ageResult.birthHijriDate.formatEn()}\n")
        }
    }

    fun copyResults() {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("MixTools Age", buildSummaryText()))
        Toast.makeText(context, if (language == AppLanguage.BANGLA) "কপি করা হয়েছে!" else "Age summary copied!", Toast.LENGTH_SHORT).show()
    }

    fun shareResults() {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, buildSummaryText())
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(sendIntent, "Share Age Summary"))
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("age_calculator_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Current Today Dates (English, Bangla, Hijri)
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "আজকের দিনপঞ্জি (Current Dates)" else "Today's Calendars",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "📅 ${today.format(dateFormatter)}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = "🇧🇩 ${if (language == AppLanguage.BANGLA) ageResult.currentBanglaDate.formatBn() else ageResult.currentBanglaDate.formatEn()}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "🌙 ${if (language == AppLanguage.BANGLA) ageResult.currentHijriDate.formatBn() else ageResult.currentHijriDate.formatEn()}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Date of Birth Picker Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "জন্মতারিখ নির্বাচন করুন" else "Select Date of Birth",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker() }
                            .testTag("dob_picker_button"),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = birthDate.format(dateFormatter),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = if (language == AppLanguage.BANGLA) "জন্মবার: ${ageResult.dayOfWeekBn}" else "Day of Week: ${ageResult.dayOfWeekEn}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Pick Date",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedStateButton(
                            text = if (language == AppLanguage.BANGLA) "রিসেট" else "Reset",
                            onClick = { birthDate = LocalDate.of(2000, 1, 1) },
                            icon = Icons.Default.Refresh,
                            modifier = Modifier.weight(1f),
                            testTag = "age_reset_button"
                        )
                        OutlinedStateButton(
                            text = if (language == AppLanguage.BANGLA) "কপি" else "Copy",
                            onClick = { copyResults() },
                            icon = Icons.Default.ContentCopy,
                            modifier = Modifier.weight(1f),
                            testTag = "age_copy_button"
                        )
                        OutlinedStateButton(
                            text = if (language == AppLanguage.BANGLA) "শেয়ার" else "Share",
                            onClick = { shareResults() },
                            icon = Icons.Default.Share,
                            borderColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f),
                            testTag = "age_share_button"
                        )
                    }
                }
            }
        }

        // Calculated Age Hero Display
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "বর্তমান বয়স" else "Current Age",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        AgePill(value = ageResult.years, unit = if (language == AppLanguage.BANGLA) "বছর" else "Years")
                        AgePill(value = ageResult.months, unit = if (language == AppLanguage.BANGLA) "মাস" else "Months")
                        AgePill(value = ageResult.days, unit = if (language == AppLanguage.BANGLA) "দিন" else "Days")
                    }
                }
            }
        }

        // Birthday Countdown & Life Statistics
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "পরবর্তী জন্মদিন ও জীবন পরিসংখ্যান" else "Next Birthday & Lifetime Stats",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val stats = listOf(
                        (if (language == AppLanguage.BANGLA) "পরবর্তী জন্মদিন" else "Next Birthday") to "${ageResult.nextBirthdayDate.format(dateFormatter)} (${ageResult.nextBirthdayDaysRemaining} days remaining)",
                        (if (language == AppLanguage.BANGLA) "মোট অতিক্রান্ত দিন" else "Total Days Lived") to "${ageResult.totalDays} days",
                        (if (language == AppLanguage.BANGLA) "মোট সপ্তাহ" else "Total Weeks") to "${ageResult.totalWeeks} weeks",
                        (if (language == AppLanguage.BANGLA) "মোট মাস" else "Total Months") to "${ageResult.totalMonths} months",
                        (if (language == AppLanguage.BANGLA) "মোট ঘণ্টা" else "Total Hours Lived") to "${ageResult.totalHours} hours"
                    )

                    stats.forEach { (label, value) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }
        }

        // Corresponding Bangla & Hijri Birth Dates
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "বাংলা ও হিজরি জন্ম সন" else "Bangla & Hijri Birth Calendar",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (language == AppLanguage.BANGLA)
                            "বাংলা জন্মতারিখ: ${ageResult.birthBanglaDate.formatBn()} (${ageResult.birthBanglaDate.seasonBn}কাল)"
                        else
                            "Bangla Birth Date: ${ageResult.birthBanglaDate.formatEn()} (${ageResult.birthBanglaDate.seasonEn})",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (language == AppLanguage.BANGLA)
                            "হিজরি জন্মতারিখ: ${ageResult.birthHijriDate.formatBn()}"
                        else
                            "Hijri Birth Date: ${ageResult.birthHijriDate.formatEn()}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == AppLanguage.BANGLA)
                                "হিজরি ক্যালেন্ডার জ্যোতির্বৈজ্ঞানিক হিসাবের ভিত্তিতে প্রস্তুত। স্থানীয় চাঁদ দেখার ওপর ভিত্তি করে ১ দিনের ব্যবধান হতে পারে।"
                            else
                                "Hijri dates use astronomical calculations. Local moon-sighting differences apply; date may vary by 1 day.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AgePill(value: Int, unit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "$value",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
        )
        Text(
            text = unit,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
