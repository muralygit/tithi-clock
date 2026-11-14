package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CelestialDarkColorScheme = darkColorScheme(
    primary = SuryaGold,
    onPrimary = CosmicMidnight,
    primaryContainer = Color(0xFF2B2210),
    onPrimaryContainer = SuryaGoldBright,
    secondary = PakshaKrishnaAccent,
    onSecondary = CosmicMidnight,
    secondaryContainer = Color(0xFF16274C),
    onSecondaryContainer = Color(0xFFD4E3FF),
    tertiary = SacredSaffron,
    onTertiary = CosmicMidnight,
    background = CosmicMidnight,
    onBackground = ChandraSilver,
    surface = DeepIndigoSurface,
    onSurface = ChandraSilver,
    surfaceVariant = ElevatedCelestialCard,
    onSurfaceVariant = MutedStarlight,
    outline = CelestialCardBorder
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CelestialDarkColorScheme,
        typography = Typography,
        content = content
    )
}
