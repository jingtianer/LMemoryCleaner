package com.jingtian.lmemorycleaner.utils

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.jingtian.lmemorycleaner.BuildConfig

object MeowUtil {
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


}