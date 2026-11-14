package com.example.domain

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * High-precision Astronomical Vedic Tithi & Panchang Engine.
 *
 * Computes the apparent geocentric ecliptic longitudes of the Sun (Surya) and Moon (Chandra)
 * using Jean Meeus ("Astronomical Algorithms") harmonic series terms.
 *
 * A Tithi is the time taken for the longitudinal angle between the Moon and the Sun
 * (Elongation Δλ = (λ_Moon - λ_Sun) mod 360°) to increase by 12°.
 * - 0° to 180° -> Shukla Paksha (15 Waxing Tithis: Pradhama to Pournami)
 * - 180° to 360° -> Krishna Paksha (15 Waning Tithis: Pradhama to Amavasya)
 */
object TithiAstronomyEngine {

    private const val SYNODIC_MONTH_DAYS = 29.530588853

    private val nakshatraNames = listOf(
        "Ashwini", "Bharani", "Krittika", "Rohini", "Mrigashirsha", "Ardra",
        "Punarvasu", "Pushya", "Ashlesha", "Magha", "Purva Phalguni", "Uttara Phalguni",
        "Hasta", "Chitra", "Swati", "Vishakha", "Anuradha", "Jyeshtha",
        "Moola", "Purva Ashadha", "Uttara Ashadha", "Shravana", "Dhanishta",
        "Shatabhisha", "Purva Bhadrapada", "Uttara Bhadrapada", "Revati"
    )

    private val yogaNames = listOf(
        "Vishkumbha", "Priti", "Ayushman", "Saubhagya", "Shobhana", "Atiganda",
        "Sukarma", "Dhriti", "Shoola", "Ganda", "Vriddhi", "Dhruva",
        "Vyaghata", "Harshana", "Vajra", "Siddhi", "Vyatipata", "Variyan",
        "Parigha", "Shiva", "Siddha", "Sadhya", "Shubha", "Shukla",
        "Brahma", "Indra", "Vaidhriti"
    )

    private val repeatingKaranas = listOf(
        "Bava", "Balava", "Kaulava", "Taitila", "Garija", "Vanija", "Vishti (Bhadra)"
    )

    val all30Tithis: List<TithiDefinition> = buildAll30Tithis()

    private fun buildAll30Tithis(): List<TithiDefinition> {
        data class BaseTithiSpec(
            val dayInPaksha: Int,
            val primaryName: String,
            val alternateSpelling: String,
            val sanskrit: String,
            val category: String,
            val deity: String,
            val shuklaMeaning: String,
            val krishnaMeaning: String,
            val special: Boolean = false
        )

        val base14 = listOf(
            BaseTithiSpec(
                1, "Pradhama", "Prathama", "प्रथमा", "Nanda (Joyful)", "Agni (Sacred Fire)",
                "First waxing lunar crescent; auspicious for new beginnings, sankalpa, and domestic rituals.",
                "First waning lunar day after Pournami; suited for reflection, planning, and steady discipline.",
                special = true
            ),
            BaseTithiSpec(
                2, "Dvitheeya", "Dwitiya", "द्वितीया", "Bhadra (Auspicious)", "Brahma / Vidhatr",
                "Second waxing day (Chandra Darshan); favorable for laying foundations, learning, and travel.",
                "Second waning lunar day; favorable for architecture, study, and steady household works."
            ),
            BaseTithiSpec(
                3, "Thritheeya", "Tritiya", "तृतीया", "Jaya (Victorious)", "Gauri / Parvati",
                "Third waxing day; highly auspicious for music, arts, buying gold (Akshaya), and harmony.",
                "Third waning day; fosters strength, creativity, and completing long-standing commitments."
            ),
            BaseTithiSpec(
                4, "Chathurthi", "Chaturthi", "चतुर्थी", "Rikta (Purifying)", "Lord Ganesha",
                "Vinayaka Chathurthi; sacred to Lord Ganesha for clearing obstacles and spiritual focus.",
                "Sankashti Chathurthi; revered moonrise observance dedicated to Lord Ganesha for peace.",
                special = true
            ),
            BaseTithiSpec(
                5, "Panchami", "Panchami", "पञ्चमी", "Purna (Fulfilling)", "Nagas (Serpent Deities)",
                "Shukla Panchami; auspicious for knowledge (Saraswati), healing, Medicine, and prosperity.",
                "Krishna Panchami; благоприятная (favorable) for scholarship, protective rites, and family harmony."
            ),
            BaseTithiSpec(
                6, "Shashthi", "Shashti", "षष्ठी", "Nanda (Joyful)", "Kartikeya (Murugan / Skanda)",
                "Skanda Shashthi; celebrated for vitality, courage, victory, and blessing children.",
                "Krishna Shashthi; favorable for craftsmanship, health routines, and resolving disputes.",
                special = true
            ),
            BaseTithiSpec(
                7, "Saptami", "Saptami", "सप्तमी", "Bhadra (Auspicious)", "Surya (Sun God)",
                "Surya Saptami (First Quarter Waxing Moon); radiant energy for travel, vitality, and leadership.",
                "Krishna Saptami; balanced waning half-moon transition suited for journeys and service."
            ),
            BaseTithiSpec(
                8, "Ashtami", "Ashtami", "अष्टमी", "Jaya (Victorious)", "Rudra / Durga",
                "Durga Ashtami (Half Moon); powerful day of Shakti worship, protection, and inner fortitude.",
                "Kala Bhairava / Janmashtami Tithi (Third Quarter Moon); sacred for devotion and fearlessness.",
                special = true
            ),
            BaseTithiSpec(
                9, "Navami", "Navami", "नवमी", "Rikta (Purifying)", "Ambika / Durga",
                "Maha Navami; dynamic spiritual potency, valor, and culmination of devotional sadhana.",
                "Krishna Navami; suited for austerity, meditation, and clearing pending obligations."
            ),
            BaseTithiSpec(
                10, "Dashami", "Dashami", "दशमी", "Purna (Fulfilling)", "Dharma / Yama",
                "Vijaya Dashami Tithi; auspicious for righteous action, career milestones, and noble ventures.",
                "Krishna Dashami; favorable for administrative tasks, ethics, and community leadership."
            ),
            BaseTithiSpec(
                11, "Ekadashi", "Ekadashi", "एकादशी", "Nanda (Joyful)", "Lord Vishnu",
                "Shukla Ekadashi; supreme Vedic fasting and meditation day for spiritual clarity and sattva.",
                "Krishna Ekadashi; deeply sacred fortnightly observance for devotion, detox, and inner peace.",
                special = true
            ),
            BaseTithiSpec(
                12, "Dwadashi", "Dwadashi", "द्वादशी", "Bhadra (Auspicious)", "Vishnu / Hari",
                "Shukla Dwadashi (Parana); auspicious for charity (Dana), Tulsi worship, and nourishment.",
                "Krishna Dwadashi; sacred for annadana, gratitude, and tranquil household ceremonies."
            ),
            BaseTithiSpec(
                13, "Trayodashi", "Thrayodashi", "त्रयोदशी", "Jaya (Victorious)", "Kamadeva / Shiva",
                "Shukla Pradosham; sacred twilight worship of Lord Shiva and Nandi for grace and victory.",
                "Krishna Pradosham / Masa Shivaratri prologue; auspicious evening meditation on Shiva.",
                special = true
            ),
            BaseTithiSpec(
                14, "Chaturdashi", "Chathurdashi", "चतुर्दशी", "Rikta (Purifying)", "Kali / Lord Shiva",
                "Shukla Chaturdashi; intense pre-full-moon spiritual energy, mantra japa, and stillness.",
                "Masa Shivaratri (Krishna Chaturdashi); deeply revered night of Shiva worship and awakening.",
                special = true
            )
        )

        val list = mutableListOf<TithiDefinition>()

        // 1..14 Shukla Paksha
        for (spec in base14) {
            val num = spec.dayInPaksha
            list.add(
                TithiDefinition(
                    number = num,
                    paksha = Paksha.SHUKLA,
                    pakshaDayNumber = num,
                    primaryName = spec.primaryName,
                    alternateSpelling = spec.alternateSpelling,
                    sanskritScript = spec.sanskrit,
                    startDegree = (num - 1) * 12.0,
                    endDegree = num * 12.0,
                    categoryName = spec.category,
                    presidingDeity = spec.deity,
                    moonPhaseLabel = getShuklaPhaseLabel(num),
                    significance = spec.shuklaMeaning,
                    isSpecialObservance = spec.special
                )
            )
        }

        // 15: Pournami (Full Moon)
        list.add(
            TithiDefinition(
                number = 15,
                paksha = Paksha.SHUKLA,
                pakshaDayNumber = 15,
                primaryName = "Pournami",
                alternateSpelling = "Purnima",
                sanskritScript = "पूर्णिमा",
                startDegree = 168.0,
                endDegree = 180.0,
                categoryName = "Purna (Complete)",
                presidingDeity = "Chandra (Moon God)",
                moonPhaseLabel = "Full Moon (Pournami)",
                significance = "Luminous Full Moon culminating Shukla Paksha; auspicious for Satyanarayana Puja, gratitude, and sacred gatherings.",
                isSpecialObservance = true
            )
        )

        // 16..29 Krishna Paksha
        for (spec in base14) {
            val num = 15 + spec.dayInPaksha
            list.add(
                TithiDefinition(
                    number = num,
                    paksha = Paksha.KRISHNA,
                    pakshaDayNumber = spec.dayInPaksha,
                    primaryName = spec.primaryName,
                    alternateSpelling = spec.alternateSpelling,
                    sanskritScript = spec.sanskrit,
                    startDegree = (num - 1) * 12.0,
                    endDegree = num * 12.0,
                    categoryName = spec.category,
                    presidingDeity = spec.deity,
                    moonPhaseLabel = getKrishnaPhaseLabel(spec.dayInPaksha),
                    significance = spec.krishnaMeaning,
                    isSpecialObservance = spec.special
                )
            )
        }

        // 30: Amavasya (New Moon)
        list.add(
            TithiDefinition(
                number = 30,
                paksha = Paksha.KRISHNA,
                pakshaDayNumber = 15,
                primaryName = "Amavasya",
                alternateSpelling = "Amavasai",
                sanskritScript = "अमावस्या",
                startDegree = 348.0,
                endDegree = 360.0,
                categoryName = "Purna (Renewal)",
                presidingDeity = "Pitrs (Ancestors)",
                moonPhaseLabel = "New Moon (Amavasya)",
                significance = "Conjunction of Sun and Moon concluding Krishna Paksha; sacred for honoring ancestors (Tarpanam), silence, and inward renewal.",
                isSpecialObservance = true
            )
        )

        return list
    }

    private fun getShuklaPhaseLabel(day: Int): String = when (day) {
        in 1..6 -> "Waxing Crescent"
        7, 8 -> "First Quarter"
        in 9..14 -> "Waxing Gibbous"
        else -> "Full Moon"
    }

    private fun getKrishnaPhaseLabel(day: Int): String = when (day) {
        in 1..6 -> "Waning Gibbous"
        7, 8 -> "Third Quarter"
        in 9..14 -> "Waning Crescent"
        else -> "New Moon"
    }

    /**
     * Converts Unix epoch milliseconds to Julian Ephemeris Day (JD).
     */
    fun toJulianDay(epochMillis: Long): Double {
        return (epochMillis.toDouble() / 86400000.0) + 2440587.5
    }

    private fun normalizeDegrees(deg: Double): Double {
        var r = deg % 360.0
        if (r < 0.0) r += 360.0
        return r
    }

    private fun degToRad(deg: Double): Double = deg * PI / 180.0

    /**
     * Apparent geocentric ecliptic longitude of the Sun in degrees [0, 360).
     * Accuracy: ~0.01° (well within 1 minute of Tithi time).
     */
    fun calculateSunLongitude(jd: Double): Double {
        val t = (jd - 2451545.0) / 36525.0

        // Geometric mean longitude of the Sun
        val l0 = normalizeDegrees(280.46646 + 36000.76983 * t + 0.0003032 * t * t)
        // Mean anomaly of the Sun
        val m = normalizeDegrees(357.52911 + 35999.05029 * t - 0.0001537 * t * t)
        val mRad = degToRad(m)

        // Equation of center
        val c = (1.914602 - 0.004817 * t - 0.000014 * t * t) * sin(mRad) +
                (0.019993 - 0.000101 * t) * sin(2 * mRad) +
                0.000289 * sin(3 * mRad)

        val trueLong = l0 + c
        val omega = 125.04 - 1934.136 * t
        val apparentLong = trueLong - 0.00569 - 0.00478 * sin(degToRad(omega))

        return normalizeDegrees(apparentLong)
    }

    /**
     * Apparent geocentric ecliptic longitude of the Moon in degrees [0, 360).
     * Uses Jean Meeus Chapter 47 primary periodic terms for high lunar accuracy.
     */
    fun calculateMoonLongitude(jd: Double): Double {
        val t = (jd - 2451545.0) / 36525.0
        val t2 = t * t
        val t3 = t2 * t
        val t4 = t3 * t

        // Moon's mean longitude L'
        val lp = normalizeDegrees(
            218.3164477 + 481267.88123421 * t - 0.0015786 * t2 + t3 / 538841.0 - t4 / 65194000.0
        )
        // Mean elongation of the Moon D
        val d = normalizeDegrees(
            297.8501921 + 445267.1114034 * t - 0.0018819 * t2 + t3 / 545868.0 - t4 / 113065000.0
        )
        // Sun's mean anomaly M
        val m = normalizeDegrees(
            357.5291092 + 35999.0502909 * t - 0.0001536 * t2 + t3 / 24490000.0
        )
        // Moon's mean anomaly M'
        val mp = normalizeDegrees(
            134.9633964 + 477198.8675055 * t + 0.0087414 * t2 + t3 / 69699.0 - t4 / 14712000.0
        )
        // Moon's argument of latitude F
        val f = normalizeDegrees(
            93.2720950 + 483202.0175233 * t - 0.0036539 * t2 - t3 / 3526000.0 + t4 / 863310000.0
        )

        val dRad = degToRad(d)
        val mRad = degToRad(m)
        val mpRad = degToRad(mp)
        val fRad = degToRad(f)

        // Eccentricity correction
        val e = 1.0 - 0.002516 * t - 0.0000074 * t2

        // Principal periodic terms in longitude (degrees)
        var sigmaL = 0.0
        sigmaL += 6.288774 * sin(mpRad)
        sigmaL += 1.274027 * sin(2 * dRad - mpRad)
        sigmaL += 0.658314 * sin(2 * dRad)
        sigmaL += 0.213618 * sin(2 * mpRad)
        sigmaL -= 0.185116 * e * sin(mRad)
        sigmaL -= 0.114332 * sin(2 * fRad)
        sigmaL += 0.058793 * sin(2 * dRad - 2 * mpRad)
        sigmaL += 0.057066 * e * sin(2 * dRad - mRad - mpRad)
        sigmaL += 0.053322 * sin(2 * dRad + mpRad)
        sigmaL += 0.045758 * e * sin(2 * dRad - mRad)
        sigmaL -= 0.040923 * e * sin(mRad - mpRad)
        sigmaL -= 0.034720 * sin(dRad)
        sigmaL -= 0.030383 * e * sin(mRad + mpRad)
        sigmaL += 0.015327 * sin(2 * dRad - 2 * fRad)
        sigmaL -= 0.012528 * sin(mpRad + 2 * fRad)
        sigmaL += 0.010980 * sin(mpRad - 2 * fRad)
        sigmaL += 0.010675 * sin(4 * dRad - mpRad)
        sigmaL += 0.010034 * sin(3 * mpRad)
        sigmaL += 0.008548 * sin(4 * dRad - 2 * mpRad)
        sigmaL -= 0.007888 * e * sin(2 * dRad + mRad - mpRad)
        sigmaL -= 0.006766 * e * sin(2 * dRad + mRad)
        sigmaL -= 0.005163 * sin(dRad - mpRad)
        sigmaL += 0.004987 * e * sin(dRad + mRad)
        sigmaL += 0.004036 * e * sin(2 * dRad - mRad + mpRad)

        return normalizeDegrees(lp + sigmaL)
    }

    /**
     * Sun-Moon longitudinal elongation Δλ = (λ_Moon - λ_Sun) in [0.0, 360.0).
     */
    fun calculateElongationDegrees(epochMillis: Long): Double {
        val jd = toJulianDay(epochMillis)
        val sun = calculateSunLongitude(jd)
        val moon = calculateMoonLongitude(jd)
        return normalizeDegrees(moon - sun)
    }

    /**
     * Lahiri Chitrapaksha Ayanamsha approximation for Sidereal Nakshatra & Yoga.
     */
    private fun calculateLahiriAyanamsha(jd: Double): Double {
        val t = (jd - 2451545.0) / 36525.0
        return 23.853 + 1.396 * t
    }

    /**
     * Finds the exact timestamp when the Sun-Moon elongation crosses targetDeg [0, 360)
     * near referenceMillis using binary search.
     */
    private fun findElongationCrossingTime(
        referenceMillis: Long,
        targetDeg: Double,
        searchForward: Boolean
    ): Long {
        val normalizedTarget = normalizeDegrees(targetDeg)
        // Average rate of elongation is ~12.1907° per day (~23.6 hours per 12° Tithi)
        val currentElong = calculateElongationDegrees(referenceMillis)
        var deltaDeg = if (searchForward) {
            normalizeDegrees(normalizedTarget - currentElong)
        } else {
            normalizeDegrees(currentElong - normalizedTarget)
        }
        if (deltaDeg > 25.0) {
            deltaDeg = 12.0
        }

        val estimatedOffsetMs = ((deltaDeg / 12.190749) * 86400000.0).toLong()
        var low = if (searchForward) {
            referenceMillis
        } else {
            referenceMillis - estimatedOffsetMs - 14 * 3600_000L
        }
        var high = if (searchForward) {
            referenceMillis + estimatedOffsetMs + 14 * 3600_000L
        } else {
            referenceMillis
        }

        // Unwrap angle relative to normalizedTarget so it's monotonic in [low, high]
        fun signedAngleDiff(ms: Long): Double {
            val e = calculateElongationDegrees(ms)
            var diff = (e - normalizedTarget) % 360.0
            if (diff < -180.0) diff += 360.0
            if (diff > 180.0) diff -= 360.0
            return diff
        }

        repeat(22) {
            val mid = (low + high) / 2L
            if (signedAngleDiff(mid) < 0.0) {
                low = mid
            } else {
                high = mid
            }
        }
        return (low + high) / 2L
    }

    /**
     * Computes the full LiveTithiState for the given timestamp and locale timezone.
     */
    fun calculateLiveTithiState(
        epochMillis: Long,
        timeZone: TimeZone = TimeZone.getDefault()
    ): LiveTithiState {
        val jd = toJulianDay(epochMillis)
        val sunLong = calculateSunLongitude(jd)
        val moonLong = calculateMoonLongitude(jd)
        val elongation = normalizeDegrees(moonLong - sunLong)

        // Tithi index: 0..29 (1..30)
        val tithiZeroBased = floor(elongation / 12.0).toInt().coerceIn(0, 29)
        val currentTithi = all30Tithis[tithiZeroBased]
        val nextTithi = all30Tithis[(tithiZeroBased + 1) % 30]

        val progressFraction = ((elongation - currentTithi.startDegree) / 12.0)
            .toFloat()
            .coerceIn(0f, 1f)

        // Illumination percentage: (1 - cos(elongation)) / 2 * 100
        val illumFraction = (1.0 - cos(degToRad(elongation))) / 2.0
        val illuminationPercent = (illumFraction * 1000.0).roundToInt() / 10.0
        val isWaxing = elongation < 180.0

        // Exact Start & End times of current Tithi
        val startMillis = findElongationCrossingTime(
            referenceMillis = epochMillis,
            targetDeg = currentTithi.startDegree,
            searchForward = false
        )
        val endMillis = findElongationCrossingTime(
            referenceMillis = epochMillis,
            targetDeg = currentTithi.endDegree % 360.0,
            searchForward = true
        )

        // Sidereal calculations for Nakshatra & Yoga
        val ayanamsha = calculateLahiriAyanamsha(jd)
        val siderealMoon = normalizeDegrees(moonLong - ayanamsha)
        val siderealSun = normalizeDegrees(sunLong - ayanamsha)

        val nakshatraSpan = 360.0 / 27.0 // 13.3333°
        val nakshatraIndex = floor(siderealMoon / nakshatraSpan).toInt().coerceIn(0, 26)
        val nakshatraName = nakshatraNames[nakshatraIndex]
        val padaSpan = nakshatraSpan / 4.0
        val nakshatraPada = (floor((siderealMoon % nakshatraSpan) / padaSpan).toInt() + 1).coerceIn(1, 4)

        val yogaSum = normalizeDegrees(siderealMoon + siderealSun)
        val yogaIndex = floor(yogaSum / nakshatraSpan).toInt().coerceIn(0, 26)
        val yogaName = yogaNames[yogaIndex]

        // Karana (each Tithi has 2 Karanas = 6° each, 60 Karanas in 360°)
        val karanaIndex = floor(elongation / 6.0).toInt().coerceIn(0, 59)
        val karanaName = when (karanaIndex) {
            0 -> "Kimstughna"
            57 -> "Shakuni"
            58 -> "Chatushpada"
            59 -> "Naga"
            else -> repeatingKaranas[(karanaIndex - 1) % 7]
        }

        // Formatters
        val dateObj = Date(epochMillis)
        val time12Fmt = SimpleDateFormat("hh:mm", Locale.US).apply { this.timeZone = timeZone }
        val secFmt = SimpleDateFormat(":ss", Locale.US).apply { this.timeZone = timeZone }
        val amPmFmt = SimpleDateFormat("a", Locale.US).apply { this.timeZone = timeZone }
        val time24Fmt = SimpleDateFormat("HH:mm:ss", Locale.US).apply { this.timeZone = timeZone }
        val dateFmt = SimpleDateFormat("EEE, dd MMM yyyy", Locale.US).apply { this.timeZone = timeZone }
        val shortDateTimeFmt = SimpleDateFormat("MMM d, hh:mm a", Locale.US).apply { this.timeZone = timeZone }

        val cal = Calendar.getInstance(timeZone).apply { timeInMillis = epochMillis }
        val varaName = when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> "Ravivara (Sun)"
            Calendar.MONDAY -> "Somavara (Mon)"
            Calendar.TUESDAY -> "Mangalavara (Tue)"
            Calendar.WEDNESDAY -> "Budhavara (Wed)"
            Calendar.THURSDAY -> "Guruvara (Thu)"
            Calendar.FRIDAY -> "Shukravara (Fri)"
            Calendar.SATURDAY -> "Shanivara (Sat)"
            else -> "Vara"
        }

        val remainingMs = (endMillis - epochMillis).coerceAtLeast(0L)
        val remHours = remainingMs / 3600_000L
        val remMinutes = (remainingMs % 3600_000L) / 60_000L
        val remSeconds = (remainingMs % 60_000L) / 1000L
        val remainingDurationText = String.format(
            Locale.US,
            "%02dh %02dm %02ds left",
            remHours,
            remMinutes,
            remSeconds
        )

        val lunarAgeDays = ((elongation / 360.0) * SYNODIC_MONTH_DAYS * 10.0).roundToInt() / 10.0

        return LiveTithiState(
            epochMillis = epochMillis,
            formattedTime12h = time12Fmt.format(dateObj),
            formattedSeconds = secFmt.format(dateObj),
            amPm = amPmFmt.format(dateObj),
            formattedTime24h = time24Fmt.format(dateObj),
            formattedDate = dateFmt.format(dateObj),
            varaName = varaName,
            currentTithi = currentTithi,
            nextTithi = nextTithi,
            sunLongitudeDeg = (sunLong * 100.0).roundToInt() / 100.0,
            moonLongitudeDeg = (moonLong * 100.0).roundToInt() / 100.0,
            elongationDeg = (elongation * 100.0).roundToInt() / 100.0,
            tithiProgressFraction = progressFraction,
            illuminationPercent = illuminationPercent,
            isWaxing = isWaxing,
            tithiStartTimeMillis = startMillis,
            tithiEndTimeMillis = endMillis,
            formattedStartTime = shortDateTimeFmt.format(Date(startMillis)),
            formattedEndTime = shortDateTimeFmt.format(Date(endMillis)),
            remainingDurationText = remainingDurationText,
            nakshatraName = nakshatraName,
            nakshatraPada = nakshatraPada,
            karanaName = karanaName,
            yogaName = yogaName,
            lunarAgeDays = lunarAgeDays
        )
    }

    /**
     * Generates a 30-day upcoming Tithi forecast starting from baseMillis.
     */
    fun generate30DayForecast(
        baseMillis: Long,
        timeZone: TimeZone = TimeZone.getDefault()
    ): List<DailyTithiForecast> {
        val result = mutableListOf<DailyTithiForecast>()
        val cal = Calendar.getInstance(timeZone).apply {
            timeInMillis = baseMillis
            set(Calendar.HOUR_OF_DAY, 6)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val dateShortFmt = SimpleDateFormat("MMM dd", Locale.US).apply { this.timeZone = timeZone }
        val dowFmt = SimpleDateFormat("EEE", Locale.US).apply { this.timeZone = timeZone }
        val timeFmt = SimpleDateFormat("hh:mm a", Locale.US).apply { this.timeZone = timeZone }

        for (dayOffset in 0 until 30) {
            val dayMillis = cal.timeInMillis + dayOffset * 86400_000L
            val state = calculateLiveTithiState(dayMillis, timeZone)
            result.add(
                DailyTithiForecast(
                    dateMillis = dayMillis,
                    formattedDateShort = dateShortFmt.format(Date(dayMillis)),
                    dayOfWeekShort = dowFmt.format(Date(dayMillis)),
                    sunriseTithi = state.currentTithi,
                    endTimeFormatted = "Ends ${timeFmt.format(Date(state.tithiEndTimeMillis))}",
                    illuminationPercent = state.illuminationPercent.roundToInt(),
                    elongationDeg = state.elongationDeg
                )
            )
        }
        return result
    }
}
