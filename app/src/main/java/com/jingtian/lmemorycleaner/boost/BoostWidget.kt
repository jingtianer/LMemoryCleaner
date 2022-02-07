package com.jingtian.lmemorycleaner.boost

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.graphics.Color
import android.widget.RemoteViews
import androidx.core.graphics.drawable.toBitmap
import com.jingtian.lmemorycleaner.R
import com.jingtian.lmemorycleaner.utils.MeowUtil.dp2px
import com.jingtian.lmemorycleaner.utils.MeowUtil.getMemoryPercentage
import com.jingtian.lmemorycleaner.utils.MeowUtil.logD


class BoostWidget : AppWidgetProvider() {
    companion object {
        const val ACTION_BOOST = "com.jingtian.lmemorycleaner.ACTION_BOOST"
        const val START_ANIMATION = 0
        const val END_ANIMATION = 1
    }

    private val boostDrawable = BoostDrawable(
        Color.parseColor("#0CDE74"),
        Color.parseColor("#36E88E"),
        Color.parseColor("#ABF8D1")
    ).apply {
        setBounds(0, 0, dp2px(60f), dp2px(60f))
    }

    private fun percentage() = getMemoryPercentage()
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        val process = intent?.extras?.get("process") as Int? ?: 0
        action?.let {
            when (it) {
                ACTION_BOOST -> {
                    updateBitmap(context, process)
                }
            }
            super.onReceive(context, intent)
        }
    }

    private fun updateBitmap(context: Context?, process: Int) {
        context ?: logD { "empty Context" }
        context ?: return
        boostDrawable.update(percentage(), process)
        boostDrawable.fluctuate()
        val remoteViews = RemoteViews(context.packageName, R.layout.widget_boost)
        val awm = AppWidgetManager.getInstance(context)
        val cn =
            ComponentName(
                context,
                BoostWidget::class.java
            )
        remoteViews.setImageViewBitmap(
            R.id.boost_ball,
            boostDrawable.toBitmap(
                boostDrawable.bounds.width(),
                boostDrawable.bounds.height()
            )
        )
        awm.updateAppWidget(cn, remoteViews)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    lateinit var mContext: Context
    lateinit var remoteViews: RemoteViews
    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {

        context?.let {
            mContext = it
        }
        remoteViews = RemoteViews(context?.packageName, R.layout.widget_boost)

        logD {
            "onUpdate"
        }
        appWidgetIds?.let { it ->
            for (id in it) {
                val intent = Intent(context, BoostActivity::class.java)
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id)
                val pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
                remoteViews.setOnClickPendingIntent(R.id.boost_ball, pendingIntent)
                appWidgetManager?.updateAppWidget(id, remoteViews)
            }
        }
//        thread {
//            handler.sendMessage(Message.obtain().apply {
//                what = START_ANIMATION
//            })
//        }
        val intent = Intent(context, BoostService::class.java)
        PendingIntent.getService(context, 0, intent, 0)
        context?.startService(intent)
        super.onUpdate(context, appWidgetManager, appWidgetIds)

    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
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


//    private val handler by lazy {
//        object :Handler(Looper.getMainLooper()) {
//            private val DELAY = 100L
//            override fun handleMessage(msg: Message) {
//                super.handleMessage(msg)
//                val awm = AppWidgetManager.getInstance(mContext)
//                val appIds = awm.getAppWidgetIds(
//                    ComponentName(
//                        mContext,
//                        BoostWidget::class.java
//                    )
//                )
//                when (msg.what) {
//                    START_ANIMATION -> {
//                        boostDrawable.update(percentage())
//                        remoteViews.setImageViewBitmap(R.id.boost_ball, boostDrawable.toBitmap(
//                            boostDrawable.bounds.width(),
//                            boostDrawable.bounds.height()))
//                        awm.updateAppWidget(appIds, remoteViews)
//                        logD {
//                            "update"
//                        }
//                        sendMessageDelayed(obtainMessage(START_ANIMATION), DELAY)
//                    }
//                    END_ANIMATION -> {
//                        if (msg.arg1 == -1) {
//                            msg.arg1 = percentage().toInt()
//                        }
//                        boostDrawable.update(msg.arg1.toFloat())
//                        remoteViews.setImageViewBitmap(R.id.boost_ball, boostDrawable.toBitmap())
//                        awm.updateAppWidget(appIds, remoteViews)
//                        msg.arg1--
//                        if (msg.arg1 > -1) {
//                            sendMessageDelayed(msg, DELAY)
//                        }
//                    }
//                    else -> {
//                        logD {
//                            "update else "
//                        }
//                    }
//                }
//            }
//        }
//    }


}