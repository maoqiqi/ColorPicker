package com.codearms.maoqiqi.colorpicker

import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet

/**
 * Alpha Color Slider View
 * author: March
 * date: 2020-12-08 21:01
 * version v1.0.0
 */
class AlphaSliderView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ColorSliderView(context, attrs, defStyleAttr) {

    override fun configurePaint(color: Int, paint: Paint) {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        val startColor = Color.HSVToColor(0, hsv)
        val endColor = Color.HSVToColor(255, hsv)
        val rect = getSeekBarRect()
        paint.shader = LinearGradient(rect.left, 0f, rect.right, 0f, startColor, endColor, Shader.TileMode.CLAMP)
    }

    override fun getColorByColor(color: Int, percentage: Float): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        val alpha = (percentage * 255).toInt()
        return Color.HSVToColor(alpha, hsv)
    }

    override fun getPercentByColor(color: Int): Float = Color.alpha(color) / 255f
}