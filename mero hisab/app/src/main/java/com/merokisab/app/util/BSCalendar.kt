package com.merokisab.app.util

/**
 * Bikram Sambat ↔ Gregorian converter.
 *
 * Uses the reference date BS 2064/1/1 ≈ AD 2007/04/14.
 * For V1 accuracy: works well for dates in the range BS 2060-2090.
 *
 * BS year ≈ AD year + 57 (offset varies slightly by month).
 * A BS year is leap if (year % 4 == 0).
 */

private val bsMonthDays = intArrayOf(30, 31, 31, 31, 31, 30, 29, 30, 30, 30, 30, 30)
private val bsLeapMonthDays = intArrayOf(30, 31, 32, 31, 31, 30, 29, 30, 30, 30, 30, 30)

/** BS 2064/1/1 = AD 2007/04/14 */
private const val BS_REF_YEAR = 2064
private const val AD_REF_YEAR = 2007
private const val AD_REF_MONTH = 4  // April
private const val AD_REF_DAY = 14

/** BS year is leap if divisible by 4 */
private fun isBsLeap(year: Int) = year % 4 == 0

private fun bsMonthLength(year: Int, month: Int): Int =
    if (isBsLeap(year)) bsLeapMonthDays[month - 1] else bsMonthDays[month - 1]

private fun adMonthLength(month: Int, year: Int): Int = when (month) {
    1, 3, 5, 7, 8, 10, 12 -> 31
    4, 6, 9, 11 -> 30
    2 -> if (isAdLeap(year)) 29 else 28
    else -> 30
}

private fun isAdLeap(year: Int) = (year % 4 == 0 && year % 100 != 0) || year % 400 == 0

/** Days from BS epoch (start of BS year) up to given month/day */
private fun bsDayOfYear(year: Int, month: Int, day: Int): Int {
    var d = day - 1
    for (m in 1 until month) d += bsMonthLength(year, m)
    return d
}

/** Total days from BS_REF to given BS date (positive if after ref) */
private fun daysFromBsRef(year: Int, month: Int, day: Int): Long {
    var days = 0L
    // Years from ref to target
    if (year >= BS_REF_YEAR) {
        for (y in BS_REF_YEAR until year) {
            days += if (isBsLeap(y)) 384L else 383L  // 354 + leap month
        }
        days += bsDayOfYear(year, month, day).toLong()
    } else {
        for (y in year until BS_REF_YEAR) {
            days -= if (isBsLeap(y)) 384L else 383L
        }
        days -= (bsDayOfYear(BS_REF_YEAR, AD_REF_MONTH, AD_REF_DAY) - 1).toLong()
        days += bsDayOfYear(year, month, day).toLong()
    }
    return days
}

/** Total AD days from AD_REF date to given AD date */
private fun adDaysFromRef(year: Int, month: Int, day: Int): Long {
    // Days from AD_REF (2007/04/14) to (year/month/day)
    var total = 0L

    // Days from AD_REF to end of AD_REF year
    for (m in AD_REF_MONTH..12) {
        total += adMonthLength(m, AD_REF_YEAR)
    }
    total -= AD_REF_DAY
    total += day

    // Full years between
    if (year > AD_REF_YEAR) {
        for (y in (AD_REF_YEAR + 1) until year) {
            total += if (isAdLeap(y)) 366 else 365
        }
    } else if (year < AD_REF_YEAR) {
        for (y in year until AD_REF_YEAR) {
            total -= if (isAdLeap(y)) 366 else 365
        }
    }

    return total
}

fun bsToAd(bsYear: Int, bsMonth: Int, bsDay: Int): Triple<Int, Int, Int> {
    // Total BS days from BS 1/1/1 (epoch)
    val daysSinceBsEpoch = daysFromBsRef(bsYear, bsMonth, bsDay)

    // BS 2064/1/1 = AD 2007/04/14, so days from BS epoch = days from AD epoch + offset
    // AD days since epoch from reference
    val refAdDays = adDaysFromRef(AD_REF_YEAR, AD_REF_MONTH, AD_REF_DAY)

    // Target AD days since epoch
    val targetAdDays = daysSinceBsEpoch + refAdDays

    // Convert AD days since epoch back to date
    return adFromDays(AD_REF_YEAR, AD_REF_MONTH, AD_REF_DAY, targetAdDays - refAdDays)
}

fun adToBs(adYear: Int, adMonth: Int, adDay: Int): Triple<Int, Int, Int> {
    val refAdDays = adDaysFromRef(AD_REF_YEAR, AD_REF_MONTH, AD_REF_DAY)
    val targetAdDays = adDaysFromRef(adYear, adMonth, adDay)
    val offset = targetAdDays - refAdDays
    return adFromDays(AD_REF_YEAR, AD_REF_MONTH, AD_REF_DAY, refAdDays + offset - refAdDays)
}

/** Convert AD date by adding a day offset to a reference AD date */
private fun adFromDays(refYear: Int, refMonth: Int, refDay: Int, dayOffset: Long): Triple<Int, Int, Int> {
    var year = refYear
    var month = refMonth
    var day = refDay.toLong() + dayOffset

    // Handle negative days (before reference)
    if (day < 1) {
        while (day < 1) {
            month--
            if (month < 1) {
                month = 12
                year--
            }
            day += adMonthLength(month, year)
        }
        return Triple(year, month, day.toInt())
    }

    // Move forward
    var daysInMonth = adMonthLength(month, year)
    while (day > daysInMonth) {
        day -= daysInMonth
        month++
        if (month > 12) {
            month = 1
            year++
        }
        daysInMonth = adMonthLength(month, year)
    }
    return Triple(year, month, day.toInt())
}

fun formatBs(y: Int, m: Int, d: Int) = "$y/${m.toString().padStart(2, '0')}/${d.toString().padStart(2, '0')}"
fun formatAd(y: Int, m: Int, d: Int) = "$y/${m.toString().padStart(2, '0')}/${d.toString().padStart(2, '0')}"
fun bsMonthName(m: Int) = when (m) {
    1 -> "Baishakh"
    2 -> "Jestha"
    3 -> "Ashadh"
    4 -> "Shrawan"
    5 -> "Bhadra"
    6 -> "Ashwin"
    7 -> "Kartik"
    8 -> "Mangsir"
    9 -> "Poush"
    10 -> "Magh"
    11 -> "Falgun"
    12 -> "Chaitra"
    else -> "Unknown"
}

/** Overload: parse "YYYY/MM/DD" string and re-format with zero-padding */
fun formatAd(date: String): String {
    val p = date.split("/").map { it.toInt() }
    return formatAd(p[0], p[1], p[2])
}

fun formatBs(date: String): String {
    val p = date.split("/").map { it.toInt() }
    return formatBs(p[0], p[1], p[2])
}

fun parseBsDate(s: String): Triple<Int, Int, Int> {
    val p = s.split("/").map { it.toInt() }
    return Triple(p[0], p[1], p[2])
}

fun todayBs(): Triple<Int, Int, Int> {
    val now = java.util.Calendar.getInstance()
    return adToBs(now.get(java.util.Calendar.YEAR), now.get(java.util.Calendar.MONTH) + 1, now.get(java.util.Calendar.DAY_OF_MONTH))
}

fun todayAd(): Triple<Int, Int, Int> {
    val now = java.util.Calendar.getInstance()
    return Triple(now.get(java.util.Calendar.YEAR), now.get(java.util.Calendar.MONTH) + 1, now.get(java.util.Calendar.DAY_OF_MONTH))
}
