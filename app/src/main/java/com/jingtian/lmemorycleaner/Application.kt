package com.jingtian.lmemorycleaner

import android.annotation.SuppressLint
import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import com.jingtian.lmemorycleaner.boost.BoostService
import com.jingtian.lmemorycleaner.utils.Utils

class Application: Application() {
    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
        val intent = Intent(this, BoostService::class.java)
        PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_NO_CREATE)
        this.startService(intent)
    }
}