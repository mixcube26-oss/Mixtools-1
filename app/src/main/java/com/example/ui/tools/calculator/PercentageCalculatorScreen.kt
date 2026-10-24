package com.example.ui.tools.calculator

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.ui.components.OutlinedStateButton
import java.text.DecimalFormat
import kotlin.math.abs

enum class PercentMode(
    val titleEn: String,
    val titleBn: String,
    val labelXEn: String,
    val labelXBn: String,
    val labelYEn: String,
    val labelYBn: String,
    val exampleEn: String,
    val exampleBn: String
) {
    PERCENT_OF(
        titleEn = "X% of Y",
        titleBn = "Y এর X%",
        labelXEn = "Percentage (%)",
        labelXBn = "শতাংশ (%)",
        labelYEn = "Total Value (Y)",
        labelYBn = "মোট সংখ্যা (Y)",
        exampleEn = "Example: What is 15% of 200? Result: 30",
        exampleBn = "উদাহরণ: ২০০ এর ১৫% কত? ফলাফল: ৩০"
    ),
    IS_WHAT_PERCENT(
        titleEn = "X is what % of Y",
        titleBn = "X হল Y এর কত %",
        labelXEn = "Part Value (X)",
        labelXBn = "অংশ মান (X)",
        labelYEn = "Whole Value (Y)",
        labelYBn = "মূল সংখ্যা (Y)",
        exampleEn = "Example: 25 is what % of 200? Result: 12.5%",
        exampleBn = "উদাহরণ: ২৫ হল ২০০ এর কত শতাংশ? ফলাফল: ১২.৫%"
    ),
    PERCENT_INCREASE(
        titleEn = "% Increase",
        titleBn = "% বৃদ্ধি",
        labelXEn = "Initial Value (From)",
        labelXBn = "প্রাথমিক মান",
        labelYEn = "Final Value (To)",
        labelYBn = "নতুন বৃদ্ধিপ্রাপ্ত মান",
        exampleEn = "Example: From 100 to 135 is a 35% increase (+35)",
        exampleBn = "উদাহরণ: ১০০ থেকে ১৩৫ হলে বৃদ্ধি ৩৫% (+৩৫)"
    ),
    PERCENT_DECREASE(
        titleEn = "% Decrease",
        titleBn = "% হ্রাস",
        labelXEn = "Initial Value (From)",
        labelXBn = "প্রাথমিক মান",
        labelYEn = "Reduced Value (To)",
        labelYBn = "নতুন হ্রাসপ্রাপ্ত মান",
        exampleEn = "Example: From 200 to 150 is a 25% decrease (-50)",
        exampleBn = "উদাহরণ: ২০০ থেকে ১৫০ হলে হ্রাস ২৫% (-৫০)"
    ),
    PERCENT_DIFFERENCE(
        titleEn = "% Difference",
        titleBn = "% পার্থক্য",
        labelXEn = "First Value (V1)",
        labelXBn = "প্রথম মান (V1)",
        labelYEn = "Second Value (V2)",
        labelYBn = "দ্বিতীয় মান (V2)",
        exampleEn = "Example: Difference between 50 and 70 is 33.3333%",
        exampleBn = "উদাহরণ: ৫০ ও ৭০ এর মধ্যকার শতকরা পার্থক্য ৩৩.৩৩%"
    ),
    DISCOUNT(
        titleEn = "Discount Price",
        titleBn = "ডিসকাউন্ট ও ছাড়",
        labelXEn = "Original Price",
        labelXBn = "মূল দাম",
        labelYEn = "Discount Rate (%)",
        labelYBn = "ছাড়ের হার (%)",
        exampleEn = "Example: $250 item with 20% discount = $200 (Saved: $50)",
        exampleBn = "উদাহরণ: ২৫০ টাকার পণ্যে ২০% ছাড় = ২০০ টাকা (সাশ্রয় ৫০ টাকা)"
    )
}

data class CalculationOutput(
    val mainResult: String,
    val formulaBreakdown: String,
    val isError: Boolean = false
)

@Composable
fun PercentageCalculatorScreen(
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedMode by remember { mutableStateOf(PercentMode.PERCENT_OF) }

    var valX by remember { mutableStateOf("15") }
    var valY by remember { mutableStateOf("200") }

    val df = remember { DecimalFormat("#,##0.######") }
    val currencyDf = remember { DecimalFormat("#,##0.00") }

    fun copyResult(text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("Percentage Result", text))
        Toast.makeText(
            context,
            if (language == AppLanguage.BANGLA) "ফলাফল কপি করা হয়েছে!" else "Result copied to clipboard!",
            Toast.LENGTH_SHORT
        ).show()
    }

    val output: CalculationOutput = run {
        if (valX.isBlank() || valY.isBlank()) {
            return@run CalculationOutput(
                mainResult = if (language == AppLanguage.BANGLA) "দুটি মান লিখুন" else "Enter both values",
                formulaBreakdown = if (language == AppLanguage.BANGLA) "হিসাব করার জন্য উভয় ইনপুট পূরণ করুন" else "Fill in both fields to calculate",
                isError = false
            )
        }

        val x = valX.toDoubleOrNull()
        val y = valY.toDoubleOrNull()

        if (x == null || y == null) {
            return@run CalculationOutput(
                mainResult = if (language == AppLanguage.BANGLA) "ভুল ইনপুট" else "Invalid numbers",
                formulaBreakdown = if (language == AppLanguage.BANGLA) "দয়া করে সঠিক সংখ্যা লিখুন" else "Please enter valid numeric values",
                isError = true
            )
        }

        when (selectedMode) {
            PercentMode.PERCENT_OF -> {
                val res = (x / 100.0) * y
                CalculationOutput(
                    mainResult = df.format(res),
                    formulaBreakdown = "$x% × $y = ${df.format(res)}"
                )
            }
            PercentMode.IS_WHAT_PERCENT -> {
                if (y == 0.0) {
                    CalculationOutput(
                        mainResult = if (language == AppLanguage.BANGLA) "শূন্য দিয়ে ভাগ অসম্ভব" else "Cannot divide by zero",
                        formulaBreakdown = if (language == AppLanguage.BANGLA) "মূল সংখ্যা (Y) শূন্য হতে পারে না" else "Base value (Y) cannot be zero",
                        isError = true
                    )
                } else {
                    val pct = (x / y) * 100.0
                    CalculationOutput(
                        mainResult = "${df.format(pct)}%",
                        formulaBreakdown = "($x ÷ $y) × 100 = ${df.format(pct)}%"
                    )
                }
            }
            PercentMode.PERCENT_INCREASE -> {
                if (x == 0.0) {
                    CalculationOutput(
                        mainResult = if (language == AppLanguage.BANGLA) "প্রাথমিক মান ০ হতে পারে না" else "Initial value cannot be zero",
                        formulaBreakdown = if (language == AppLanguage.BANGLA) "শূন্য থেকে শতকরা বৃদ্ধি অনির্ণেয়" else "Percentage increase from zero is undefined",
                        isError = true
                    )
                } else {
                    val diff = y - x
                    val pct = (diff / abs(x)) * 100.0
                    CalculationOutput(
                        mainResult = "${df.format(pct)}% ${if (pct >= 0) if (language == AppLanguage.BANGLA) "বৃদ্ধি" else "Increase" else if (language == AppLanguage.BANGLA) "হ্রাস" else "Decrease"}",
                        formulaBreakdown = "(($y - $x) ÷ |$x|) × 100 | ${if (language == AppLanguage.BANGLA) "পার্থক্য" else "Diff"}: ${df.format(diff)}"
                    )
                }
            }
            PercentMode.PERCENT_DECREASE -> {
                if (x == 0.0) {
                    CalculationOutput(
                        mainResult = if (language == AppLanguage.BANGLA) "প্রাথমিক মান ০ হতে পারে না" else "Initial value cannot be zero",
                        formulaBreakdown = if (language == AppLanguage.BANGLA) "শূন্য থেকে শতকরা হ্রাস অনির্ণেয়" else "Percentage decrease from zero is undefined",
                        isError = true
                    )
                } else {
                    val diff = x - y
                    val pct = (diff / abs(x)) * 100.0
                    CalculationOutput(
                        mainResult = "${df.format(pct)}% ${if (language == AppLanguage.BANGLA) "হ্রাস" else "Decrease"}",
                        formulaBreakdown = "(($x - $y) ÷ |$x|) × 100 | ${if (language == AppLanguage.BANGLA) "কমেছে" else "Reduced by"}: ${df.format(diff)}"
                    )
                }
            }
            PercentMode.PERCENT_DIFFERENCE -> {
                val average = (abs(x) + abs(y)) / 2.0
                if (average == 0.0) {
                    CalculationOutput(
                        mainResult = "0%",
                        formulaBreakdown = "|0 - 0| ÷ 0 = 0%"
                    )
                } else {
                    val diff = abs(x - y)
                    val pct = (diff / average) * 100.0
                    CalculationOutput(
                        mainResult = "${df.format(pct)}%",
                        formulaBreakdown = "(|$x - $y| ÷ (($x + $y) / 2)) × 100 = ${df.format(pct)}%"
                    )
                }
            }
            PercentMode.DISCOUNT -> {
                val originalPrice = x
                val discountRate = y
                val amountSaved = originalPrice * (discountRate / 100.0)
                val finalPrice = originalPrice - amountSaved
                CalculationOutput(
                    mainResult = "${if (language == AppLanguage.BANGLA) "চূড়ান্ত মূল্য:" else "Final Price:"} ${currencyDf.format(finalPrice)}",
                    formulaBreakdown = "${if (language == AppLanguage.BANGLA) "সাশ্রয়:" else "You Save:"} ${currencyDf.format(amountSaved)} ($discountRate%)"
                )
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("percentage_calculator_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Mode Selector Horizontal Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PercentMode.entries.forEach { mode ->
                    val isSelected = selectedMode == mode
                    OutlinedStateButton(
                        text = if (language == AppLanguage.BANGLA) mode.titleBn else mode.titleEn,
                        onClick = { selectedMode = mode },
                        borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                        contentColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        testTag = "pct_mode_${mode.name}"
                    )
                }
            }
        }

        // Example & Guide Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Example",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (language == AppLanguage.BANGLA) selectedMode.exampleBn else selectedMode.exampleEn,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Inputs Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    val labelX = if (language == AppLanguage.BANGLA) selectedMode.labelXBn else selectedMode.labelXEn
                    val labelY = if (language == AppLanguage.BANGLA) selectedMode.labelYBn else selectedMode.labelYEn

                    OutlinedTextField(
                        value = valX,
                        onValueChange = { valX = it },
                        label = { Text(labelX) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("pct_input_x"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = valY,
                        onValueChange = { valY = it },
                        label = { Text(labelY) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("pct_input_y"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedStateButton(
                            text = if (language == AppLanguage.BANGLA) "মুছুন / রিসেট" else "Clear / Reset",
                            onClick = {
                                valX = ""
                                valY = ""
                            },
                            icon = Icons.Default.Refresh,
                            modifier = Modifier.weight(1f),
                            testTag = "pct_reset_button"
                        )

                        OutlinedStateButton(
                            text = if (language == AppLanguage.BANGLA) "ফলাফল কপি" else "Copy Result",
                            onClick = {
                                copyResult("${output.mainResult} (${output.formulaBreakdown})")
                            },
                            icon = Icons.Default.ContentCopy,
                            borderColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f),
                            testTag = "pct_copy_button"
                        )
                    }
                }
            }
        }

        // Result Hero Display
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = if (output.isError) {
                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f)
                } else {
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                },
                border = BorderStroke(
                    1.5.dp,
                    if (output.isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (language == AppLanguage.BANGLA) "গণনা ফলাফল" else "Calculated Result",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (output.isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            )
                        )
                        IconButton(
                            onClick = { copyResult("${output.mainResult} [${output.formulaBreakdown}]") },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy",
                                tint = if (output.isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = output.mainResult,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = if (output.isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = output.formulaBreakdown,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
