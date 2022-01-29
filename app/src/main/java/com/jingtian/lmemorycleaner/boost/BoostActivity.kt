package com.jingtian.lmemorycleaner.boost

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.jingtian.lmemorycleaner.databinding.ActivityBoostBinding
import com.jingtian.lmemorycleaner.utils.*
import kotlinx.coroutines.delay

class BoostActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityBoostBinding.inflate(LayoutInflater.from(this))
    }

    @SuppressLint("QueryPermissionsNeeded")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.lottie.start(
            "boost_images",
            "boost_images/data.json",
            repeat = ValueAnimator.REVERSE
        )
        newMainCoroutineJob {
            newIOTask {
                //清理不可以写在这里哦
                kotlin.runCatching {
                    app.packageManager.getInstalledPackages(0).forEach { packageInfo ->
                        val isSystem =
                            packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
                        if (!isSystem && packageInfo.packageName != packageName) {
                            try {
                                (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).killBackgroundProcesses(
                                    packageInfo.packageName
                                )
                                MeowUtil.logD {
                                    "killed : ${packageInfo.packageName}"
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()

                            }
                        }
                    }
                }
            }
            delay(2000)
//            binding.lottie.cancelAnimation()

        }


    }
}