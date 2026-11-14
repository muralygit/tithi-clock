package com.example

import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.AutoAwesomeMosaic
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.AutoAwesomeMosaic
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.TithiAppTab
import com.example.ui.TithiClockViewModel
import com.example.ui.screens.CalendarAndWidgetScreen
import com.example.ui.screens.FullScreenDeskClockOverlay
import com.example.ui.screens.LiveTithiClockScreen
import com.example.ui.screens.TithiAtlasScreen
import com.example.ui.theme.CosmicMidnight
import com.example.ui.theme.DeepIndigoSurface
import com.example.ui.theme.ElevatedCelestialCard
import com.example.ui.theme.MutedStarlight
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SuryaGold
import com.example.ui.theme.SuryaGoldBright
import com.example.widget.TithiClockWidgetProvider

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Ensure any placed Home Screen Tithi Clock widgets are synced on launch
        TithiClockWidgetProvider.refreshAllWidgets(this)

        setContent {
            MyApplicationTheme {
                val viewModel: TithiClockViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                // Keep screen awake when in Full-Screen Desk Clock Mode
                DisposableEffect(uiState.isFullScreenClockMode) {
                    if (uiState.isFullScreenClockMode) {
                        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    } else {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    }
                    onDispose {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    }
                }

                TithiClockApp(
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun TithiClockApp(
    viewModel: TithiClockViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Handle Back Navigation from secondary tabs back to the primary Live Clock tab
    if (uiState.selectedTab != TithiAppTab.LIVE_CLOCK && !uiState.isFullScreenClockMode) {
        BackHandler {
            viewModel.selectTab(TithiAppTab.LIVE_CLOCK)
        }
    }

    val handlePinWidget: () -> Unit = {
        TithiClockWidgetProvider.refreshAllWidgets(context)
        val pinned = TithiClockWidgetProvider.requestPinWidgetToHomeScreen(context)
        val msg = if (pinned) {
            "Confirm in the system prompt to place the Tithi Clock Widget on your Home Screen."
        } else {
            "Widget synced! You can also long-press your phone's Home Screen -> Widgets -> Tithi Clock."
        }
        viewModel.setWidgetStatusMessage(msg)
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }

    val handleRefreshWidget: () -> Unit = {
        TithiClockWidgetProvider.refreshAllWidgets(context)
        val msg = "Home Screen Tithi Widget updated with current ${uiState.liveTithiState.currentTithi.fullTitle} & Moon image!"
        viewModel.setWidgetStatusMessage(msg)
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    if (uiState.isFullScreenClockMode) {
        FullScreenDeskClockOverlay(
            liveState = uiState.liveTithiState,
            use24HourFormat = uiState.use24HourFormat,
            onExitFullScreen = { viewModel.setFullScreenClockMode(false) }
        )
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = CosmicMidnight,
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                NavigationBar(
                    containerColor = DeepIndigoSurface,
                    contentColor = MutedStarlight
                ) {
                    NavigationBarItem(
                        selected = uiState.selectedTab == TithiAppTab.LIVE_CLOCK,
                        onClick = { viewModel.selectTab(TithiAppTab.LIVE_CLOCK) },
                        icon = {
                            Icon(
                                imageVector = if (uiState.selectedTab == TithiAppTab.LIVE_CLOCK) {
                                    Icons.Filled.AccessTimeFilled
                                } else {
                                    Icons.Outlined.AccessTime
                                },
                                contentDescription = "Live Tithi Clock"
                            )
                        },
                        label = { Text("Live Clock") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CosmicMidnight,
                            selectedTextColor = SuryaGoldBright,
                            indicatorColor = SuryaGold,
                            unselectedIconColor = MutedStarlight,
                            unselectedTextColor = MutedStarlight
                        ),
                        modifier = Modifier.testTag("nav_tab_live_clock")
                    )

                    NavigationBarItem(
                        selected = uiState.selectedTab == TithiAppTab.TITHI_ATLAS,
                        onClick = { viewModel.selectTab(TithiAppTab.TITHI_ATLAS) },
                        icon = {
                            Icon(
                                imageVector = if (uiState.selectedTab == TithiAppTab.TITHI_ATLAS) {
                                    Icons.Filled.AutoAwesomeMosaic
                                } else {
                                    Icons.Outlined.AutoAwesomeMosaic
                                },
                                contentDescription = "All 30 Tithis"
                            )
                        },
                        label = { Text("30 Tithis") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CosmicMidnight,
                            selectedTextColor = SuryaGoldBright,
                            indicatorColor = SuryaGold,
                            unselectedIconColor = MutedStarlight,
                            unselectedTextColor = MutedStarlight
                        ),
                        modifier = Modifier.testTag("nav_tab_tithi_atlas")
                    )

                    NavigationBarItem(
                        selected = uiState.selectedTab == TithiAppTab.CALENDAR_AND_WIDGET,
                        onClick = { viewModel.selectTab(TithiAppTab.CALENDAR_AND_WIDGET) },
                        icon = {
                            Icon(
                                imageVector = if (uiState.selectedTab == TithiAppTab.CALENDAR_AND_WIDGET) {
                                    Icons.Filled.CalendarMonth
                                } else {
                                    Icons.Outlined.CalendarMonth
                                },
                                contentDescription = "30-Day Schedule & Widget"
                            )
                        },
                        label = { Text("Calendar & Widget") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CosmicMidnight,
                            selectedTextColor = SuryaGoldBright,
                            indicatorColor = SuryaGold,
                            unselectedIconColor = MutedStarlight,
                            unselectedTextColor = MutedStarlight
                        ),
                        modifier = Modifier.testTag("nav_tab_calendar_widget")
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (uiState.selectedTab) {
                    TithiAppTab.LIVE_CLOCK -> {
                        LiveTithiClockScreen(
                            liveState = uiState.liveTithiState,
                            isLiveMode = uiState.isLiveMode,
                            use24HourFormat = uiState.use24HourFormat,
                            onToggleTimeFormat = viewModel::toggleTimeFormat,
                            onStepTithi = viewModel::stepByTithi,
                            onResetToLive = viewModel::resetToLiveNow,
                            onOpenFullScreenClock = { viewModel.setFullScreenClockMode(true) },
                            onPinWidgetClick = handlePinWidget
                        )
                    }

                    TithiAppTab.TITHI_ATLAS -> {
                        TithiAtlasScreen(
                            allTithis = uiState.allTithis,
                            activeTithiNumber = uiState.liveTithiState.currentTithi.number,
                            pakshaFilter = uiState.pakshaFilter,
                            selectedTithiForDetail = uiState.selectedTithiForDetail,
                            onSelectFilter = viewModel::setPakshaFilter,
                            onSelectTithiDetail = viewModel::selectTithiDetail,
                            onPreviewTithiOnClock = viewModel::previewSpecificTithi
                        )
                    }

                    TithiAppTab.CALENDAR_AND_WIDGET -> {
                        CalendarAndWidgetScreen(
                            liveState = uiState.liveTithiState,
                            forecast30Days = uiState.forecast30Days,
                            widgetStatusMessage = uiState.widgetStatusMessage,
                            onPinWidgetClick = handlePinWidget,
                            onRefreshWidgetClick = handleRefreshWidget,
                            onOpenFullScreenClock = { viewModel.setFullScreenClockMode(true) },
                            onSelectForecastDay = viewModel::previewTimestamp
                        )
                    }
                }
            }
        }
    }
}
