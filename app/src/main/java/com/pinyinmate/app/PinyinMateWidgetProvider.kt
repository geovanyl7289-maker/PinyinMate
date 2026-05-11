package com.pinyinmate.app

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews

class PinyinMateWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            val intent = Intent(context, MainActivity::class.java).apply {
                putExtra(MainActivity.EXTRA_QUICK_CONVERT, true)
            }
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
            val pendingIntent = PendingIntent.getActivity(context, 1001, intent, flags)
            val views = RemoteViews(context.packageName, R.layout.widget_pinyinmate).apply {
                setOnClickPendingIntent(R.id.widget_root, pendingIntent)
                setOnClickPendingIntent(R.id.widget_quick_convert, pendingIntent)
            }
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
