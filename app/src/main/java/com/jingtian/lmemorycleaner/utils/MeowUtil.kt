package com.jingtian.lmemorycleaner.utils

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import android.view.View
import androidx.viewbinding.ViewBinding
import com.jingtian.lmemorycleaner.BuildConfig
import kotlinx.coroutines.delay

object MeowUtil {
    //获取内存信息
    fun getMemoryInfo() :ActivityManager.MemoryInfo {
        val am = app.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val info = ActivityManager.MemoryInfo()
        am.getMemoryInfo(info)
        return info
    }

    fun getMemoryPercentage():Float {
        val info = getMemoryInfo()
        return 1 - 1.0f * info.availMem/info.totalMem
    }
    @SuppressLint("QueryPermissionsNeeded")
    suspend fun killBgProcess() {
        newIOTask {
            kotlin.runCatching {
                app.packageManager.getInstalledPackages(0).forEach { packageInfo ->
                    val isSystem =
                        packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
                    if (!isSystem && packageInfo.packageName != app.packageName) {
                        try {
                            (app.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).killBackgroundProcesses(
                                packageInfo.packageName
                            )
                            logD {
                                "killed : ${packageInfo.packageName}"
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()

                        }
                    }
                }
            }
        }
    }
    suspend fun View.gradient(vararg colors: String, mDuration: Long = 600) {
        val size = colors.size
        for (i in 0 until size - 1) {
            this.newGradient(
                Color.parseColor(colors[i]),
                Color.parseColor(colors[i + 1]),
                mDuration
            )
        }
    }
    private val argbEvaluator by lazy { ArgbEvaluator() }
    private suspend fun View.newGradient(from: Int, to: Int, mDuration: Long = 600L) {
        ValueAnimator().apply {
            duration = mDuration
            setFloatValues(0f, 1f)
            addUpdateListener {
                val fraction = it.animatedValue as Float
                val color = argbEvaluator.evaluate(fraction, from, to) as Int
                this@newGradient.setBackgroundColor(color)

            }
            start()
        }
        delay(mDuration)
    }


    //Context相关
    fun Context.startActivity(cls:Class<*>) {
        startActivity(Intent(this, cls))
    }
    //dialog相关
    fun Dialog.tryShow() {
        try {
            if (!isShowing) {
                show()
            }
        } catch (e: Exception) {
        }
    }

    fun Dialog.tryDismiss() {
        try {
            if (isShowing) {
                dismiss()
            }
        } catch (e: Exception) {
        }
    }

    // log
    fun logD(tag: String = "MeowMeowDebug", msg: () -> String) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg())
        }
    }

    fun logE(tag: String = "MeowMeowDebug", msg: () -> String) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, msg())
        }
    }

    fun logI(tag: String = "MeowMeowDebug", msg: () -> String) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, msg())
        }
    }

    fun logW(tag: String = "MeowMeowDebug", msg: () -> String) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, msg())
        }
    }

    fun logWTF(tag: String = "MeowMeowDebug", msg: () -> String) {
        if (BuildConfig.DEBUG) {
            Log.wtf(tag, msg())
        }
    }

    fun logV(tag: String = "MeowMeowDebug", msg: () -> String) {
        if (BuildConfig.DEBUG) {
            Log.v(tag, msg())
        }
    }

    fun Int.asBitMap():Bitmap {
        return BitmapFactory.decodeResource(app.resources, this)
    }

    fun dp2px(dipValue: Float): Int {
        val scale = app.resources.displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }


}