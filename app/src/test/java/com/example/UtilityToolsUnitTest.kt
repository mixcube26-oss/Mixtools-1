package com.example

import com.example.data.calendar.CalendarCalculators
import com.example.data.currency.SupportedCurrencies
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import kotlin.math.abs

class UtilityToolsUnitTest {

    @Test
    fun testAgeCalculation_Standard() {
        val birth = LocalDate.of(2000, 1, 1)
        val today = LocalDate.of(2025, 1, 1)
        val result = CalendarCalculators.calculateAge(birth, today)

        assertEquals(25, result.years)
        assertEquals(0, result.months)
        assertEquals(0, result.days)
        assertTrue(result.totalDays > 9000)
        assertEquals("Saturday", result.dayOfWeekEn)
        assertEquals("শনিবার", result.dayOfWeekBn)
    }

    @Test
    fun testAgeCalculation_LeapYearBirth() {
        // Born on Feb 29, 2000 (leap year)
        val birth = LocalDate.of(2000, 2, 29)
        val today = LocalDate.of(2025, 3, 1)
        val result = CalendarCalculators.calculateAge(birth, today)

        assertEquals(25, result.years)
        assertEquals(0, result.months)
        assertTrue(result.days in 0..1)
        assertNotNull(result.nextBirthdayDate)
    }

    @Test
    fun testBanglaCalendarCalculation() {
        // April 14 is Pohela Boishakh (1st Boishakh)
        val pBoishakh = LocalDate.of(2024, 4, 14)
        val banglaDate = CalendarCalculators.toBanglaDate(pBoishakh)

        assertEquals(1, banglaDate.day)
        assertEquals("Boishakh", banglaDate.monthNameEn)
        assertEquals(1431, banglaDate.year)
        assertEquals("Summer (Greeshma)", banglaDate.seasonEn)
    }

    @Test
    fun testHijriCalendarCalculation() {
        val date = LocalDate.of(2024, 4, 10) // Approx 1 Shawwal 1445 (Eid)
        val hijriDate = CalendarCalculators.toHijriDate(date)

        assertEquals(1445, hijriDate.year)
        assertNotNull(hijriDate.monthNameEn)
        assertTrue(hijriDate.day in 1..30)
    }

    @Test
    fun testPercentageCalculation_PercentOf() {
        val x = 15.0
        val y = 200.0
        val result = (x / 100.0) * y
        assertEquals(30.0, result, 0.0001)
    }

    @Test
    fun testPercentageCalculation_IsWhatPercent() {
        val x = 25.0
        val y = 200.0
        val result = (x / y) * 100.0
        assertEquals(12.5, result, 0.0001)
    }

    @Test
    fun testPercentageCalculation_IncreaseDecrease() {
        val initial = 100.0
        val finalVal = 135.0
        val increase = ((finalVal - initial) / initial) * 100.0
        assertEquals(35.0, increase, 0.0001)

        val decreaseVal = 80.0
        val decrease = ((initial - decreaseVal) / initial) * 100.0
        assertEquals(20.0, decrease, 0.0001)
    }

    @Test
    fun testPercentageCalculation_Difference() {
        val v1 = 50.0
        val v2 = 70.0
        val average = (abs(v1) + abs(v2)) / 2.0
        val pctDiff = (abs(v1 - v2) / average) * 100.0
        assertEquals(33.3333, pctDiff, 0.001)
    }

    @Test
    fun testPercentageCalculation_Discount() {
        val originalPrice = 250.0
        val discountRate = 20.0
        val saved = originalPrice * (discountRate / 100.0)
        val finalPrice = originalPrice - saved
        assertEquals(50.0, saved, 0.0001)
        assertEquals(200.0, finalPrice, 0.0001)
    }

    @Test
    fun testSupportedCurrencies() {
        val bdt = SupportedCurrencies.get("BDT")
        assertEquals("BDT", bdt.code)
        assertEquals("৳", bdt.symbol)

        val usd = SupportedCurrencies.get("USD")
        assertEquals("USD", usd.code)
        assertEquals("$", usd.symbol)

        assertTrue(SupportedCurrencies.list.size >= 15)
    }

    @Test
    fun testWriterPageDimensions() {
        val a4 = com.example.ui.tools.writer.WriterPageSize.A4
        assertEquals(210f, a4.widthMm)
        assertEquals(297f, a4.heightMm)

        val letter = com.example.ui.tools.writer.WriterPageSize.LETTER
        assertEquals(215.9f, letter.widthMm)
        assertEquals(279.4f, letter.heightMm)

        val legal = com.example.ui.tools.writer.WriterPageSize.LEGAL
        assertEquals(215.9f, legal.widthMm)
        assertEquals(355.6f, legal.heightMm)
    }

    @Test
    fun testWriterTemplates() {
        val templatesEn = com.example.ui.tools.writer.WriterTemplateCatalog.getTemplates(com.example.model.AppLanguage.ENGLISH)
        assertTrue(templatesEn.size >= 4)
        assertTrue(templatesEn.any { it.titleEn == "Blank Document" })
        assertTrue(templatesEn.any { it.titleEn == "Official Application" })

        val templatesBn = com.example.ui.tools.writer.WriterTemplateCatalog.getTemplates(com.example.model.AppLanguage.BANGLA)
        assertTrue(templatesBn.any { it.titleBn == "খালি নথি" })
        assertTrue(templatesBn.any { it.titleBn == "অফিসিয়াল দরখাস্ত" })
    }
}
