package com.example.ui.tools.calculator

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChangeCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.model.AppLanguage
import com.example.ui.components.OutlinedStateButton
import java.text.DecimalFormat

@Composable
fun ComprehensiveUnitConverterScreen(
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val categories = listOf("Length", "Weight", "Temperature", "Area", "Volume", "Speed", "Time", "Data")
    var selectedCategory by remember { mutableStateOf("Length") }

    val unitsMap = mapOf(
        "Length" to listOf("Meters", "Kilometers", "Centimeters", "Millimeters", "Miles", "Yards", "Feet", "Inches", "Nautical Miles"),
        "Weight" to listOf("Kilograms", "Grams", "Milligrams", "Metric Tons", "Pounds", "Ounces", "Stones"),
        "Temperature" to listOf("Celsius", "Fahrenheit", "Kelvin", "Rankine"),
        "Area" to listOf("Square Meters", "Square Kilometers", "Square Feet", "Square Yards", "Acres", "Hectares", "Bigha (বিঘা)", "Katha (কাঠা)"),
        "Volume" to listOf("Liters", "Milliliters", "Cubic Meters", "Gallons (US)", "Quarts", "Pints", "Cups", "Fluid Ounces"),
        "Speed" to listOf("km/h", "m/s", "mph", "Knot", "ft/s"),
        "Time" to listOf("Seconds", "Minutes", "Hours", "Days", "Weeks", "Months (30.4d)", "Years (365.25d)"),
        "Data" to listOf("Bytes (B)", "Kilobytes (KB)", "Megabytes (MB)", "Gigabytes (GB)", "Terabytes (TB)", "Petabytes (PB)", "Kibibytes (KiB)", "Mebibytes (MiB)", "Gibibytes (GiB)")
    )

    var fromUnit by remember { mutableStateOf("Meters") }
    var toUnit by remember { mutableStateOf("Kilometers") }
    var inputValue by remember { mutableStateOf("1000") }

    LaunchedEffect(selectedCategory) {
        val list = unitsMap[selectedCategory] ?: emptyList()
        if (list.size >= 2) {
            fromUnit = list[0]
            toUnit = list[1]
        }
    }

    val df = DecimalFormat("#,##0.######")

    fun convert(): Double {
        val v = inputValue.toDoubleOrNull() ?: return 0.0

        return when (selectedCategory) {
            "Length" -> {
                val inMeters = when (fromUnit) {
                    "Meters" -> v
                    "Kilometers" -> v * 1000.0
                    "Centimeters" -> v * 0.01
                    "Millimeters" -> v * 0.001
                    "Miles" -> v * 1609.344
                    "Yards" -> v * 0.9144
                    "Feet" -> v * 0.3048
                    "Inches" -> v * 0.0254
                    "Nautical Miles" -> v * 1852.0
                    else -> v
                }
                when (toUnit) {
                    "Meters" -> inMeters
                    "Kilometers" -> inMeters / 1000.0
                    "Centimeters" -> inMeters / 0.01
                    "Millimeters" -> inMeters / 0.001
                    "Miles" -> inMeters / 1609.344
                    "Yards" -> inMeters / 0.9144
                    "Feet" -> inMeters / 0.3048
                    "Inches" -> inMeters / 0.0254
                    "Nautical Miles" -> inMeters / 1852.0
                    else -> inMeters
                }
            }
            "Weight" -> {
                val inKg = when (fromUnit) {
                    "Kilograms" -> v
                    "Grams" -> v * 0.001
                    "Milligrams" -> v * 1e-6
                    "Metric Tons" -> v * 1000.0
                    "Pounds" -> v * 0.45359237
                    "Ounces" -> v * 0.02834952
                    "Stones" -> v * 6.35029318
                    else -> v
                }
                when (toUnit) {
                    "Kilograms" -> inKg
                    "Grams" -> inKg / 0.001
                    "Milligrams" -> inKg / 1e-6
                    "Metric Tons" -> inKg / 1000.0
                    "Pounds" -> inKg / 0.45359237
                    "Ounces" -> inKg / 0.02834952
                    "Stones" -> inKg / 6.35029318
                    else -> inKg
                }
            }
            "Temperature" -> {
                val inCelsius = when (fromUnit) {
                    "Celsius" -> v
                    "Fahrenheit" -> (v - 32.0) * 5.0 / 9.0
                    "Kelvin" -> v - 273.15
                    "Rankine" -> (v - 491.67) * 5.0 / 9.0
                    else -> v
                }
                when (toUnit) {
                    "Celsius" -> inCelsius
                    "Fahrenheit" -> (inCelsius * 9.0 / 5.0) + 32.0
                    "Kelvin" -> inCelsius + 273.15
                    "Rankine" -> (inCelsius + 273.15) * 9.0 / 5.0
                    else -> inCelsius
                }
            }
            "Area" -> {
                val inSqm = when (fromUnit) {
                    "Square Meters" -> v
                    "Square Kilometers" -> v * 1_000_000.0
                    "Square Feet" -> v * 0.092903
                    "Square Yards" -> v * 0.836127
                    "Acres" -> v * 4046.856
                    "Hectares" -> v * 10000.0
                    "Bigha (বিঘা)" -> v * 1337.8 // 1 Bigha ≈ 14,400 sq ft in Bengal
                    "Katha (কাঠা)" -> v * 66.89 // 1 Katha = 1/20 Bigha ≈ 720 sq ft
                    else -> v
                }
                when (toUnit) {
                    "Square Meters" -> inSqm
                    "Square Kilometers" -> inSqm / 1_000_000.0
                    "Square Feet" -> inSqm / 0.092903
                    "Square Yards" -> inSqm / 0.836127
                    "Acres" -> inSqm / 4046.856
                    "Hectares" -> inSqm / 10000.0
                    "Bigha (বিঘা)" -> inSqm / 1337.8
                    "Katha (কাঠা)" -> inSqm / 66.89
                    else -> inSqm
                }
            }
            "Volume" -> {
                val inLiters = when (fromUnit) {
                    "Liters" -> v
                    "Milliliters" -> v * 0.001
                    "Cubic Meters" -> v * 1000.0
                    "Gallons (US)" -> v * 3.78541
                    "Quarts" -> v * 0.946353
                    "Pints" -> v * 0.473176
                    "Cups" -> v * 0.24
                    "Fluid Ounces" -> v * 0.0295735
                    else -> v
                }
                when (toUnit) {
                    "Liters" -> inLiters
                    "Milliliters" -> inLiters / 0.001
                    "Cubic Meters" -> inLiters / 1000.0
                    "Gallons (US)" -> inLiters / 3.78541
                    "Quarts" -> inLiters / 0.946353
                    "Pints" -> inLiters / 0.473176
                    "Cups" -> inLiters / 0.24
                    "Fluid Ounces" -> inLiters / 0.0295735
                    else -> inLiters
                }
            }
            "Speed" -> {
                val inKmh = when (fromUnit) {
                    "km/h" -> v
                    "m/s" -> v * 3.6
                    "mph" -> v * 1.609344
                    "Knot" -> v * 1.852
                    "ft/s" -> v * 1.09728
                    else -> v
                }
                when (toUnit) {
                    "km/h" -> inKmh
                    "m/s" -> inKmh / 3.6
                    "mph" -> inKmh / 1.609344
                    "Knot" -> inKmh / 1.852
                    "ft/s" -> inKmh / 1.09728
                    else -> inKmh
                }
            }
            "Time" -> {
                val inSeconds = when (fromUnit) {
                    "Seconds" -> v
                    "Minutes" -> v * 60.0
                    "Hours" -> v * 3600.0
                    "Days" -> v * 86400.0
                    "Weeks" -> v * 604800.0
                    "Months (30.4d)" -> v * 2629746.0
                    "Years (365.25d)" -> v * 31557600.0
                    else -> v
                }
                when (toUnit) {
                    "Seconds" -> inSeconds
                    "Minutes" -> inSeconds / 60.0
                    "Hours" -> inSeconds / 3600.0
                    "Days" -> inSeconds / 86400.0
                    "Weeks" -> inSeconds / 604800.0
                    "Months (30.4d)" -> inSeconds / 2629746.0
                    "Years (365.25d)" -> inSeconds / 31557600.0
                    else -> inSeconds
                }
            }
            "Data" -> {
                val inBytes = when (fromUnit) {
                    "Bytes (B)" -> v
                    "Kilobytes (KB)" -> v * 1000.0
                    "Megabytes (MB)" -> v * 1e6
                    "Gigabytes (GB)" -> v * 1e9
                    "Terabytes (TB)" -> v * 1e12
                    "Petabytes (PB)" -> v * 1e15
                    "Kibibytes (KiB)" -> v * 1024.0
                    "Mebibytes (MiB)" -> v * 1048576.0
                    "Gibibytes (GiB)" -> v * 1073741824.0
                    else -> v
                }
                when (toUnit) {
                    "Bytes (B)" -> inBytes
                    "Kilobytes (KB)" -> inBytes / 1000.0
                    "Megabytes (MB)" -> inBytes / 1e6
                    "Gigabytes (GB)" -> inBytes / 1e9
                    "Terabytes (TB)" -> inBytes / 1e12
                    "Petabytes (PB)" -> inBytes / 1e15
                    "Kibibytes (KiB)" -> inBytes / 1024.0
                    "Mebibytes (MiB)" -> inBytes / 1048576.0
                    "Gibibytes (GiB)" -> inBytes / 1073741824.0
                    else -> inBytes
                }
            }
            else -> v
        }
    }

    val convertedValue = convert()
    val formattedResult = df.format(convertedValue)

    fun copyResult() {
        val text = "$inputValue $fromUnit = $formattedResult $toUnit"
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("Unit Conversion", text))
        Toast.makeText(context, if (language == AppLanguage.BANGLA) "কপি করা হয়েছে!" else "Copied!", Toast.LENGTH_SHORT).show()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("comprehensive_unit_converter_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Category Pills
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    OutlinedStateButton(
                        text = cat,
                        onClick = { selectedCategory = cat },
                        borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                        contentColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        paddingValues = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        testTag = "unit_cat_$cat"
                    )
                }
            }
        }

        // Input Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "ইনপুট মান ও উৎস একক" else "Input Value & From Unit",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = inputValue,
                        onValueChange = { inputValue = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("unit_input_value"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    UnitPickerBox(
                        selected = fromUnit,
                        options = unitsMap[selectedCategory] ?: emptyList(),
                        onSelect = { fromUnit = it }
                    )
                }
            }
        }

        // Swap Button
        item {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = {
                        val temp = fromUnit
                        fromUnit = toUnit
                        toUnit = temp
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .testTag("swap_units_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.ChangeCircle,
                        contentDescription = "Swap",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Result Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$inputValue $fromUnit =",
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
                        text = "$formattedResult $toUnit",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    UnitPickerBox(
                        selected = toUnit,
                        options = unitsMap[selectedCategory] ?: emptyList(),
                        onSelect = { toUnit = it }
                    )
                }
            }
        }
    }
}

@Composable
private fun UnitPickerBox(
    selected: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { expanded = true },
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(selected, fontWeight = FontWeight.Bold)
                Text("▼", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { opt ->
                DropdownMenuItem(
                    text = { Text(opt) },
                    onClick = {
                        onSelect(opt)
                        expanded = false
                    }
                )
            }
        }
    }
}
