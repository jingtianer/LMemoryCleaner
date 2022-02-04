package com.jingtian.lmemorycleaner.boost

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.graphics.Color
import android.widget.RemoteViews
import androidx.core.graphics.drawable.toBitmap
import com.jingtian.lmemorycleaner.R
import com.jingtian.lmemorycleaner.utils.MeowUtil.dp2px
import com.jingtian.lmemorycleaner.utils.MeowUtil.getMemoryPercentage
import com.jingtian.lmemorycleaner.utils.newMainCoroutineJob
import com.jingtian.lmemorycleaner.utils.toast
import kotlinx.coroutines.delay

class BoostWidget : AppWidgetProvider() {
    companion object {
        const val ACTION_BOOST = "com.jingtian.lmemorycleaner.ACTION_BOOST"
    }

    private val boostDrawable = BoostDrawable(
        Color.parseColor("#99ff66"),
        Color.parseColor("#00cc00"),
        Color.parseColor("#dedede")
    ).apply {
        setBounds(0, 0, dp2px(150f), dp2px(150f))
    }

    private fun percentage() = getMemoryPercentage()
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        action?.let {
            when (it) {
                ACTION_BOOST -> {
                    toast("????")
                }
            }
        }
        super.onReceive(context, intent)
    }

    var running = false

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        boostDrawable.update(percentage())
        boostDrawable.fluctuate()
        appWidgetIds?.let { it ->
            for (id in it) {
                val remoteViews = RemoteViews(context?.packageName, R.layout.widget_boost)
                val intent = Intent(context, BoostActivity::class.java)
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id)
                val pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
                remoteViews.setOnClickPendingIntent(R.id.boost_ball, pendingIntent)
                remoteViews.setImageViewBitmap(
                    R.id.boost_ball,
                    boostDrawable.toBitmap(
                        boostDrawable.bounds.width(),
                        boostDrawable.bounds.height()
                    )
                )
                appWidgetManager?.updateAppWidget(id, remoteViews)
                if (!running) {
                    running = true
                    newMainCoroutineJob {
                        while (running) {
                            delay(40)
//                            boostDrawable.fluctuate()
//                            remoteViews.setImageViewBitmap(
//                                R.id.boost_ball,
//                                boostDrawable.toBitmap(
//                                    boostDrawable.bounds.width(),
//                                    boostDrawable.bounds.height()
//                                )
//                            )
//                            appWidgetManager?.updateAppWidget(id, remoteViews)
                            this@BoostWidget.onUpdate(context, appWidgetManager, appWidgetIds)
                        }
                    }
                }
            }
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
        running = false
    }

    private fun Context.getWidgetsSize(widgetId: Int): Pair<Int, Int> {
        val manager = AppWidgetManager.getInstance(this)
        val isPortrait = this.resources.configuration.orientation == ORIENTATION_PORTRAIT
        val width = manager.getWidgetWidth(isPortrait, widgetId)
        val height = manager.getWidgetHeight(isPortrait, widgetId)
        val widthInPx = dp2px(width + 0f)
        val heightInPx = dp2px(height + 0f)
        return widthInPx to heightInPx

    }

    private fun AppWidgetManager.getWidgetWidth(isPortrait: Boolean, widgetId: Int): Int =

        if (isPortrait) {
            getWidgetSizeInDp(widgetId, AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
        } else {
            getWidgetSizeInDp(widgetId, AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH)
        }

    private fun AppWidgetManager.getWidgetHeight(isPortrait: Boolean, widgetId: Int): Int =
        if (isPortrait) {
            getWidgetSizeInDp(widgetId, AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT)
        } else {
            getWidgetSizeInDp(widgetId, AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)
        }

    private fun AppWidgetManager.getWidgetSizeInDp(widgetId: Int, key: String): Int =
        this.getAppWidgetOptions(widgetId).getInt(key, 0)

}