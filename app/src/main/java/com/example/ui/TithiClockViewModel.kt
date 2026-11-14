package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.DailyTithiForecast
import com.example.domain.LiveTithiState
import com.example.domain.Paksha
import com.example.domain.TithiAstronomyEngine
import com.example.domain.TithiDefinition
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class TithiAppTab {
    LIVE_CLOCK,
    TITHI_ATLAS,
    CALENDAR_AND_WIDGET
}

enum class PakshaFilter {
    ALL,
    SHUKLA,
    KRISHNA
}

data class TithiClockUiState(
    val selectedTab: TithiAppTab = TithiAppTab.LIVE_CLOCK,
    val isLiveMode: Boolean = true,
    val previewOffsetMillis: Long = 0L,
    val use24HourFormat: Boolean = false,
    val isFullScreenClockMode: Boolean = false,
    val liveTithiState: LiveTithiState = TithiAstronomyEngine.calculateLiveTithiState(System.currentTimeMillis()),
    val allTithis: List<TithiDefinition> = TithiAstronomyEngine.all30Tithis,
    val pakshaFilter: PakshaFilter = PakshaFilter.ALL,
    val selectedTithiForDetail: TithiDefinition? = null,
    val forecast30Days: List<DailyTithiForecast> = TithiAstronomyEngine.generate30DayForecast(System.currentTimeMillis()),
    val widgetStatusMessage: String? = null
)

class TithiClockViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TithiClockUiState())
    val uiState: StateFlow<TithiClockUiState> = _uiState.asStateFlow()

    init {
        startLiveClockTicker()
    }

    private fun startLiveClockTicker() {
        viewModelScope.launch {
            while (isActive) {
                val now = System.currentTimeMillis()
                val current = _uiState.value
                val effectiveTime = if (current.isLiveMode) now else now + current.previewOffsetMillis
                val updatedState = TithiAstronomyEngine.calculateLiveTithiState(effectiveTime)
                _uiState.update { state ->
                    state.copy(liveTithiState = updatedState)
                }
                delay(1000L)
            }
        }
    }

    fun selectTab(tab: TithiAppTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun toggleTimeFormat() {
        _uiState.update { it.copy(use24HourFormat = !it.use24HourFormat) }
    }

    fun setFullScreenClockMode(enabled: Boolean) {
        _uiState.update { it.copy(isFullScreenClockMode = enabled) }
    }

    fun setPakshaFilter(filter: PakshaFilter) {
        _uiState.update { it.copy(pakshaFilter = filter) }
    }

    fun selectTithiDetail(tithi: TithiDefinition?) {
        _uiState.update { it.copy(selectedTithiForDetail = tithi) }
    }

    /**
     * Steps forward or backward by [deltaTithis] (~23.62 hours per Tithi) so the user can
     * inspect any Tithi (Pradhama, Dvitheeya, Thritheeya, Chathurthi...) and its Moon phase right on the clock.
     */
    fun stepByTithi(deltaTithis: Int) {
        val avgTithiMillis = (29.530588853 / 30.0 * 86400_000.0).toLong()
        val now = System.currentTimeMillis()
        val newOffset = _uiState.value.previewOffsetMillis + deltaTithis * avgTithiMillis
        val targetMillis = now + newOffset
        _uiState.update {
            it.copy(
                isLiveMode = false,
                previewOffsetMillis = newOffset,
                liveTithiState = TithiAstronomyEngine.calculateLiveTithiState(targetMillis)
            )
        }
    }

    /**
     * Jumps the clock preview to a specific Tithi (1..30) so the user can view its exact Moon phase on the live orb.
     */
    fun previewSpecificTithi(targetTithiNumber: Int) {
        val now = System.currentTimeMillis()
        val currentElong = TithiAstronomyEngine.calculateElongationDegrees(now)
        val targetMidElong = (targetTithiNumber - 1) * 12.0 + 6.0
        var deltaDeg = (targetMidElong - currentElong) % 360.0
        if (deltaDeg < 0) deltaDeg += 360.0
        val offsetMillis = ((deltaDeg / 12.190749) * 86400_000.0).toLong()
        val targetMillis = now + offsetMillis
        _uiState.update {
            it.copy(
                selectedTab = TithiAppTab.LIVE_CLOCK,
                isLiveMode = false,
                previewOffsetMillis = offsetMillis,
                selectedTithiForDetail = null,
                liveTithiState = TithiAstronomyEngine.calculateLiveTithiState(targetMillis)
            )
        }
    }

    /**
     * Jumps to a specific timestamp from the 30-day forecast calendar.
     */
    fun previewTimestamp(targetMillis: Long) {
        val now = System.currentTimeMillis()
        val offsetMillis = targetMillis - now
        _uiState.update {
            it.copy(
                selectedTab = TithiAppTab.LIVE_CLOCK,
                isLiveMode = false,
                previewOffsetMillis = offsetMillis,
                liveTithiState = TithiAstronomyEngine.calculateLiveTithiState(targetMillis)
            )
        }
    }

    fun resetToLiveNow() {
        val now = System.currentTimeMillis()
        _uiState.update {
            it.copy(
                isLiveMode = true,
                previewOffsetMillis = 0L,
                liveTithiState = TithiAstronomyEngine.calculateLiveTithiState(now)
            )
        }
    }

    fun setWidgetStatusMessage(message: String?) {
        _uiState.update { it.copy(widgetStatusMessage = message) }
    }
}
