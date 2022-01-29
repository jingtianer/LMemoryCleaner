package com.jingtian.lmemorycleaner

import android.app.Application
import com.jingtian.lmemorycleaner.utils.Utils

class Application: Application() {
    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
    }
}