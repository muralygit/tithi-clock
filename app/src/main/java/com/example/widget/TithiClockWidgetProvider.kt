package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.domain.TithiAstronomyEngine
import com.example.ui.components.createWidgetMoonPhaseBitmap
import kotlin.math.roundToInt

/**
 * Android Home Screen AppWidgetProvider that displays:
 * 1. Live ticking system time (`TextClock`)
 * 2. Current Vedic Tithi (Pradhama, Dvitheeya, Thritheeya, Chathurthi... Pournami, Amavasya)
 * 3. Paksha, Moon illumination %, and Tithi ending time
 * 4. Corresponding rendered Moon Phase image (`ImageView` bitmap)
 */
class TithiClockWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val action = intent.action
        if (action == Intent.ACTION_TIME_CHANGED ||
            action == Intent.ACTION_TIMEZONE_CHANGED ||
            action == Intent.ACTION_DATE_CHANGED
        ) {
            refreshAllWidgets(context)
        }
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val nowMillis = System.currentTimeMillis()
            val liveState = TithiAstronomyEngine.calculateLiveTithiState(nowMillis)
            val moonBitmap = createWidgetMoonPhaseBitmap(
                elongationDeg = liveState.elongationDeg,
                sizePx = 192
            )

            val views = RemoteViews(context.packageName, R.layout.widget_tithi_clock)
            views.setImageViewBitmap(R.id.widget_moon_image, moonBitmap)
            views.setTextViewText(
                R.id.widget_tithi_name,
                "${liveState.currentTithi.fullTitle} (${liveState.currentTithi.sanskritScript})"
            )
            views.setTextViewText(
                R.id.widget_paksha_details,
                "${liveState.illuminationPercent.roundToInt()}% Moon • Ends ${liveState.formattedEndTime}"
            )

            val launchIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                launchIntent,
                pendingIntentFlags
            )
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun refreshAllWidgets(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, TithiClockWidgetProvider::class.java)
            val ids = manager.getAppWidgetIds(componentName)
            if (ids != null && ids.isNotEmpty()) {
                for (id in ids) {
                    updateAppWidget(context, manager, id)
                }
            }
        }

        fun requestPinWidgetToHomeScreen(context: Context): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val appWidgetManager = context.getSystemService(AppWidgetManager::class.java)
                val provider = ComponentName(context, TithiClockWidgetProvider::class.java)
                if (appWidgetManager != null && appWidgetManager.isRequestPinAppWidgetSupported) {
                    return appWidgetManager.requestPinAppWidget(provider, null, null)
                }
            }
            return false
        }
    }
}
