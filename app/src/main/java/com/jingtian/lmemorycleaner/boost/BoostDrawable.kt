package com.jingtian.lmemorycleaner.boost

import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.TextPaint
import com.jingtian.lmemorycleaner.utils.MeowUtil.dp2px
import java.util.*
import kotlin.math.min
import kotlin.math.sin

interface DrawInterface {
    fun update(percentage: Float)
    fun fluctuate()
}


data class Sin(
    val level: Double,
    val amplitude: Double,
    val angularVelocity: Double,
    val initPhase: Double
) {


    fun value(x: Double): Double {
        return level + sin(2 * Math.PI * angularVelocity * (x + initPhase)) * amplitude
    }
}

data class Fluctuate(val updatePrecision: Int, val precision: Int, val sins: Map<Double, Sin>) {
    private var percentage = 0.0
    fun getValue(): Double {
        val old = percentage
        percentage = increaseNumber(percentage, precision)

        for (item in sins) {
            if (old <= item.key) {
                return item.value.value(old)
            }
        }
        return 0.0
    }

    private fun increaseNumber(i: Double, p: Int): Double {
        return if (i >= 1) 0.0 else i + 1.0 / p
    }
    private var initPercentage = 0.0
    fun update(speed:Int) {
        initPercentage = increaseNumber(initPercentage, -speed)
        percentage = initPercentage
    }


}

class BoostDrawable(
    private val color: Int,
    private val color1: Int,
    private val colorBg: Int,
    private val fluctuatePrecision: Int = 100
) : Drawable(), DrawInterface {
    private val mPaint = Paint().apply {
        color = this@BoostDrawable.color
        flags = flags and Paint.ANTI_ALIAS_FLAG
        style = Paint.Style.FILL
    }

    private val mPaint1 = Paint().apply {
        color = this@BoostDrawable.color1
        flags = flags and Paint.ANTI_ALIAS_FLAG
        style = Paint.Style.FILL
    }

    /**
     * 中心字体高度
     */
    private val mTextHeightCenter: Int by lazy {
        val textRect = Rect()
        mPaintText.getTextBounds("0%", 0, 2, textRect)
        (textRect.bottom - textRect.top) / 2
    }

    private val mPaintBg = Paint().apply {
        color = this@BoostDrawable.colorBg
        flags = flags and Paint.ANTI_ALIAS_FLAG
        style = Paint.Style.FILL
    }
    private val mPaintText = TextPaint().apply {
        color = Color.BLACK
        flags = flags and Paint.ANTI_ALIAS_FLAG
        textSize = dp2px(12f).toFloat()
        textAlign = Paint.Align.CENTER
    }
    private var percentage = 0f
    private val precision = 50
    private val borderWidth = dp2px(2f)
    private val amplitude by lazy {
        bounds.height() * 0.1
    }
    private val split = 2/3.0
    private val fluctuate by lazy {
        val split = 1-this.split
        val sin1 = Sin(0.0, amplitude.toDouble() * (split * 2), 1 / (split * 2), 0.0)
        val sin2 =
            Sin(0.0, amplitude.toDouble() * ((1 - split) * 2), 1 / ((1 - split) * 2), 1 - 2 * split)
        Fluctuate(fluctuatePrecision, precision, mapOf(split to sin1, 1.0 to sin2))
    }
    private val fluctuateComplex by lazy {
        val split = this.split
        val sin1 = Sin(0.0, amplitude.toDouble() * (split * 2), 1 / (split * 2), 0.0)
        val sin2 =
            Sin(0.0, amplitude.toDouble() * ((1 - split) * 2), 1 / ((1 - split) * 2), 1 - 2 * split)
        Fluctuate(fluctuatePrecision, precision, mapOf(split to sin1, 1.0 to sin2))
    }

    private fun getPercentageLevel(): Float {
        return bounds.top + (1 - percentage) * bounds.height()
    }

    override fun onBoundsChange(bounds: Rect?) {
        super.onBoundsChange(bounds)

    }

    private val clipPath by lazy {
        Path().apply {
            addCircle((w / 2).toFloat(), (h / 2).toFloat(), (d / 2).toFloat(), Path.Direction.CCW)
        }
    }

    private val w by lazy { bounds.width() }
    private val h by lazy { bounds.height() }
    private val d by lazy { min(w, h) }
    override fun draw(p0: Canvas) {
        p0.clipPath(clipPath)
        val dx = d * 1f / precision
        var left = (w - d) / 2f + bounds.left + 0f
        var right = left + dx
        val per = "%.2f%%".format(percentage * 100)
        for (i in 0 until precision) {
            val top = -fluctuate.getValue().toFloat()
            val top1 = -fluctuateComplex.getValue().toFloat()
//            p0.drawRect(left, top1 + getPercentageLevel(), right, bounds.bottom + 0f, mPaint)
            p0.drawRect(left, bounds.top.toFloat(), right, getPercentageLevel() + top1, mPaintBg)
            p0.drawRect(
                left,
                getPercentageLevel() + top1,
                right,
                getPercentageLevel() + top,
                mPaint1
            )
            p0.drawRect(left, getPercentageLevel() + top, right, bounds.bottom.toFloat(), mPaint)
            p0.drawText(
                per,
                (bounds.right + bounds.left) / 2f,
                (bounds.bottom + bounds.top) / 2f + mTextHeightCenter,
                mPaintText
            )
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
//        fluctuate.update(60)
//        fluctuateComplex.update(60)
        invalidateSelf()
    }
}