package com.example.ui.tools.calculator

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.ChangeCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.ui.components.OutlinedStateButton
import kotlinx.coroutines.delay
import java.text.DecimalFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan
import kotlin.random.Random

// ---------------- CALCULATOR ----------------
@Composable
fun CalculatorScreen(
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    var expression by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("0") }
    var isScientific by remember { mutableStateOf(false) }

    fun calculateResult() {
        if (expression.isBlank()) {
            resultText = "0"
            return
        }
        try {
            val eval = SimpleMathEvaluator.evaluate(expression)
            val df = DecimalFormat("#.########")
            resultText = df.format(eval)
        } catch (_: Exception) {
            resultText = "Error"
        }
    }

    fun onKeyClick(key: String) {
        when (key) {
            "C" -> {
                expression = ""
                resultText = "0"
            }
            "⌫" -> {
                if (expression.isNotEmpty()) {
                    expression = expression.dropLast(1)
                    if (expression.isNotEmpty()) calculateResult() else resultText = "0"
                }
            }
            "=" -> {
                calculateResult()
                if (resultText != "Error") {
                    expression = resultText
                }
            }
            "±" -> {
                if (expression.startsWith("-")) {
                    expression = expression.removePrefix("-")
                } else if (expression.isNotEmpty()) {
                    expression = "-$expression"
                }
                calculateResult()
            }
            "sin", "cos", "tan", "ln", "log", "√" -> {
                expression += "$key("
            }
            "π" -> expression += "3.14159"
            "e" -> expression += "2.71828"
            else -> {
                expression += key
                calculateResult()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("calculator_screen"),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Display Area
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { isScientific = !isScientific },
                        modifier = Modifier.testTag("toggle_scientific_button")
                    ) {
                        Text(
                            text = if (isScientific) "Standard" else "Scientific",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = if (language == AppLanguage.BANGLA) "হিসাব" else "Calculator",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = expression.ifEmpty { "0" },
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 26.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = resultText,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        maxLines = 1
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Keypad Grid
        val standardKeys = listOf(
            listOf("C", "(", ")", "÷"),
            listOf("7", "8", "9", "×"),
            listOf("4", "5", "6", "-"),
            listOf("1", "2", "3", "+"),
            listOf("±", "0", ".", "=")
        )

        val scientificRow = listOf("sin", "cos", "tan", "√", "log", "π")

        if (isScientific) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                scientificRow.forEach { key ->
                    CalcButton(
                        text = key,
                        isAccent = false,
                        isOperator = true,
                        onClick = { onKeyClick(key) },
                        modifier = Modifier.size(52.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            standardKeys.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    row.forEach { key ->
                        val isOp = key in listOf("÷", "×", "-", "+", "=", "C", "⌫")
                        val isAccent = key == "="
                        CalcButton(
                            text = key,
                            isAccent = isAccent,
                            isOperator = isOp,
                            onClick = { onKeyClick(key) },
                            modifier = Modifier
                                .weight(1f)
                                .height(58.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CalcButton(
    text: String,
    isAccent: Boolean,
    isOperator: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.92f else 1f, label = "calc_key")

    val bg = when {
        isAccent -> MaterialTheme.colorScheme.primary
        isOperator -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val fg = when {
        isAccent -> MaterialTheme.colorScheme.onPrimary
        isOperator -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }

    Surface(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.material3.ripple(),
                onClick = onClick
            )
            .testTag("calc_key_$text"),
        shape = RoundedCornerShape(16.dp),
        color = bg,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = fg
            )
        }
    }
}

private object SimpleMathEvaluator {
    fun evaluate(expr: String): Double {
        val sanitized = expr
            .replace("×", "*")
            .replace("÷", "/")
            .replace(" ", "")

        return parseExpression(sanitized)
    }

    private fun parseExpression(str: String): Double {
        var s = str
        // Handle parentheses
        while (s.contains("(") && s.contains(")")) {
            val start = s.lastIndexOf("(")
            val end = s.indexOf(")", start)
            if (end != -1) {
                val inner = s.substring(start + 1, end)
                val innerVal = parseExpression(inner)
                // Check if preceded by function
                val prefix = s.substring(0, start)
                val (fnName, newPrefix) = extractFunction(prefix)
                val computed = when (fnName) {
                    "sin" -> sin(Math.toRadians(innerVal))
                    "cos" -> cos(Math.toRadians(innerVal))
                    "tan" -> tan(Math.toRadians(innerVal))
                    "ln" -> ln(innerVal)
                    "log" -> log10(innerVal)
                    "√" -> sqrt(innerVal)
                    else -> innerVal
                }
                s = newPrefix + computed.toString() + s.substring(end + 1)
            } else break
        }

        // Basic operators + - * /
        val tokens = mutableListOf<String>()
        var num = ""
        var i = 0
        while (i < s.length) {
            val c = s[i]
            if (c in "+-*/") {
                if (num.isNotEmpty()) {
                    tokens.add(num)
                    num = ""
                } else if (c == '-' && (tokens.isEmpty() || tokens.last() in "+-*/")) {
                    num += "-"
                    i++
                    continue
                }
                tokens.add(c.toString())
            } else {
                num += c
            }
            i++
        }
        if (num.isNotEmpty()) tokens.add(num)

        if (tokens.isEmpty()) return 0.0

        // Perform * and /
        val afterMulDiv = mutableListOf<String>()
        var idx = 0
        while (idx < tokens.size) {
            val t = tokens[idx]
            if (t == "*" || t == "/") {
                val prev = afterMulDiv.removeLast().toDouble()
                val next = tokens[idx + 1].toDouble()
                val res = if (t == "*") prev * next else (if (next != 0.0) prev / next else 0.0)
                afterMulDiv.add(res.toString())
                idx += 2
            } else {
                afterMulDiv.add(t)
                idx++
            }
        }

        // Perform + and -
        var finalVal = afterMulDiv.firstOrNull()?.toDoubleOrNull() ?: 0.0
        var opIdx = 1
        while (opIdx < afterMulDiv.size) {
            val op = afterMulDiv[opIdx]
            val nextVal = afterMulDiv.getOrNull(opIdx + 1)?.toDoubleOrNull() ?: 0.0
            if (op == "+") finalVal += nextVal
            if (op == "-") finalVal -= nextVal
            opIdx += 2
        }

        return finalVal
    }

    private fun extractFunction(prefix: String): Pair<String, String> {
        val fns = listOf("sin", "cos", "tan", "ln", "log", "√")
        for (f in fns) {
            if (prefix.endsWith(f)) {
                return Pair(f, prefix.removeSuffix(f))
            }
        }
        return Pair("", prefix)
    }
}

// ---------------- UNIT CONVERTER ----------------
@Composable
fun UnitConverterScreen(
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val categories = listOf("Length", "Weight", "Temperature", "Speed", "Area")
    var selectedCategory by remember { mutableStateOf("Length") }

    val unitsMap = mapOf(
        "Length" to listOf("Meters", "Kilometers", "Centimeters", "Millimeters", "Feet", "Inches", "Miles"),
        "Weight" to listOf("Kilograms", "Grams", "Milligrams", "Pounds", "Ounces"),
        "Temperature" to listOf("Celsius", "Fahrenheit", "Kelvin"),
        "Speed" to listOf("km/h", "m/s", "mph", "Knot"),
        "Area" to listOf("Square Meters", "Square Kilometers", "Square Feet", "Acre", "Hectare")
    )

    var fromUnit by remember { mutableStateOf("Meters") }
    var toUnit by remember { mutableStateOf("Kilometers") }
    var inputValue by remember { mutableStateOf("1000") }

    // Update units when category changes
    LaunchedEffect(selectedCategory) {
        val list = unitsMap[selectedCategory] ?: emptyList()
        if (list.size >= 2) {
            fromUnit = list[0]
            toUnit = list[1]
        }
    }

    fun convert(): String {
        val v = inputValue.toDoubleOrNull() ?: return "0"
        return when (selectedCategory) {
            "Length" -> {
                val inMeters = when (fromUnit) {
                    "Meters" -> v
                    "Kilometers" -> v * 1000.0
                    "Centimeters" -> v * 0.01
                    "Millimeters" -> v * 0.001
                    "Feet" -> v * 0.3048
                    "Inches" -> v * 0.0254
                    "Miles" -> v * 1609.34
                    else -> v
                }
                val result = when (toUnit) {
                    "Meters" -> inMeters
                    "Kilometers" -> inMeters / 1000.0
                    "Centimeters" -> inMeters / 0.01
                    "Millimeters" -> inMeters / 0.001
                    "Feet" -> inMeters / 0.3048
                    "Inches" -> inMeters / 0.0254
                    "Miles" -> inMeters / 1609.34
                    else -> inMeters
                }
                DecimalFormat("#.######").format(result)
            }
            "Weight" -> {
                val inKg = when (fromUnit) {
                    "Kilograms" -> v
                    "Grams" -> v * 0.001
                    "Milligrams" -> v * 1e-6
                    "Pounds" -> v * 0.453592
                    "Ounces" -> v * 0.0283495
                    else -> v
                }
                val result = when (toUnit) {
                    "Kilograms" -> inKg
                    "Grams" -> inKg / 0.001
                    "Milligrams" -> inKg / 1e-6
                    "Pounds" -> inKg / 0.453592
                    "Ounces" -> inKg / 0.0283495
                    else -> inKg
                }
                DecimalFormat("#.######").format(result)
            }
            "Temperature" -> {
                val inCelsius = when (fromUnit) {
                    "Celsius" -> v
                    "Fahrenheit" -> (v - 32) * 5 / 9
                    "Kelvin" -> v - 273.15
                    else -> v
                }
                val result = when (toUnit) {
                    "Celsius" -> inCelsius
                    "Fahrenheit" -> (inCelsius * 9 / 5) + 32
                    "Kelvin" -> inCelsius + 273.15
                    else -> inCelsius
                }
                DecimalFormat("#.##").format(result)
            }
            "Speed" -> {
                val inKmh = when (fromUnit) {
                    "km/h" -> v
                    "m/s" -> v * 3.6
                    "mph" -> v * 1.60934
                    "Knot" -> v * 1.852
                    else -> v
                }
                val result = when (toUnit) {
                    "km/h" -> inKmh
                    "m/s" -> inKmh / 3.6
                    "mph" -> inKmh / 1.60934
                    "Knot" -> inKmh / 1.852
                    else -> inKmh
                }
                DecimalFormat("#.####").format(result)
            }
            "Area" -> {
                val inSqm = when (fromUnit) {
                    "Square Meters" -> v
                    "Square Kilometers" -> v * 1_000_000.0
                    "Square Feet" -> v * 0.092903
                    "Acre" -> v * 4046.86
                    "Hectare" -> v * 10000.0
                    else -> v
                }
                val result = when (toUnit) {
                    "Square Meters" -> inSqm
                    "Square Kilometers" -> inSqm / 1_000_000.0
                    "Square Feet" -> inSqm / 0.092903
                    "Acre" -> inSqm / 4046.86
                    "Hectare" -> inSqm / 10000.0
                    else -> inSqm
                }
                DecimalFormat("#.######").format(result)
            }
            else -> v.toString()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("unit_converter_screen"),
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
                        testTag = "unit_cat_$cat"
                    )
                }
            }
        }

        // Input card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "ইনপুট মান ও একক" else "From Value & Unit",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = inputValue,
                        onValueChange = { inputValue = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    UnitSelector(
                        selectedUnit = fromUnit,
                        units = unitsMap[selectedCategory] ?: emptyList(),
                        onSelect = { fromUnit = it }
                    )
                }
            }
        }

        // Swap button
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
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .testTag("swap_units_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ChangeCircle,
                        contentDescription = "Swap",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Result card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "রূপান্তরিত ফলাফল" else "Converted Output",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = convert(),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    UnitSelector(
                        selectedUnit = toUnit,
                        units = unitsMap[selectedCategory] ?: emptyList(),
                        onSelect = { toUnit = it }
                    )
                }
            }
        }
    }
}

@Composable
private fun UnitSelector(
    selectedUnit: String,
    units: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .clickable { expanded = true },
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(selectedUnit, fontWeight = FontWeight.SemiBold)
                Text("▼", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            units.forEach { unit ->
                DropdownMenuItem(
                    text = { Text(unit) },
                    onClick = {
                        onSelect(unit)
                        expanded = false
                    }
                )
            }
        }
    }
}

// ---------------- DATE CALCULATOR ----------------
@Composable
fun DateCalculatorScreen(
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    var daysToAdd by remember { mutableIntStateOf(30) }
    val now = Calendar.getInstance()

    val targetDate = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, daysToAdd)
    }

    val dayDiff = 120
    val diffMonths = dayDiff / 30
    val diffRemainingDays = dayDiff % 30

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("date_calculator_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "আজকের তারিখ থেকে দিন যোগ/বিয়োগ" else "Add/Subtract Days from Today",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedStateButton(
                            text = "- 7 দিন",
                            onClick = { daysToAdd -= 7 }
                        )
                        OutlinedStateButton(
                            text = "+ 30 দিন",
                            onClick = { daysToAdd += 30 }
                        )
                        OutlinedStateButton(
                            text = "+ 90 দিন",
                            onClick = { daysToAdd += 90 }
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = if (language == AppLanguage.BANGLA)
                            "ফলাফল (${daysToAdd} দিন পর): ${targetDate.get(Calendar.DAY_OF_MONTH)}/${targetDate.get(Calendar.MONTH) + 1}/${targetDate.get(Calendar.YEAR)}"
                        else
                            "Calculated (${daysToAdd} days offset): ${targetDate.get(Calendar.DAY_OF_MONTH)}/${targetDate.get(Calendar.MONTH) + 1}/${targetDate.get(Calendar.YEAR)}",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

// ---------------- STOPWATCH ----------------
@Composable
fun StopwatchScreen(
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    var isRunning by remember { mutableStateOf(false) }
    var elapsedMillis by remember { mutableLongStateOf(0L) }
    val laps = remember { mutableStateListOf<Long>() }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(10)
            elapsedMillis += 10
        }
    }

    val minutes = (elapsedMillis / 60000) % 60
    val seconds = (elapsedMillis / 1000) % 60
    val millis = (elapsedMillis % 1000) / 10
    val timeFormatted = String.format(Locale.US, "%02d:%02d.%02d", minutes, seconds, millis)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("stopwatch_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Time display
        Text(
            text = timeFormatted,
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.Black,
                fontSize = 54.sp,
                letterSpacing = 2.sp,
                color = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            // Reset
            IconButton(
                onClick = {
                    isRunning = false
                    elapsedMillis = 0L
                    laps.clear()
                },
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .testTag("stopwatch_reset_button")
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset")
            }

            Spacer(modifier = Modifier.width(20.dp))

            // Play / Pause
            IconButton(
                onClick = { isRunning = !isRunning },
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(if (isRunning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
                    .testTag("stopwatch_toggle_button")
            ) {
                Icon(
                    imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isRunning) "Pause" else "Start",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            // Lap
            IconButton(
                onClick = {
                    if (isRunning) laps.add(0, elapsedMillis)
                },
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .testTag("stopwatch_lap_button")
            ) {
                Icon(imageVector = Icons.Default.Flag, contentDescription = "Lap")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Lap Times List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(laps) { lapTime ->
                val lapMin = (lapTime / 60000) % 60
                val lapSec = (lapTime / 1000) % 60
                val lapMil = (lapTime % 1000) / 10
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Lap", fontWeight = FontWeight.SemiBold)
                        Text(
                            String.format(Locale.US, "%02d:%02d.%02d", lapMin, lapSec, lapMil),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

// ---------------- RANDOM GENERATOR ----------------
@Composable
fun RandomGeneratorScreen(
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    var minVal by remember { mutableStateOf("1") }
    var maxVal by remember { mutableStateOf("100") }
    var randomNumberResult by remember { mutableStateOf<Int?>(null) }

    var coinResult by remember { mutableStateOf<String?>(null) }
    var diceResult by remember { mutableStateOf<Int?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("random_generator_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Number range randomizer
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "সংখ্যা রেঞ্জ রেন্ডমাইজার" else "Random Number in Range",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = minVal,
                            onValueChange = { minVal = it },
                            label = { Text("Min") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = maxVal,
                            onValueChange = { maxVal = it },
                            label = { Text("Max") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedStateButton(
                        text = if (language == AppLanguage.BANGLA) "সংখ্যা তৈরি করুন" else "Generate Number",
                        onClick = {
                            val min = minVal.toIntOrNull() ?: 1
                            val max = maxVal.toIntOrNull() ?: 100
                            randomNumberResult = if (max >= min) Random.nextInt(min, max + 1) else min
                        },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "gen_random_number"
                    )
                    if (randomNumberResult != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "${randomNumberResult!!}",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }

        // Coin Flip & Dice Roll
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Coin flip
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (language == AppLanguage.BANGLA) "কয়েন ফ্লিপ" else "Coin Flip",
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = coinResult ?: "?",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedStateButton(
                            text = if (language == AppLanguage.BANGLA) "ফ্লিপ" else "Flip",
                            onClick = {
                                coinResult = if (Random.nextBoolean()) "HEADS" else "TAILS"
                            },
                            testTag = "coin_flip_button"
                        )
                    }
                }

                // Dice Roll
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (language == AppLanguage.BANGLA) "ছক্কা রোল" else "Roll Dice",
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (diceResult != null) "🎲 $diceResult" else "🎲 ?",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedStateButton(
                            text = if (language == AppLanguage.BANGLA) "রোল" else "Roll",
                            onClick = {
                                diceResult = Random.nextInt(1, 7)
                            },
                            testTag = "dice_roll_button"
                        )
                    }
                }
            }
        }
    }
}
