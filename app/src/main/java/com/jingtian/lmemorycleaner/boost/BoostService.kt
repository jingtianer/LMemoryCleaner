package com.jingtian.lmemorycleaner.boost

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.jingtian.lmemorycleaner.boost.BoostWidget.Companion.ACTION_BOOST
import com.jingtian.lmemorycleaner.utils.MeowUtil.logD
import com.jingtian.lmemorycleaner.utils.newMainCoroutineJob
import java.util.*
import kotlin.concurrent.thread

class BoostService: Service() {
    var timer : Timer? = null
    private var process = 0
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        timer = timer?:Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {

                process = (50+process-1)%50
                sendBroadcast(Intent(ACTION_BOOST).apply {
                    `package` = this@BoostService.packageName
                    putExtra("process", process+1)
                })
            }

        }, 0,100)
        logD {
           "service started"
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
    override fun onBind(p0: Intent?): IBinder? = null
}