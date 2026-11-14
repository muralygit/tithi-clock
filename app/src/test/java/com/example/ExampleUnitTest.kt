package com.example

import com.example.domain.Paksha
import com.example.domain.TithiAstronomyEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun verifyAll30TithisSequenceAndSpellings() {
        val tithis = TithiAstronomyEngine.all30Tithis
        assertEquals(30, tithis.size)

        // Verify Shukla Paksha first 4 Tithis match requested spellings
        assertEquals("Pradhama", tithis[0].primaryName)
        assertEquals("Dvitheeya", tithis[1].primaryName)
        assertEquals("Thritheeya", tithis[2].primaryName)
        assertEquals("Chathurthi", tithis[3].primaryName)
        assertEquals("Pournami", tithis[14].primaryName)
        assertEquals(Paksha.SHUKLA, tithis[14].paksha)

        // Verify Krishna Paksha Tithis
        assertEquals("Pradhama", tithis[15].primaryName)
        assertEquals(Paksha.KRISHNA, tithis[15].paksha)
        assertEquals("Amavasya", tithis[29].primaryName)
    }

    @Test
    fun verifyLiveTithiCalculationAndStartEndTimes() {
        val now = 1794626400000L // Sample epoch
        val state = TithiAstronomyEngine.calculateLiveTithiState(now)
        assertTrue(state.elongationDeg in 0.0..360.0)
        assertTrue(state.illuminationPercent in 0.0..100.0)
        assertTrue(state.tithiProgressFraction in 0f..1f)
        assertTrue(state.tithiStartTimeMillis <= now)
        assertTrue(state.tithiEndTimeMillis >= now)
        // A Tithi is between ~19 and ~27 hours long
        val durationHours = (state.tithiEndTimeMillis - state.tithiStartTimeMillis) / 3600_000.0
        assertTrue("Expected Tithi duration around 20-27h, got $durationHours", durationHours in 18.0..28.0)
    }
}
