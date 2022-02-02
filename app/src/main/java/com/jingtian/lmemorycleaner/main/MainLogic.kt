package com.jingtian.lmemorycleaner.main

import android.content.Context
import android.graphics.Color
import androidx.core.graphics.drawable.toDrawable
import com.jingtian.lmemorycleaner.R
import com.jingtian.lmemorycleaner.bean.MainFunctionsBean
import com.jingtian.lmemorycleaner.boost.BoostActivity
import com.jingtian.lmemorycleaner.boost.BoostDrawable
import com.jingtian.lmemorycleaner.utils.MeowUtil
import com.jingtian.lmemorycleaner.utils.MeowUtil.asBitMap
import com.jingtian.lmemorycleaner.utils.MeowUtil.startActivity
import com.jingtian.lmemorycleaner.utils.app
import com.jingtian.lmemorycleaner.utils.toast

class MainLogic(private val context: Context) : MainContract.Presenter {
    var view: MainContract.View? = null
    override fun bind(v: MainContract.View) {
        view = v
    }

    override fun unBind() {
        view = null
    }

    override fun getFunctionBean(): List<MainFunctionsBean> {
        return listOf(
            MainFunctionsBean(
                "清理",
                R.mipmap.main_app_clean.asBitMap().toDrawable(app.resources),
                R.mipmap.main_app_clean
            ),
            MainFunctionsBean(
                "省电",
                R.mipmap.main_battery_saver.asBitMap().toDrawable(app.resources),
                R.mipmap.main_battery_saver
            ),
            MainFunctionsBean(
                "降温",
                R.mipmap.main_cpu_cooler.asBitMap().toDrawable(app.resources),
                R.mipmap.main_cpu_cooler
            ),
            MainFunctionsBean(
                "加速",
                BoostDrawable(Color.GREEN).apply {
                    update(MeowUtil.getMemoryPercentage())
                },
                R.mipmap.main_phone_boost
            )
        )
    }

    override fun startFunction(id: Int) {
        when (id) {
            R.mipmap.main_app_clean -> {
                toast("开发中")
            }
            R.mipmap.main_battery_saver -> {
                toast("开发中")
            }
            R.mipmap.main_cpu_cooler -> {
                toast("开发中")
            }
            R.mipmap.main_phone_boost -> {
                context.startActivity(BoostActivity::class.java)

            }
        }
    }


}