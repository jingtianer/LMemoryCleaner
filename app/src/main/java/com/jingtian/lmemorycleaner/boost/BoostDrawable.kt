package com.jingtian.lmemorycleaner.boost

import android.graphics.*
import android.graphics.drawable.Drawable
import com.jingtian.lmemorycleaner.utils.MeowUtil.dp2px
import com.jingtian.lmemorycleaner.utils.MeowUtil.logD
import java.util.*

interface DrawInterface {
    fun update(percentage: Float)
    fun fluctuate()
}

class Fluctuate {
    private var initPhase = 0.0 // 0-1
    private var phase = 0.0 // 0-1
    private val amplitude: Int
    val level: Float
    private val precision:Int
    private val fluctuatePrecision:Int
    constructor() {
        amplitude = 10
        level = 0f
        precision = 100
        fluctuatePrecision = 100
    }

    constructor(
        height: Int,
        precision:Int = 100,
        fluctuatePrecision:Int = 100,
        level: Float = 0f,
        initPhase: Double = 0.0,
        phase: Double = 0.0
    ) {
        amplitude = height / 2
        this.precision = precision
        this.fluctuatePrecision = fluctuatePrecision
        this.initPhase = initPhase
        this.phase = phase
        this.level = level
    }

    private fun getPhase(): Double {
        return (phase + initPhase) * 2 * Math.PI

    }

    fun sin(): Double {
        val d = level + amplitude * kotlin.math.sin(getPhase())
        if (phase >= 1 - 1.0/precision) {
            phase = 0.0
        } else {
            phase += 1.0/precision
        }
        return d
    }

    fun updateInitPhase() {
        if (initPhase >= 1 - 1/fluctuatePrecision) {
            initPhase = 0.0
        } else {
            initPhase += 1/fluctuatePrecision
        }
        initPhase = 0.0
    }
}

class BoostDrawable(private val color:Int ,private val fluctuatePrecision: Int = 100) : Drawable(), DrawInterface {
    private val mPaint = Paint().apply {
        color = this@BoostDrawable.color
        flags = flags and Paint.ANTI_ALIAS_FLAG
        style = Paint.Style.FILL
    }
//    var timer = Timer().apply {
//        scheduleAtFixedRate(object : TimerTask() {
//            override fun run() {
//                fluctuate()
//            }
//
//        },0,50)
//    }
    private var percentage = 0f
    private val precision = 100
    private val amplitude = dp2px(10f)
    private val fluctuate by lazy {
        Fluctuate(
            amplitude,
            precision = precision,
            fluctuatePrecision = fluctuatePrecision,
            level = bounds.top + percentage * bounds.height() + amplitude*2
        )
    }


    override fun draw(p0: Canvas) {
        val dx =  bounds.width() * 1f / precision
        val level = fluctuate.level + 0f
        var left = bounds.left + 0f
        var right = left + dx
        logD {
            "bottom = ${bounds.bottom}, top = ${bounds.top}"
        }
        for (i in 0 until precision-1) {
            val top = fluctuate.sin().toFloat()
            logD {
                "dx = $dx, level = $level, left = $left, right = $right, top = $top"
            }

            p0.drawRect(left, top, right, bounds.bottom.toFloat(), mPaint)
            left = right
            right += dx
        }
//        p0.drawRect(bounds.left+0f, level, bounds.right+0f, bounds.bottom+0f, mPaint)
    }

    override fun setAlpha(p0: Int) {
        mPaint.alpha = p0
        invalidateSelf()
    }

    override fun setColorFilter(p0: ColorFilter?) {
        mPaint.colorFilter = p0
        invalidateSelf()
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
    override fun update(percentage: Float) {
        this.percentage = percentage
        invalidateSelf()
    }

    override fun fluctuate() {
        fluctuate.updateInitPhase()
        invalidateSelf()
    }
}