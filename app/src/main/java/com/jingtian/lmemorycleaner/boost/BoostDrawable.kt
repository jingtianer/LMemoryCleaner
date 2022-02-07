package com.jingtian.lmemorycleaner.boost

import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.TextPaint
import com.jingtian.lmemorycleaner.R
import com.jingtian.lmemorycleaner.utils.MeowUtil.dp2px
import com.jingtian.lmemorycleaner.utils.MeowUtil.logD
import com.jingtian.lmemorycleaner.utils.app
import java.util.*
import kotlin.math.min
import kotlin.math.sin


data class Sin(
    val level: Double,
    val amplitude: Double,
    val angularVelocity: Double,
    val initPhase: Double
) {


    fun value(x: Double): Double {
        return level + sin(2 * Math.PI * (angularVelocity * x + initPhase)) * amplitude
    }
}

data class Fluctuate(val updatePrecision: Int, val precision: Int, val sins: Map<Double, Sin>) {
    private var cur_i = 0
    private val T: Double

    init {
        var t = 0.0
        for (sin in sins) {
            t += sin.value.angularVelocity
        }
        T = t
    }

    fun getValue(): Double {
        val old = cur_i / precision.toDouble()
        cur_i = increaseNumber(cur_i, precision)

        for (item in sins) {
            if (old * T <= item.key) {
                return item.value.value(old)
            }
        }
        return 0.0
    }

    private fun increaseNumber(i: Int, p: Int): Int {
        return if (i >= p / T - 1) 0 else i + 1
    }

    private var initProcess = 0
    fun update(initProcess: Int) {
        this.initProcess = (initProcess / T).toInt()
        cur_i = this.initProcess
    }


}

class BoostDrawable(
    private var color: Int,
    private var color1: Int,
    private var colorBg: Int,
    val fluctuatePrecision: Int = 50
) : Drawable() {
    private val mPaint = Paint().apply {
        color = this@BoostDrawable.color
        flags = flags and Paint.ANTI_ALIAS_FLAG
        style = Paint.Style.FILL
        alpha = (225 * 0.85).toInt()
    }

    private val mPaint1 = Paint().apply {
        color = this@BoostDrawable.color1
        flags = flags and Paint.ANTI_ALIAS_FLAG
        style = Paint.Style.FILL
        alpha = (225 * 0.55).toInt()
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
        alpha = (225).toInt()
    }
    private val mPaintShader = Paint().apply {
        color = this@BoostDrawable.colorBg
        flags = flags and Paint.ANTI_ALIAS_FLAG
        style = Paint.Style.FILL
        alpha = (225 * 0.85).toInt()
    }

    private val mPaintText = TextPaint().apply {
        color = Color.WHITE
        flags = flags and Paint.ANTI_ALIAS_FLAG
        textSize = dp2px(12f).toFloat()
        textAlign = Paint.Align.CENTER
    }
    private var percentage = 0f
    private val precision = 50
    private val borderWidth = dp2px(2f)
    private val amplitude by lazy {
        bounds.height() * 0.05
    }
    private val split = 2 / 3.0
    private val fluctuate by lazy {
//        val split = 1-this.split
//        val sin1 = Sin(0.0, amplitude.toDouble() * (split * 2), 1 / (split * 2), 0.0)
//        val sin2 =
//            Sin(0.0, amplitude.toDouble() * ((1 - split) * 2), 1 / ((1 - split) * 2), 1 - 2 * split)
//        Fluctuate(fluctuatePrecision, precision, mapOf(split to sin1, 1.0 to sin2))
        val sin = Sin(0.0, amplitude, 0.75, 0.0)
        Fluctuate(fluctuatePrecision, precision, mapOf(1.0 to sin))
    }
    private val fluctuateComplex by lazy {
//        val split = this.split
//        val sin1 = Sin(0.0, amplitude.toDouble() * (split * 2), 1 / (split * 2), 0.0)
//        val sin2 =
//            Sin(0.0, amplitude.toDouble() * ((1 - split) * 2), 1 / ((1 - split) * 2), 1 - 2 * split)
//        Fluctuate(fluctuatePrecision, precision, mapOf(split to sin1, 1.0 to sin2))

        val sin = Sin(0.0, amplitude, 0.5, 0.5)
        Fluctuate(fluctuatePrecision, precision, mapOf(1.0 to sin))
    }

    private fun getPercentageLevel(): Float {
        return bounds.top + (1 - percentage) * bounds.height()
    }

    override fun onBoundsChange(bounds: Rect?) {
        super.onBoundsChange(bounds)

    }

    private val clipPath by lazy {
        Path().apply {
            addCircle(
                (w / 2).toFloat(),
                (h / 2).toFloat(),
                ((d - dp2px(8f)) / 2).toFloat(),
                Path.Direction.CCW
            )
        }
    }
    private val mCoverBitmap: Bitmap by lazy {
        val tempBitmap = BitmapFactory.decodeResource(
            app.resources,
            R.mipmap.ic_boost_cover
        )
        if (tempBitmap.width != bounds.width().toInt()
            || tempBitmap.height != bounds.height().toInt()
        )
            Bitmap.createScaledBitmap(
                tempBitmap,
                d,
                d,
                true
            ) else tempBitmap
    }
    private val w by lazy { bounds.width() }
    private val h by lazy { bounds.height() }
    private val d by lazy { min(w, h) }
    override fun draw(p0: Canvas) {
        p0.save()
        p0.clipPath(clipPath)
        val dx = d * 1f / precision
        var left = (w - d) / 2f + bounds.left + 0f
        var right = left + dx


        val per = "%.1f%%".format(percentage * 100)
        for (i in 0 until precision) {
            val top = -fluctuate.getValue().toFloat()
            val top1 = -fluctuateComplex.getValue().toFloat()
//            p0.drawRect(left, top1 + getPercentageLevel(), right, bounds.bottom + 0f, mPaint)
            p0.drawRect(left, bounds.top.toFloat(), right, bounds.bottom.toFloat(), mPaintBg)
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

        p0.restore()
        p0.drawBitmap(
            mCoverBitmap,
            (w - d) / 2f + bounds.left + 0f,
            bounds.top.toFloat(),
            mPaintShader
        )
    }

    override fun setAlpha(p0: Int) {

    }

    override fun setColorFilter(p0: ColorFilter?) {
        mPaint.colorFilter = p0
        invalidateSelf()
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
    fun update(percentage: Float, initProcess: Int) {
        this.percentage = percentage
        this.fluctuate.update(initProcess)
        this.fluctuateComplex.update(initProcess)
        updateColor()
        invalidateSelf()
    }

    fun update(percentage: Float) {
        this.percentage = percentage
        updateColor()
        invalidateSelf()
    }

    data class Colors(val color: Int, val color1: Int, val colorBg: Int)

    private val colorConfig = mapOf(
        0.45 to Colors(
            Color.parseColor("#0CDE74"),
            Color.parseColor("#36E88E"),
            Color.parseColor("#ABF8D1")
        ),
        0.65 to Colors(
            Color.parseColor("#FF7215"),
            Color.parseColor("#FF8E24"),
            Color.parseColor("#FFE0C4")
        ),
        1.0 to Colors(
            Color.parseColor("#F54028"),
            Color.parseColor("#FF614C"),
            Color.parseColor("#FFCDC6")
        )
    )

    private fun updateColor() {
        var color:Colors? = null
        for (k in colorConfig.keys) {
            logD {
                "key = $k, percentage = $percentage, true = ${percentage > k}"
            }
            if (percentage <= k) {
                color = colorConfig[k]
                break
            } else {
            }
        }
        color?.let {
            this.color = it.color
            this.color1 = it.color1
            this.colorBg = it.colorBg
            mPaint.color = it.color
            mPaint1.color = it.color1
            mPaintBg.color = it.colorBg
        }

    }

    fun fluctuate() {
//        fluctuate.update()
//        fluctuateComplex.update()
        invalidateSelf()
    }
}