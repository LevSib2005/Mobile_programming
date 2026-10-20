package com.example.mobile_programming.presentation.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.mobile_programming.MyApplication
import com.example.mobile_programming.R
import com.example.mobile_programming.domain.models.GoldRate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class GoldRateWidgetProvider : AppWidgetProvider() {

    companion object {
        const val ACTION_REFRESH = "com.example.mobile_programming.widget.REFRESH_GOLD"

        fun updateViews(
            context: Context,
            manager: AppWidgetManager,
            appWidgetId: Int,
            rate: GoldRate?
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_gold_rate)
            val text = if (rate != null) {
                context.getString(R.string.widget_gold_rate, formatRate(rate.valueRubPerGram))
            } else {
                context.getString(R.string.widget_gold_loading)
            }
            views.setTextViewText(R.id.tvWidgetRate, text)

            val refreshIntent = Intent(context, GoldRateWidgetProvider::class.java).apply {
                action = ACTION_REFRESH
            }
            val pending = PendingIntent.getBroadcast(
                context,
                appWidgetId,
                refreshIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widgetRoot, pending)
            manager.updateAppWidget(appWidgetId, views)
        }

        private fun formatRate(value: Double): String {
            val format = NumberFormat.getIntegerInstance(Locale.forLanguageTag("ru-RU"))
            return format.format(value.toLong())
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val pending = goAsync()
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        scope.launch {
            try {
                appWidgetIds.forEach { id ->
                    updateViews(context, appWidgetManager, id, null)
                }
                val app = context.applicationContext as MyApplication
                val rate = try {
                    app.goldRateRepository.loadRate()
                } catch (_: Exception) {
                    null
                }
                appWidgetIds.forEach { id ->
                    updateViews(context, appWidgetManager, id, rate)
                }
            } finally {
                pending.finish()
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_REFRESH) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(
                ComponentName(context, GoldRateWidgetProvider::class.java)
            )
            if (ids.isNotEmpty()) {
                onUpdate(context, manager, ids)
            }
        }
    }
}