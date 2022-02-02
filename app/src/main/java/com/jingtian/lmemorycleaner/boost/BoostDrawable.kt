package com.jingtian.lmemorycleaner.boost

import android.graphics.*
import android.graphics.drawable.Drawable

interface DrawInterface {
    fun update(percentage: Float)
    fun fluctuate()
}

class Fluctuate {
    private val angularVelocity: Double
    private var initPhase = 0.0 // 0-1
    private var phase = 0.0 // 0-1
    private val amplitude: Int
    val level: Float
    private val precision:Int
    private val fluctuatePrecision:Int
    constructor() {
        angularVelocity = 1.0
        amplitude = 10
        level = 0f
        precision = 100
        fluctuatePrecision = 100
    }

    constructor(
        width: Int,
        height: Int,
        precision:Int = 100,
        fluctuatePrecision:Int = 100,
        level: Float = 0f,
        initPhase: Double = 0.0,
        phase: Double = 0.0
    ) {
        angularVelocity = Math.PI * 2 / width
        amplitude = height / 2
        this.precision = precision
        this.fluctuatePrecision = fluctuatePrecision
        this.initPhase = initPhase
        this.phase = phase
        this.level = level
    }

    private fun getPhase(): Double {
        return (phase + initPhase) * 2 * Math.PI * angularVelocity

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
        phase = 0.0
    }
}

class BoostDrawable(private val fluctuatePrecision: Int = 100) : Drawable(), DrawInterface {
    private val mPaint = Paint().apply {
        color = Color.RED
        flags = flags and Paint.ANTI_ALIAS_FLAG
    }
    private var percentage = 0f
    private val precision = 100
    private val fluctuate by lazy {
        Fluctuate(
            bounds.width(),
            10,
            precision = precision,
            fluctuatePrecision = fluctuatePrecision,
            level = bounds.bottom + percentage * bounds.height()
        )
    }

    override fun draw(p0: Canvas) {
        val dx =  bounds.width() * 1f / precision
        val level = fluctuate.level + 0f
        var left = bounds.left + 0f
        var right = left + dx
        for (i in 0 until precision-1) {
            val top = fluctuate.sin().toFloat()
            p0.drawRect(left, top, right, level, mPaint)
            left = right
            right += dx
        }
        p0.drawRect(bounds.left+0f, level, bounds.right+0f, bounds.bottom+0f, mPaint)
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