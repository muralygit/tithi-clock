package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.domain.LiveTithiState
import com.example.domain.Paksha
import com.example.ui.components.AstronomicalMoonPhaseOrb
import com.example.ui.theme.AuspiciousEmerald
import com.example.ui.theme.CelestialCardBorder
import com.example.ui.theme.ChandraPearl
import com.example.ui.theme.ChandraSilver
import com.example.ui.theme.CosmicMidnight
import com.example.ui.theme.DeepIndigoSurface
import com.example.ui.theme.ElevatedCelestialCard
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.MutedStarlight
import com.example.ui.theme.PakshaKrishnaAccent
import com.example.ui.theme.SacredSaffron
import com.example.ui.theme.SuryaGold
import com.example.ui.theme.SuryaGoldBright
import kotlin.math.roundToInt

@Composable
fun LiveTithiClockScreen(
    liveState: LiveTithiState,
    isLiveMode: Boolean,
    use24HourFormat: Boolean,
    onToggleTimeFormat: () -> Unit,
    onStepTithi: (Int) -> Unit,
    onResetToLive: () -> Unit,
    onOpenFullScreenClock: () -> Unit,
    onPinWidgetClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tithi = liveState.currentTithi
    val isShukla = tithi.paksha == Paksha.SHUKLA
    val pakshaAccent = if (isShukla) SuryaGold else PakshaKrishnaAccent

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CosmicMidnight)
            .testTag("live_tithi_clock_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Unified Mobile Time + Live Tithi Hero Clock Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                pakshaAccent.copy(alpha = 0.65f),
                                CelestialCardBorder,
                                pakshaAccent.copy(alpha = 0.3f)
                            )
                        ),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .testTag("hero_clock_tithi_card"),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = DeepIndigoSurface)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Celestial Banner Artwork Background with gradient scrim
                    Image(
                        painter = painterResource(id = R.drawable.img_celestial_banner_1794626434222),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(230.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(230.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        CosmicMidnight.copy(alpha = 0.55f),
                                        DeepIndigoSurface.copy(alpha = 0.88f),
                                        DeepIndigoSurface
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Top Status Bar: Live Indicator + 12h/24h Toggle + Fullscreen Clock Icon
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Live / Preview Pill
                            Surface(
                                shape = CircleShape,
                                color = if (isLiveMode) {
                                    AuspiciousEmerald.copy(alpha = 0.16f)
                                } else {
                                    SacredSaffron.copy(alpha = 0.2f)
                                },
                                modifier = Modifier
                                    .border(
                                        width = 1.dp,
                                        color = if (isLiveMode) AuspiciousEmerald else SacredSaffron,
                                        shape = CircleShape
                                    )
                                    .clickable { if (!isLiveMode) onResetToLive() }
                                    .testTag("live_status_badge")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(if (isLiveMode) AuspiciousEmerald else SacredSaffron)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isLiveMode) "LIVE TITHI CLOCK" else "PREVIEW • TAP FOR LIVE",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (isLiveMode) AuspiciousEmerald else SacredSaffron,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = ElevatedCelestialCard.copy(alpha = 0.85f),
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .clickable { onToggleTimeFormat() }
                                        .testTag("toggle_time_format_button")
                                ) {
                                    Text(
                                        text = if (use24HourFormat) "24H" else "12H",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = SuryaGoldBright,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                    )
                                }

                                FilledTonalIconButton(
                                    onClick = onOpenFullScreenClock,
                                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                                        containerColor = ElevatedCelestialCard.copy(alpha = 0.85f),
                                        contentColor = ChandraPearl
                                    ),
                                    modifier = Modifier
                                        .size(38.dp)
                                        .testTag("fullscreen_clock_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Fullscreen,
                                        contentDescription = "Open Full-Screen Tithi Clock"
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // LIVE DIGITAL TIME DISPLAY
                        if (use24HourFormat) {
                            Text(
                                text = liveState.formattedTime24h,
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontSize = 46.sp,
                                    color = ChandraPearl
                                ),
                                modifier = Modifier.testTag("digital_clock_text")
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.testTag("digital_clock_text")
                            ) {
                                Text(
                                    text = liveState.formattedTime12h,
                                    style = MaterialTheme.typography.displayLarge.copy(
                                        fontSize = 48.sp,
                                        color = ChandraPearl
                                    )
                                )
                                Text(
                                    text = liveState.formattedSeconds,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontSize = 22.sp,
                                        color = pakshaAccent
                                    ),
                                    modifier = Modifier.padding(bottom = 6.dp, start = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = liveState.amPm,
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontSize = 15.sp,
                                        color = MutedStarlight
                                    ),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                        }

                        // Date + Vedic Weekday (Vara)
                        Text(
                            text = "${liveState.formattedDate}  •  ${liveState.varaName}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedStarlight
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // PROMINENT TITHI NAME DISPLAY (Directly paired with Time)
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = ElevatedCelestialCard.copy(alpha = 0.92f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 1.dp,
                                    color = pakshaAccent.copy(alpha = 0.45f),
                                    shape = RoundedCornerShape(20.dp)
                                )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "${tithi.paksha.displayName.uppercase()}  •  TITHI ${tithi.pakshaDayNumber} OF 15",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = pakshaAccent,
                                    letterSpacing = 1.2.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = tithi.primaryName,
                                    style = MaterialTheme.typography.displayMedium.copy(
                                        color = ChandraPearl,
                                        fontSize = 32.sp
                                    ),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.testTag("current_tithi_name")
                                )
                                Text(
                                    text = "${tithi.sanskritScript}  •  Also spelled ${tithi.alternateSpelling}  •  ${tithi.moonPhaseLabel}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MutedStarlight,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // CORRESPONDING MOON IMAGE + 30-TITHI ORBITAL VISUALIZER + STEPPER
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            FilledTonalIconButton(
                                onClick = { onStepTithi(-1) },
                                colors = IconButtonDefaults.filledTonalIconButtonColors(
                                    containerColor = ElevatedCelestialCard,
                                    contentColor = pakshaAccent
                                ),
                                modifier = Modifier
                                    .size(48.dp)
                                    .testTag("prev_tithi_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                    contentDescription = "Previous Tithi"
                                )
                            }

                            AstronomicalMoonPhaseOrb(
                                elongationDeg = liveState.elongationDeg,
                                activeTithiNumber = tithi.number,
                                orbSize = 236.dp,
                                modifier = Modifier.testTag("moon_phase_orb")
                            )

                            FilledTonalIconButton(
                                onClick = { onStepTithi(1) },
                                colors = IconButtonDefaults.filledTonalIconButtonColors(
                                    containerColor = ElevatedCelestialCard,
                                    contentColor = pakshaAccent
                                ),
                                modifier = Modifier
                                    .size(48.dp)
                                    .testTag("next_tithi_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = "Next Tithi"
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Illumination & Elongation Telemetry Chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
                        ) {
                            TelemetryPill(
                                label = "Moon Illuminated",
                                value = "${liveState.illuminationPercent}%",
                                accent = pakshaAccent
                            )
                            TelemetryPill(
                                label = "Sun-Moon Arc",
                                value = "${liveState.elongationDeg}° (${tithi.degreeRangeText})",
                                accent = ChandraSilver
                            )
                        }

                        // Return to Live button if user stepped through Tithis
                        AnimatedVisibility(
                            visible = !isLiveMode,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            OutlinedButton(
                                onClick = onResetToLive,
                                modifier = Modifier
                                    .padding(top = 12.dp)
                                    .testTag("return_to_live_button"),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SuryaGold)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = SuryaGold,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Return to Live Current Time",
                                    color = SuryaGoldBright
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Active Tithi Progress & Start/End Timings Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
                    .border(1.dp, CelestialCardBorder, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DeepIndigoSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = pakshaAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${tithi.primaryName} Span & Timing",
                                style = MaterialTheme.typography.titleMedium,
                                color = ChandraPearl
                            )
                        }
                        Text(
                            text = liveState.remainingDurationText,
                            style = MaterialTheme.typography.labelLarge,
                            color = pakshaAccent
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { liveState.tithiProgressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(CircleShape),
                        color = pakshaAccent,
                        trackColor = ElevatedCelestialCard,
                        strokeCap = StrokeCap.Round
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "TITHI BEGINS (${tithi.startDegree.toInt()}°)",
                                style = MaterialTheme.typography.labelMedium,
                                color = MutedStarlight
                            )
                            Text(
                                text = liveState.formattedStartTime,
                                style = MaterialTheme.typography.titleSmall,
                                color = ChandraSilver
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "ELAPSED",
                                style = MaterialTheme.typography.labelMedium,
                                color = MutedStarlight
                            )
                            Text(
                                text = "${(liveState.tithiProgressFraction * 100).roundToInt()}%",
                                style = MaterialTheme.typography.labelLarge,
                                color = pakshaAccent
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "TITHI ENDS (${tithi.endDegree.toInt()}°)",
                                style = MaterialTheme.typography.labelMedium,
                                color = MutedStarlight
                            )
                            Text(
                                text = liveState.formattedEndTime,
                                style = MaterialTheme.typography.titleSmall,
                                color = ChandraSilver
                            )
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = CelestialCardBorder.copy(alpha = 0.6f)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Next Tithi: ${liveState.nextTithi.fullTitle} (${liveState.nextTithi.sanskritScript})",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedStarlight
                        )
                        Text(
                            text = "Lunar Day ${liveState.lunarAgeDays}d",
                            style = MaterialTheme.typography.labelMedium,
                            color = SuryaGold
                        )
                    }
                }
            }
        }

        // 3. Vedic Panchang Details & Significance Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
                    .border(1.dp, CelestialCardBorder, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DeepIndigoSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Panchang & Astronomical Coordinates",
                        style = MaterialTheme.typography.titleMedium,
                        color = ChandraPearl
                    )

                    Text(
                        text = tithi.significance,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedStarlight
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PanchangAttributeTile(
                            icon = Icons.Default.AutoAwesome,
                            label = "Nakshatra (Star)",
                            value = "${liveState.nakshatraName} (Pada ${liveState.nakshatraPada})",
                            modifier = Modifier.weight(1f)
                        )
                        PanchangAttributeTile(
                            icon = Icons.Default.NightsStay,
                            label = "Karana & Yoga",
                            value = "${liveState.karanaName} • ${liveState.yogaName}",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PanchangAttributeTile(
                            icon = Icons.Default.WbSunny,
                            label = "Surya / Chandra Long.",
                            value = "☉ ${liveState.sunLongitudeDeg}°  ☽ ${liveState.moonLongitudeDeg}°",
                            modifier = Modifier.weight(1f)
                        )
                        PanchangAttributeTile(
                            icon = Icons.Default.AccessTime,
                            label = "Tithi Nature & Deity",
                            value = "${tithi.categoryName} • ${tithi.presidingDeity}",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 4. Quick Home Screen Widget Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
                    .border(1.dp, SuryaGold.copy(alpha = 0.45f), RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = ElevatedCelestialCard)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Show Tithi & Moon on Mobile Home Screen",
                            style = MaterialTheme.typography.titleSmall,
                            color = SuryaGoldBright,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Add the live Tithi Clock Widget alongside your phone's clock.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedStarlight
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = onPinWidgetClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SuryaGold,
                            contentColor = CosmicMidnight
                        ),
                        modifier = Modifier.testTag("home_pin_widget_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Widgets,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Add Widget", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        // Developer credit
        item {
            DeveloperCredit()
        }
    }
}

/** Small developer credit line shown at the bottom of the main screens. */
@Composable
internal fun DeveloperCredit(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Developer: Muraly C B",
            style = MaterialTheme.typography.bodySmall,
            color = MutedStarlight,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Phone: 7012451340",
            style = MaterialTheme.typography.bodySmall,
            color = MutedStarlight,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TelemetryPill(
    label: String,
    value: String,
    accent: Color
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = ElevatedCelestialCard,
        modifier = Modifier.border(1.dp, CelestialCardBorder, RoundedCornerShape(14.dp))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MutedStarlight
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                color = accent
            )
        }
    }
}

@Composable
private fun PanchangAttributeTile(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = ElevatedCelestialCard
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = SuryaGold,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MutedStarlight
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                color = ChandraSilver,
                maxLines = 2
            )
        }
    }
}
