package com.example

import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testArabicNumberNormalization() {
        val arabicNumbers = "٥٢٧١٤٩"
        val arabicToLatinMap = mapOf(
            '٠' to '0', '١' to '1', '٢' to '2', '٣' to '3', '٤' to '4',
            '٥' to '5', '٦' to '6', '٧' to '7', '٨' to '8', '٩' to '9'
        )
        val normalized = arabicNumbers.map { arabicToLatinMap[it] ?: it }.joinToString("")
        assertEquals("527149", normalized)
    }

    @Test
    fun testZakatCalculation() {
        // Gold price 300, 85g nisab = 25,500
        val nisab = 85.0 * 300.0
        val wealth = 30000.0
        val isNisabReached = wealth >= nisab
        val zakatDueHijri = if (isNisabReached) wealth * 0.025 else 0.0
        val zakatDueGregorian = if (isNisabReached) wealth * 0.02577 else 0.0
        assertTrue(isNisabReached)
        assertEquals(750.0, zakatDueHijri, 0.01)
        assertEquals(773.1, zakatDueGregorian, 0.01)
    }
}
