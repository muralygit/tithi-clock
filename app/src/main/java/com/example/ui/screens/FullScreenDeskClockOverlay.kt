package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.LiveTithiState
import com.example.domain.Paksha
import com.example.ui.components.AstronomicalMoonPhaseOrb
import com.example.ui.theme.AuspiciousEmerald
import com.example.ui.theme.ChandraPearl
import com.example.ui.theme.CosmicMidnight
import com.example.ui.theme.ElevatedCelestialCard
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.MutedStarlight
import com.example.ui.theme.PakshaKrishnaAccent
import com.example.ui.theme.SuryaGold

/**
 * Full-Screen Always-On Desk / Altar Clock Mode showing:
 * - Giant live ticking Time (HH:mm:ss)
 * - Current Vedic Tithi (Pradhama, Dvitheeya, Thritheeya, Chathurthi...)
 * - Corresponding High-Resolution Moon Phase Orb
 * - Remaining time in current Tithi
 */
@Composable
fun FullScreenDeskClockOverlay(
    liveState: LiveTithiState,
    use24HourFormat: Boolean,
    onExitFullScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onExitFullScreen()
    }

    val tithi = liveState.currentTithi
    val isShukla = tithi.paksha == Paksha.SHUKLA
    val accent = if (isShukla) SuryaGold else PakshaKrishnaAccent

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CosmicMidnight)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(24.dp)
            .testTag("fullscreen_desk_clock_overlay")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = ElevatedCelestialCard,
                    modifier = Modifier.border(1.dp, AuspiciousEmerald, CircleShape)
                ) {
                    Text(
                        text = "ALWAYS-ON TITHI CLOCK",
                        style = MaterialTheme.typography.labelMedium,
                        color = AuspiciousEmerald,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                FilledTonalButton(
                    onClick = onExitFullScreen,
                    modifier = Modifier.testTag("exit_fullscreen_clock_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.FullscreenExit,
                        contentDescription = "Exit Full-Screen Clock"
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Exit")
                }
            }

            // Center Moon Orb & Live Clock
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AstronomicalMoonPhaseOrb(
                    elongationDeg = liveState.elongationDeg,
                    activeTithiNumber = tithi.number,
                    orbSize = 260.dp
                )

                Spacer(modifier = Modifier.height(20.dp))

                if (use24HourFormat) {
                    Text(
                        text = liveState.formattedTime24h,
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 60.sp,
                            color = ChandraPearl
                        )
                    )
                } else {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = liveState.formattedTime12h,
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = 62.sp,
                                color = ChandraPearl
                            )
                        )
                        Text(
                            text = liveState.formattedSeconds,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                color = accent
                            ),
                            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = liveState.amPm,
                            style = MaterialTheme.typography.titleMedium,
                            color = MutedStarlight,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                    }
                }

                Text(
                    text = "${liveState.formattedDate}  •  ${liveState.varaName}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MutedStarlight
                )

                Spacer(modifier = Modifier.height(18.dp))

                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = ElevatedCelestialCard,
                    modifier = Modifier.border(1.5.dp, accent.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = tithi.paksha.displayName.uppercase(),
                            style = MaterialTheme.typography.labelLarge,
                            color = accent
                        )
                        Text(
                            text = "${tithi.primaryName} (${tithi.sanskritScript})",
                            style = MaterialTheme.typography.displayMedium,
                            color = ChandraPearl,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${liveState.illuminationPercent}% Moon  •  ${liveState.remainingDurationText}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedStarlight
                        )
                    }
                }
            }

            // Footer Panchang line
            Text(
                text = "Nakshatra: ${liveState.nakshatraName} (Pada ${liveState.nakshatraPada})  •  Ends ${liveState.formattedEndTime}",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedStarlight,
                textAlign = TextAlign.Center
            )
        }
    }
}
