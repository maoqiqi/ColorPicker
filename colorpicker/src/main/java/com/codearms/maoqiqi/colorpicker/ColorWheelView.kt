/*
 * Copyright [2020] [March]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codearms.maoqiqi.colorpicker

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * HSV color wheel
 * author: March
 * date: 2020-12-08 21:01
 * version v1.0.0
 */
class ColorWheelView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr), ColorObservable {

    private val tag = "ColorWheelView"

    private var centerX = 0f
    private var centerY = 0f
    private var radius = 0f
    private var pointer: Drawable? = null
    private var pointRadius = 0f
    private var pointRect = Rect()
    private val currentPoint = PointF()

    private val huePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val saturationPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val emitter: ColorObservableEmitter = ColorObservableEmitter()

    init {
        val typedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.ColorWheelView)
        pointer = typedArray.getDrawable(R.styleable.ColorWheelView_pointer)
        typedArray.recycle()

        if (pointer == null) pointer = ShapeDrawable(OvalShape()).apply { paint.apply { color = Color.BLACK } }

        pointer?.let {
            pointRadius = if (it.intrinsicWidth > 0 || it.intrinsicHeight > 0) {
                it.intrinsicWidth.coerceAtLeast(it.intrinsicHeight) * 0.5f
            } else 10 * context.resources.displayMetrics.density
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.e(tag, "onSizeChanged(),w=$w,h=$h,oldw=$oldw,oldh=$oldh")
        val width = w - paddingLeft - paddingRight
        val height = h - paddingTop - paddingBottom
        centerX = paddingLeft + width * 0.5f
        centerY = paddingTop + height * 0.5f
        radius = width.coerceAtMost(height) * 0.5f - pointRadius

        val colors = intArrayOf(Color.RED, Color.MAGENTA, Color.BLUE, Color.CYAN, Color.GREEN, Color.YELLOW, Color.RED)
        // 扫描渐变
        huePaint.shader = SweepGradient(centerX, centerY, colors, null)
        // 放射渐变
        saturationPaint.shader = RadialGradient(centerX, centerY, radius, Color.WHITE, Color.TRANSPARENT, Shader.TileMode.CLAMP)

        getPointByColor(getColor())
        emitter.onColor(getColor(), false)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            canvas.drawCircle(centerX, centerY, radius, huePaint)
            canvas.drawCircle(centerX, centerY, radius, saturationPaint)

            pointRect.apply {
                left = (currentPoint.x - pointRadius).toInt()
                top = (currentPoint.y - pointRadius).toInt()
                right = (currentPoint.x + pointRadius).toInt()
                bottom = (currentPoint.y + pointRadius).toInt()
            }

            pointer?.let {
                it.bounds = pointRect
                it.draw(canvas)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                val x = event.x
                val y = event.y
                if (isInCircle(x, y)) {
                    checkPoint(x, y)
                    invalidate()
                    emitter.onColor(getColorByPoint(x, y), true)
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    // 判读一个点是否在圆内
    private fun isInCircle(x: Float, y: Float) = sqrt((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY)) <= radius + pointRadius

    private fun getColorByPoint(x: Float, y: Float): Int {
        val disX: Float = x - centerX
        val disY: Float = y - centerY
        val r = sqrt(disX * disX + disY * disY)
        // 色调范围0-360,饱和度范围0-1,亮度范围0-1
        val hsv = floatArrayOf(0f, 0f, 1f)
        hsv[0] = (atan2(disY, -disX) / Math.PI * 180f).toFloat() + 180
        hsv[1] = 0f.coerceAtLeast(1f.coerceAtMost((r / radius)))
        return Color.HSVToColor(hsv)
    }

    private fun getPointByColor(color: Int) {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        val r = hsv[1] * radius
        val radian = (hsv[0] / 180f * Math.PI).toFloat()
        checkPoint(r * cos(radian) + centerX, -r * sin(radian) + centerY)
    }

    private fun checkPoint(x: Float, y: Float) {
        var disX: Float = x - centerX
        var disY: Float = y - centerY
        val r = sqrt(disX * disX + disY * disY)
        if (r > radius) {
            disX *= radius / r
            disY *= radius / r
        }
        currentPoint.x = disX + centerX
        currentPoint.y = disY + centerY
    }

    fun setInitialColor(color: Int) {
        emitter.setInitialColor(color)
    }

    override fun subscribe(observer: ColorObserver?) {
        emitter.subscribe(observer)
    }

    override fun unsubscribe(observer: ColorObserver?) {
        emitter.unsubscribe(observer)
    }

    override fun getColor(): Int = emitter.getColor()
}