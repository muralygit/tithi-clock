package com.example.domain

enum class Paksha(
    val displayName: String,
    val sanskritName: String,
    val shortName: String,
    val description: String
) {
    SHUKLA(
        displayName = "Shukla Paksha",
        sanskritName = "शुक्ल पक्ष",
        shortName = "Shukla",
        description = "Bright Lunar Fortnight • Waxing Moon (0° – 180°)"
    ),
    KRISHNA(
        displayName = "Krishna Paksha",
        sanskritName = "कृष्ण पक्ष",
        shortName = "Krishna",
        description = "Dark Lunar Fortnight • Waning Moon (180° – 360°)"
    )
}

/**
 * Represents one of the 30 Vedic Tithis (Lunar Days) in a synodic month.
 * Includes the traditional South/Pan-Indian spellings requested by the user
 * (Pradhama, Dvitheeya, Thritheeya, Chathurthi, etc.) as well as Sanskrit script.
 */
data class TithiDefinition(
    val number: Int, // 1..30 (1..15 Shukla, 16..30 Krishna)
    val paksha: Paksha,
    val pakshaDayNumber: Int, // 1..15 within the Paksha
    val primaryName: String, // e.g., "Pradhama", "Dvitheeya", "Thritheeya", "Chathurthi"
    val alternateSpelling: String, // e.g., "Prathama", "Dwitiya", "Tritiya", "Chaturthi"
    val sanskritScript: String, // e.g., "प्रथमा"
    val startDegree: Double, // (number - 1) * 12.0
    val endDegree: Double, // number * 12.0
    val categoryName: String, // Nanda, Bhadra, Jaya, Rikta, Purna
    val presidingDeity: String,
    val moonPhaseLabel: String,
    val significance: String,
    val isSpecialObservance: Boolean = false
) {
    val fullTitle: String
        get() = when (number) {
            15 -> "Pournami (Full Moon)"
            30 -> "Amavasya (New Moon)"
            else -> "${paksha.shortName} $primaryName"
        }

    val degreeRangeText: String
        get() = "${startDegree.toInt()}° – ${endDegree.toInt()}°"

    val midpointElongation: Double
        get() = startDegree + 6.0
}

/**
 * Complete real-time astronomical & Panchang snapshot for any moment in time.
 */
data class LiveTithiState(
    val epochMillis: Long,
    val formattedTime12h: String,
    val formattedSeconds: String,
    val amPm: String,
    val formattedTime24h: String,
    val formattedDate: String,
    val varaName: String, // Vedic weekday name e.g. Shukravara (Friday)
    val currentTithi: TithiDefinition,
    val nextTithi: TithiDefinition,
    val sunLongitudeDeg: Double,
    val moonLongitudeDeg: Double,
    val elongationDeg: Double, // 0.0 .. 360.0 (Moon - Sun)
    val tithiProgressFraction: Float, // 0.0 .. 1.0 within current 12° Tithi
    val illuminationPercent: Double, // 0.0 .. 100.0
    val isWaxing: Boolean,
    val tithiStartTimeMillis: Long,
    val tithiEndTimeMillis: Long,
    val formattedStartTime: String,
    val formattedEndTime: String,
    val remainingDurationText: String,
    val nakshatraName: String,
    val nakshatraPada: Int,
    val karanaName: String,
    val yogaName: String,
    val lunarAgeDays: Double
)

data class DailyTithiForecast(
    val dateMillis: Long,
    val formattedDateShort: String,
    val dayOfWeekShort: String,
    val sunriseTithi: TithiDefinition,
    val endTimeFormatted: String,
    val illuminationPercent: Int,
    val elongationDeg: Double
)
