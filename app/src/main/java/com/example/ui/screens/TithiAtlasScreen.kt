package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.Paksha
import com.example.domain.TithiDefinition
import com.example.ui.PakshaFilter
import com.example.ui.components.AstronomicalMoonPhaseOrb
import com.example.ui.components.MiniMoonPhaseCanvas
import com.example.ui.theme.AuspiciousEmerald
import com.example.ui.theme.CelestialCardBorder
import com.example.ui.theme.ChandraPearl
import com.example.ui.theme.ChandraSilver
import com.example.ui.theme.CosmicMidnight
import com.example.ui.theme.DeepIndigoSurface
import com.example.ui.theme.ElevatedCelestialCard
import com.example.ui.theme.MutedStarlight
import com.example.ui.theme.PakshaKrishnaAccent
import com.example.ui.theme.SuryaGold
import com.example.ui.theme.SuryaGoldBright
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt

/**
 * Complete Atlas of all 30 Vedic Tithis starting from:
 * 1. Pradhama, 2. Dvitheeya, 3. Thritheeya, 4. Chathurthi, 5. Panchami, 6. Shashthi,
 * 7. Saptami, 8. Ashtami, 9. Navami, 10. Dashami, 11. Ekadashi, 12. Dwadashi,
 * 13. Trayodashi, 14. Chaturdashi, 15. Pournami (Shukla Paksha)
 * and 16..30 Pradhama through Amavasya (Krishna Paksha),
 * each with its corresponding rendered Moon Phase image!
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TithiAtlasScreen(
    allTithis: List<TithiDefinition>,
    activeTithiNumber: Int,
    pakshaFilter: PakshaFilter,
    selectedTithiForDetail: TithiDefinition?,
    onSelectFilter: (PakshaFilter) -> Unit,
    onSelectTithiDetail: (TithiDefinition?) -> Unit,
    onPreviewTithiOnClock: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredTithis = when (pakshaFilter) {
        PakshaFilter.ALL -> allTithis
        PakshaFilter.SHUKLA -> allTithis.filter { it.paksha == Paksha.SHUKLA }
        PakshaFilter.KRISHNA -> allTithis.filter { it.paksha == Paksha.KRISHNA }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CosmicMidnight)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("tithi_atlas_list"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 600.dp)
                ) {
                    Text(
                        text = "30 Vedic Tithis & Moon Phases",
                        style = MaterialTheme.typography.headlineMedium,
                        color = ChandraPearl
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "From Pradhama, Dvitheeya, Thritheeya, Chathurthi to Pournami & Amavasya — each spanning 12° of Sun-Moon elongation.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedStarlight
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = pakshaFilter == PakshaFilter.ALL,
                            onClick = { onSelectFilter(PakshaFilter.ALL) },
                            label = { Text("All 30 Tithis") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SuryaGold.copy(alpha = 0.22f),
                                selectedLabelColor = SuryaGoldBright
                            ),
                            modifier = Modifier.testTag("filter_all_tithis")
                        )
                        FilterChip(
                            selected = pakshaFilter == PakshaFilter.SHUKLA,
                            onClick = { onSelectFilter(PakshaFilter.SHUKLA) },
                            label = { Text("Shukla (1–15)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SuryaGold.copy(alpha = 0.22f),
                                selectedLabelColor = SuryaGoldBright
                            ),
                            modifier = Modifier.testTag("filter_shukla_tithis")
                        )
                        FilterChip(
                            selected = pakshaFilter == PakshaFilter.KRISHNA,
                            onClick = { onSelectFilter(PakshaFilter.KRISHNA) },
                            label = { Text("Krishna (16–30)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PakshaKrishnaAccent.copy(alpha = 0.22f),
                                selectedLabelColor = PakshaKrishnaAccent
                            ),
                            modifier = Modifier.testTag("filter_krishna_tithis")
                        )
                    }
                }
            }

            items(
                items = filteredTithis,
                key = { it.number }
            ) { tithi ->
                val isCurrent = tithi.number == activeTithiNumber
                TithiAtlasItemCard(
                    tithi = tithi,
                    isCurrentActive = isCurrent,
                    onClick = { onSelectTithiDetail(tithi) }
                )
            }
        }

        // Modal Bottom Sheet for inspecting any Tithi + its large Moon Phase orb
        if (selectedTithiForDetail != null) {
            ModalBottomSheet(
                onDismissRequest = { onSelectTithiDetail(null) },
                containerColor = DeepIndigoSurface,
                contentColor = ChandraSilver
            ) {
                val t = selectedTithiForDetail
                val isShukla = t.paksha == Paksha.SHUKLA
                val accent = if (isShukla) SuryaGold else PakshaKrishnaAccent
                val midIllum = (((1.0 - cos(t.midpointElongation * PI / 180.0)) / 2.0) * 100).roundToInt()

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${t.paksha.displayName} • Day ${t.pakshaDayNumber} of 15",
                        style = MaterialTheme.typography.labelLarge,
                        color = accent
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${t.primaryName} (${t.sanskritScript})",
                        style = MaterialTheme.typography.headlineLarge,
                        color = ChandraPearl
                    )
                    Text(
                        text = "Classical spelling: ${t.alternateSpelling} • Arc: ${t.degreeRangeText}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedStarlight
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AstronomicalMoonPhaseOrb(
                        elongationDeg = t.midpointElongation,
                        activeTithiNumber = t.number,
                        orbSize = 200.dp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = ElevatedCelestialCard,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "${t.moonPhaseLabel} • ~${midIllum}% Illumination",
                                style = MaterialTheme.typography.titleSmall,
                                color = accent,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Category: ${t.categoryName}  |  Deity: ${t.presidingDeity}",
                                style = MaterialTheme.typography.labelMedium,
                                color = ChandraSilver
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = t.significance,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MutedStarlight
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { onPreviewTithiOnClock(t.number) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accent,
                            contentColor = CosmicMidnight
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("preview_tithi_on_clock_button")
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "View ${t.primaryName} on Live Tithi Clock",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun TithiAtlasItemCard(
    tithi: TithiDefinition,
    isCurrentActive: Boolean,
    onClick: () -> Unit
) {
    val isShukla = tithi.paksha == Paksha.SHUKLA
    val accent = if (isShukla) SuryaGold else PakshaKrishnaAccent
    val midIllum = (((1.0 - cos(tithi.midpointElongation * PI / 180.0)) / 2.0) * 100).roundToInt()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 600.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = if (isCurrentActive) 2.dp else 1.dp,
                color = if (isCurrentActive) AuspiciousEmerald else CelestialCardBorder,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .testTag("tithi_atlas_card_${tithi.number}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentActive) ElevatedCelestialCard else DeepIndigoSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Corresponding Moon Phase Image for this Tithi
            MiniMoonPhaseCanvas(
                elongationDeg = tithi.midpointElongation,
                size = 54.dp
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = accent.copy(alpha = 0.18f)
                    ) {
                        Text(
                            text = "#${tithi.number} • ${tithi.paksha.shortName} ${tithi.pakshaDayNumber}",
                            style = MaterialTheme.typography.labelMedium,
                            color = accent,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    if (isCurrentActive) {
                        Surface(
                            shape = CircleShape,
                            color = AuspiciousEmerald.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "ACTIVE NOW",
                                style = MaterialTheme.typography.labelMedium,
                                color = AuspiciousEmerald,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    } else if (tithi.isSpecialObservance) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Special Observance",
                            tint = SuryaGold,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = tithi.primaryName,
                        style = MaterialTheme.typography.titleLarge,
                        color = ChandraPearl
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = tithi.sanskritScript,
                        style = MaterialTheme.typography.titleSmall,
                        color = accent
                    )
                }

                Text(
                    text = "${tithi.moonPhaseLabel} • ${tithi.degreeRangeText} • ~${midIllum}% lit",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedStarlight
                )
            }
        }
    }
}
