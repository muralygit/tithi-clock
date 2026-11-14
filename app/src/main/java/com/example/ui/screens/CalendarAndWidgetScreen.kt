package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.DailyTithiForecast
import com.example.domain.LiveTithiState
import com.example.domain.Paksha
import com.example.ui.components.MiniMoonPhaseCanvas
import com.example.ui.theme.AuspiciousEmerald
import com.example.ui.theme.CelestialCardBorder
import com.example.ui.theme.ChandraPearl
import com.example.ui.theme.CosmicMidnight
import com.example.ui.theme.DeepIndigoSurface
import com.example.ui.theme.ElevatedCelestialCard
import com.example.ui.theme.MutedStarlight
import com.example.ui.theme.PakshaKrishnaAccent
import com.example.ui.theme.SuryaGold
import com.example.ui.theme.SuryaGoldBright
import kotlin.math.roundToInt

@Composable
fun CalendarAndWidgetScreen(
    liveState: LiveTithiState,
    forecast30Days: List<DailyTithiForecast>,
    widgetStatusMessage: String?,
    onPinWidgetClick: () -> Unit,
    onRefreshWidgetClick: () -> Unit,
    onOpenFullScreenClock: () -> Unit,
    onSelectForecastDay: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CosmicMidnight)
            .testTag("calendar_widget_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Mobile Home Screen Widget Live Preview & Pin Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
                    .border(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(SuryaGold, PakshaKrishnaAccent)
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DeepIndigoSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Widgets,
                            contentDescription = null,
                            tint = SuryaGold,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Mobile Home Screen Tithi & Clock Widget",
                            style = MaterialTheme.typography.titleMedium,
                            color = ChandraPearl
                        )
                    }

                    Text(
                        text = "View the live time, current Tithi (Pradhama, Dvitheeya, Thritheeya…), and corresponding Moon Phase image directly on your phone's Home Screen.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedStarlight
                    )

                    // Simulated Home Screen Widget Preview Box
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = ElevatedCelestialCard,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, SuryaGold.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MiniMoonPhaseCanvas(
                                elongationDeg = liveState.elongationDeg,
                                size = 64.dp
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${liveState.formattedTime12h}${liveState.formattedSeconds} ${liveState.amPm}",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = ChandraPearl,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${liveState.currentTithi.fullTitle} (${liveState.currentTithi.sanskritScript})",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = SuryaGoldBright
                                )
                                Text(
                                    text = "${liveState.illuminationPercent.roundToInt()}% Moon • Ends ${liveState.formattedEndTime}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MutedStarlight
                                )
                            }
                        }
                    }

                    if (widgetStatusMessage != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = AuspiciousEmerald.copy(alpha = 0.15f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = widgetStatusMessage,
                                style = MaterialTheme.typography.bodyMedium,
                                color = AuspiciousEmerald,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onPinWidgetClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SuryaGold,
                                contentColor = CosmicMidnight
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("pin_widget_action_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Widgets,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Pin to Home Screen", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onRefreshWidgetClick,
                            border = androidx.compose.foundation.BorderStroke(1.dp, SuryaGold),
                            modifier = Modifier.testTag("sync_widget_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = "Sync Widget",
                                tint = SuryaGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Sync", color = SuryaGoldBright)
                        }
                    }

                    OutlinedButton(
                        onClick = onOpenFullScreenClock,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CelestialCardBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("open_desk_clock_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fullscreen,
                            contentDescription = null,
                            tint = ChandraPearl,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Launch Full-Screen Always-On Desk Clock",
                            color = ChandraPearl
                        )
                    }
                }
            }
        }

        // 2. Upcoming 30-Day Tithi Calendar Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
            ) {
                Text(
                    text = "Upcoming 30-Day Tithi Schedule",
                    style = MaterialTheme.typography.headlineMedium,
                    color = ChandraPearl
                )
                Text(
                    text = "Tap any date to preview its Tithi and Moon Phase on the Live Clock.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedStarlight
                )
            }
        }

        // 3. 30-Day Forecast Rows
        itemsIndexed(
            items = forecast30Days,
            key = { _, item -> item.dateMillis }
        ) { index, dayItem ->
            val isToday = index == 0
            val isShukla = dayItem.sunriseTithi.paksha == Paksha.SHUKLA
            val accent = if (isShukla) SuryaGold else PakshaKrishnaAccent

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .border(
                        width = if (isToday) 1.5.dp else 1.dp,
                        color = if (isToday) SuryaGold else CelestialCardBorder,
                        shape = RoundedCornerShape(18.dp)
                    )
                    .clickable { onSelectForecastDay(dayItem.dateMillis) }
                    .testTag("forecast_day_row_$index"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isToday) ElevatedCelestialCard else DeepIndigoSurface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Date Column
                    Column(
                        modifier = Modifier.width(64.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isToday) "TODAY" else dayItem.dayOfWeekShort.uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isToday) AuspiciousEmerald else MutedStarlight,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = dayItem.formattedDateShort,
                            style = MaterialTheme.typography.titleSmall,
                            color = ChandraPearl
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    MiniMoonPhaseCanvas(
                        elongationDeg = dayItem.elongationDeg,
                        size = 44.dp
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${dayItem.sunriseTithi.fullTitle} (${dayItem.sunriseTithi.sanskritScript})",
                            style = MaterialTheme.typography.titleMedium,
                            color = ChandraPearl
                        )
                        Text(
                            text = "${dayItem.endTimeFormatted} • ${dayItem.illuminationPercent}% Moon",
                            style = MaterialTheme.typography.bodyMedium,
                            color = accent
                        )
                    }
                }
            }
        }
        item {
            DeveloperCredit()
        }
    }
}
