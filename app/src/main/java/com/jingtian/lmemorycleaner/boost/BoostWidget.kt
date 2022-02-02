package com.jingtian.lmemorycleaner.boost

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.graphics.drawable.toBitmap
import com.jingtian.lmemorycleaner.R
import com.jingtian.lmemorycleaner.utils.MeowUtil.getMemoryPercentage
import com.jingtian.lmemorycleaner.utils.toast

class BoostWidget : AppWidgetProvider() {
    companion object {
        const val ACTION_BOOST = "com.jingtian.lmemorycleaner.ACTION_BOOST"
    }

    private val boostDrawable = BoostDrawable(100)
    private fun percentage() = getMemoryPercentage()

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        val awm = AppWidgetManager.getInstance(context)
        action?.let {
            when(it) {
                ACTION_BOOST-> {
                    toast("????")
                }
            }
        }
        super.onReceive(context, intent)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        boostDrawable.update(percentage())

        appWidgetIds?.let { it ->
            for (id in it) {
                val remoteViews = RemoteViews(context?.packageName, R.layout.widget_boost)
                val intent = Intent(ACTION_BOOST)
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id)
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
                remoteViews.setOnClickPendingIntent(R.id.boost_ball, pendingIntent)

                context?.let { c->
                    val awm = AppWidgetManager.getInstance(c)
                    val componentName = ComponentName(c,BoostWidget::class.java)
                    remoteViews.setImageViewBitmap(R.id.boost_ball, boostDrawable.toBitmap())
                    awm.updateAppWidget(awm.getAppWidgetIds(componentName), remoteViews)
                }
            }
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }
}