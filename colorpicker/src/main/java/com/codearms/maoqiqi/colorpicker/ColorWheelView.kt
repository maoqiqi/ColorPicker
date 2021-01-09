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
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.drawable.DrawableCompat
import kotlin.math.*

/**
 * HSV color wheel
 * author: March
 * date: 2020-12-08 21:01
 * version v1.0.0
 */
class ColorWheelView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr), ColorObservable {

    private val colors = intArrayOf(Color.RED, Color.MAGENTA, Color.BLUE, Color.CYAN, Color.GREEN, Color.YELLOW, Color.RED)
    private val n = 2
    private var centerX = 0f
    private var centerY = 0f
    private var radius = 0f
    private var style = 0
    private var pointer: Drawable? = null
    private var strokeColor = Color.BLACK
    private var outsideTouch: Boolean = false
    private var pointRadius = 0f
    private var pointRect = Rect()
    private val currentPoint = PointF()

    private val colorsPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val saturationPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val pointPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var isMoving = false

    private val emitter: ColorObservableEmitter = ColorObservableEmitter()

    init {
        val typedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.ColorWheelView)
        style = typedArray.getInteger(R.styleable.ColorWheelView_style, style)
        pointer = typedArray.getDrawable(R.styleable.ColorWheelView_pointer)
        strokeColor = typedArray.getColor(R.styleable.ColorWheelView_strokeColor, strokeColor)
        outsideTouch = typedArray.getBoolean(R.styleable.ColorWheelView_outsideTouch, outsideTouch)
        typedArray.recycle()

        if (pointer == null) pointer = ShapeDrawable(OvalShape()).apply { paint.color = Color.BLACK }

        pointer?.let {
            pointRadius = if (it.intrinsicWidth > 0 || it.intrinsicHeight > 0) {
                it.intrinsicWidth.coerceAtLeast(it.intrinsicHeight) * 0.5f
            } else 10 * context.resources.displayMetrics.density
        }

        if (isStroke()) {
            colorsPaint.style = Paint.Style.STROKE
            colorsPaint.strokeWidth = 2 * pointRadius
            pointPaint.color = strokeColor
            pointPaint.style = Paint.Style.STROKE
            pointPaint.strokeWidth = pointRadius
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val width = w - paddingLeft - paddingRight
        val height = h - paddingTop - paddingBottom
        centerX = paddingLeft + width * 0.5f
        centerY = paddingTop + height * 0.5f
        radius = width.coerceAtMost(height) * 0.5f - pointRadius
        if (isStroke()) radius -= pointRadius * 0.5f

        // 扫描渐变
        colorsPaint.shader = SweepGradient(centerX, centerY, colors, null)
        // 放射渐变
        saturationPaint.shader = RadialGradient(centerX, centerY, radius, Color.WHITE, Color.TRANSPARENT, Shader.TileMode.CLAMP)

        // 色调范围0-360,饱和度范围0-1,亮度范围0-1
        val hsv = FloatArray(3)
        Color.colorToHSV(getColor(), hsv)
        val r = if (isStroke()) radius else hsv[1] * radius
        val radian = Math.toRadians(hsv[0].toDouble()).toFloat()
        checkPoint(r * cos(radian) + centerX, -r * sin(radian) + centerY)
        emitter.onColor(getColorByPoint(currentPoint.x, currentPoint.y), false)
        if (isStroke()) {
            pointer?.let { DrawableCompat.setTint(it, getColor()) }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            canvas.drawCircle(centerX, centerY, radius, colorsPaint)
            canvas.drawCircle(centerX, centerY, radius, saturationPaint)

            pointRect.apply {
                left = (currentPoint.x - pointRadius).toInt()
                top = (currentPoint.y - pointRadius).toInt()
                right = (currentPoint.x + pointRadius).toInt()
                bottom = (currentPoint.y + pointRadius).toInt()
            }

            if (isStroke()) canvas.drawCircle(currentPoint.x, currentPoint.y, pointRadius, pointPaint)
            pointer?.let {
                it.bounds = pointRect
                it.draw(canvas)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                val x = event.x
                val y = event.y
                isMoving = isInCircle(x, y, true)
                if (isMoving) {
                    checkPoint(x, y)
                    invalidate()
                    emitter.onColor(getColorByPoint(x, y), true)
                    if (isStroke()) pointer?.let { DrawableCompat.setTint(it, getColor()) }
                }
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val x = event.x
                val y = event.y
                if (isMoving && isInCircle(x, y, false)) {
                    checkPoint(x, y)
                    invalidate()
                    emitter.onColor(getColorByPoint(x, y), true)
                    if (isStroke()) pointer?.let { DrawableCompat.setTint(it, getColor()) }
                }
                return true
            }
            MotionEvent.ACTION_UP -> {
                isMoving = false
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun isStroke() = style == 1

    // 判断一个点是否在圆环内
    private fun isInCircle(x: Float, y: Float, isDown: Boolean) =
        outsideTouch || sqrt((x - centerX).pow(n) + (y - centerY).pow(n)) in getStartPosition(isDown)..radius + pointRadius

    private fun getStartPosition(isDown: Boolean) = if (isDown && isStroke()) radius - 2 * pointRadius else 0f

    private fun getColorByPoint(x: Float, y: Float): Int {
        val disX: Float = x - centerX
        val disY: Float = y - centerY
        val r = sqrt(disX.pow(n) + disY.pow(n))
        // 色调范围0-360,饱和度范围0-1,亮度范围0-1
        val hsv = floatArrayOf(0f, 0f, 1f)
        hsv[0] = (atan2(disY, -disX) / Math.PI * 180f).toFloat() + 180
        hsv[1] = if (isStroke()) 1f else 0f.coerceAtLeast(1f.coerceAtMost((r / radius)))
        return Color.HSVToColor(hsv)
    }

    private fun checkPoint(x: Float, y: Float) {
        var disX: Float = x - centerX
        var disY: Float = y - centerY
        val r = sqrt(disX.pow(n) + disY.pow(n))
        if (isStroke() || r > radius) {
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