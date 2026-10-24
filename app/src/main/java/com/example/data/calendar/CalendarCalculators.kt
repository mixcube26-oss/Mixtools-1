package com.example.data.calendar

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Calendar

data class BanglaDate(
    val day: Int,
    val monthNameEn: String,
    val monthNameBn: String,
    val year: Int,
    val seasonEn: String,
    val seasonBn: String
) {
    fun formatEn(): String = "$day $monthNameEn, $year বঙ্গাব্দ (Bangabda)"
    fun formatBn(): String = "$day $monthNameBn, $year বঙ্গাব্দ"
}

data class HijriDate(
    val day: Int,
    val monthNameEn: String,
    val monthNameBn: String,
    val year: Int
) {
    fun formatEn(): String = "$day $monthNameEn, $year AH"
    fun formatBn(): String = "$day $monthNameBn, $year হিজরি"
}

data class AgeCalculationResult(
    val birthDate: LocalDate,
    val currentDate: LocalDate,
    val years: Int,
    val months: Int,
    val days: Int,
    val totalDays: Long,
    val totalWeeks: Long,
    val totalMonths: Long,
    val totalHours: Long,
    val nextBirthdayDaysRemaining: Long,
    val nextBirthdayDate: LocalDate,
    val dayOfWeekEn: String,
    val dayOfWeekBn: String,
    val birthBanglaDate: BanglaDate,
    val birthHijriDate: HijriDate,
    val currentBanglaDate: BanglaDate,
    val currentHijriDate: HijriDate
)

object CalendarCalculators {

    private val banglaMonths = listOf(
        Pair("Boishakh", "বৈশাখ"),
        Pair("Jyoishtho", "জ্যৈষ্ঠ"),
        Pair("Asharh", "আষাঢ়"),
        Pair("Shrabon", "শ্রাবণ"),
        Pair("Bhadro", "ভাদ্র"),
        Pair("Ashwin", "আশ্বিন"),
        Pair("Kartik", "কার্তিক"),
        Pair("Ogrohayon", "অগ্রহায়ণ"),
        Pair("Poush", "পৌষ"),
        Pair("Magh", "মাঘ"),
        Pair("Falgun", "ফাল্গুন"),
        Pair("Choitro", "চৈত্র")
    )

    private val banglaSeasons = listOf(
        Pair("Summer (Greeshma)", "গ্রীষ্ম"),
        Pair("Monsoon (Borsha)", "বর্ষা"),
        Pair("Autumn (Sarat)", "শরৎ"),
        Pair("Late Autumn (Hemanta)", "হেমন্ত"),
        Pair("Winter (Sheet)", "শীত"),
        Pair("Spring (Basanta)", "বসন্ত")
    )

    private val hijriMonths = listOf(
        Pair("Muharram", "মুহররম"),
        Pair("Safar", "সফর"),
        Pair("Rabi' al-Awwal", "রবিউল আউয়াল"),
        Pair("Rabi' al-Thani", "রবিউস সানি"),
        Pair("Jumada al-Ula", "জমাদিউল আউয়াল"),
        Pair("Jumada al-Akhirah", "জমাদিউস সানি"),
        Pair("Rajab", "রজব"),
        Pair("Sha'ban", "শাবান"),
        Pair("Ramadan", "রমজান"),
        Pair("Shawwal", "শাওয়াল"),
        Pair("Dhu al-Qi'dah", "জিলকদ"),
        Pair("Dhu al-Hijjah", "জিলহজ্জ")
    )

    /**
     * Converts Gregorian date to official Bangladesh Bengali Academy calendar
     */
    fun toBanglaDate(date: LocalDate): BanglaDate {
        val gYear = date.year
        val april14 = LocalDate.of(gYear, 4, 14)
        val bangabdaYear = if (date.isBefore(april14)) gYear - 594 else gYear - 593

        // Falgun falls in February/March. Check the leap year of the Gregorian year in which Falgun occurs.
        val falgunYear = if (date.isBefore(april14)) gYear else gYear + 1
        val isLeap = LocalDate.of(falgunYear, 1, 1).isLeapYear

        // Month lengths in Bangladesh Revised Calendar:
        // First 6 months: 31 days (Boishakh to Ashwin)
        // Kartik to Magh: 30 days (4 months)
        // Falgun: 29 days (30 in leap year)
        // Choitro: 30 days
        val monthLengths = intArrayOf(
            31, 31, 31, 31, 31, 31, // Boishakh to Ashwin
            30, 30, 30, 30,         // Kartik to Magh
            if (isLeap) 30 else 29, // Falgun
            30                      // Choitro
        )

        // Count days since Boishakh 1
        val baseBoishakh1 = if (date.isBefore(april14)) {
            LocalDate.of(gYear - 1, 4, 14)
        } else {
            april14
        }

        var dayOffset = ChronoUnit.DAYS.between(baseBoishakh1, date).toInt()
        var mIdx = 0
        while (mIdx < 12 && dayOffset >= monthLengths[mIdx]) {
            dayOffset -= monthLengths[mIdx]
            mIdx++
        }

        val bDay = dayOffset + 1
        val safeMonthIdx = mIdx.coerceIn(0, 11)
        val month = banglaMonths[safeMonthIdx]
        val season = banglaSeasons[(safeMonthIdx / 2) % 6]

        return BanglaDate(
            day = bDay,
            monthNameEn = month.first,
            monthNameBn = month.second,
            year = bangabdaYear,
            seasonEn = season.first,
            seasonBn = season.second
        )
    }

    /**
     * Astronomical algorithmic conversion to Hijri Calendar (Kuwaiti / Umm al-Qura standard)
     */
    fun toHijriDate(date: LocalDate): HijriDate {
        // Julian day number calculation
        val y = date.year
        val m = date.monthValue
        val d = date.dayOfMonth

        val a = (14 - m) / 12
        val yr = y + 4800 - a
        val mo = m + 12 * a - 3
        val jdn = d + (153 * mo + 2) / 5 + 365 * yr + yr / 4 - yr / 100 + yr / 400 - 32045

        // Epoch of Islamic Calendar: JDN 1948440
        val l = jdn - 1948440 + 10632
        val n = (l - 1) / 10631
        val l1 = l - 10631 * n + 354
        val j = ((10985 - l1) / 5316) * ((50 * l1) / 17719) + (l1 / 5670) * ((43 * l1) / 15238)
        val l2 = l1 - ((30 - j) / 15) * ((17719 * j) / 50) - (j / 16) * ((15238 * j) / 43) + 29
        val monthIdx = ((24 * l2) / 709).coerceIn(1, 12)
        val day = (l2 - (709 * monthIdx) / 24).coerceIn(1, 30)
        val year = 30 * n + j - 30

        val hMonth = hijriMonths[monthIdx - 1]
        return HijriDate(
            day = day,
            monthNameEn = hMonth.first,
            monthNameBn = hMonth.second,
            year = year
        )
    }

    fun calculateAge(birthDate: LocalDate, today: LocalDate = LocalDate.now()): AgeCalculationResult {
        var birth = birthDate
        if (birth.isAfter(today)) {
            birth = today
        }

        var temp = birth
        var y = 0
        while (temp.plusYears(1).isBefore(today) || temp.plusYears(1).isEqual(today)) {
            temp = temp.plusYears(1)
            y++
        }

        var m = 0
        while (temp.plusMonths(1).isBefore(today) || temp.plusMonths(1).isEqual(today)) {
            temp = temp.plusMonths(1)
            m++
        }

        val d = ChronoUnit.DAYS.between(temp, today).toInt()

        val totalDays = ChronoUnit.DAYS.between(birth, today)
        val totalWeeks = totalDays / 7
        val totalMonths = y * 12L + m
        val totalHours = totalDays * 24

        // Next Birthday (with safe leap-year handling for Feb 29 births)
        val nextBday = run {
            if (birth.monthValue == 2 && birth.dayOfMonth == 29) {
                val candidateYear = if (LocalDate.of(today.year, 2, 28).isBefore(today)) today.year + 1 else today.year
                if (LocalDate.of(candidateYear, 1, 1).isLeapYear) {
                    LocalDate.of(candidateYear, 2, 29)
                } else {
                    LocalDate.of(candidateYear, 2, 28)
                }
            } else {
                var candidate = birth.withYear(today.year)
                if (candidate.isBefore(today)) {
                    candidate = birth.withYear(today.year + 1)
                }
                candidate
            }
        }
        val nextBdayDays = ChronoUnit.DAYS.between(today, nextBday)

        val dayOfWeekEn = birth.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }
        val dayOfWeekBn = when (birth.dayOfWeek.name) {
            "MONDAY" -> "সোমবার"
            "TUESDAY" -> "মঙ্গলবার"
            "WEDNESDAY" -> "বুধবার"
            "THURSDAY" -> "বৃহস্পতিবার"
            "FRIDAY" -> "শুক্রবার"
            "SATURDAY" -> "শনিবার"
            "SUNDAY" -> "রবিবার"
            else -> "রবিবার"
        }

        return AgeCalculationResult(
            birthDate = birth,
            currentDate = today,
            years = y,
            months = m,
            days = d,
            totalDays = totalDays,
            totalWeeks = totalWeeks,
            totalMonths = totalMonths,
            totalHours = totalHours,
            nextBirthdayDaysRemaining = nextBdayDays,
            nextBirthdayDate = nextBday,
            dayOfWeekEn = dayOfWeekEn,
            dayOfWeekBn = dayOfWeekBn,
            birthBanglaDate = toBanglaDate(birth),
            birthHijriDate = toHijriDate(birth),
            currentBanglaDate = toBanglaDate(today),
            currentHijriDate = toHijriDate(today)
        )
    }
}
