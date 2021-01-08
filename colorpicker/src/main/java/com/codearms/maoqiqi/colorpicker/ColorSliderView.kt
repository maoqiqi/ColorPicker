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
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.toRect

/**
 * Color Slider View
 * author: March
 * date: 2020-12-08 21:01
 * version v1.0.0
 */
abstract class ColorSliderView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr), ColorObserver, ColorObservable {

    private var baseColor = Color.WHITE
    private var seekBarHeight = 0f
    private var sliderWidth = 0f
    private var sliderHeight = 0f
    private var currentPercentage = 0f
    private var isBindColorWheelView = false
    private var slider: Drawable? = null
    private var reversal: Boolean = false

    private val seekBarRect = RectF()
    private val sliderRect = RectF()

    private val seekBarPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val emitter: ColorObservableEmitter = ColorObservableEmitter()

    init {
        val typedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.ColorSliderView)
        slider = typedArray.getDrawable(R.styleable.ColorSliderView_slider)
        reversal = typedArray.getBoolean(R.styleable.ColorSliderView_reversal, false)
        typedArray.recycle()
    }

    fun getSeekBarRect(): RectF = seekBarRect

    fun isReversal(): Boolean = reversal

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // 默认值
        seekBarHeight = (h - paddingTop - paddingBottom) * 0.5f
        sliderWidth = seekBarHeight
        sliderHeight = 2 * seekBarHeight

        if (slider == null) {
            val radius = seekBarHeight * 0.25f
            val outerRadii = floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius)
            slider = ShapeDrawable(RoundRectShape(outerRadii, null, null)).apply { paint.color = Color.BLACK }
        }

        slider?.let {
            if (it.intrinsicWidth > 0) sliderWidth = it.intrinsicWidth.toFloat()
            if (it.intrinsicHeight > 0) sliderHeight = it.intrinsicHeight.toFloat()
        }

        val disW = sliderWidth * 0.5f
        val disH = (height - seekBarHeight) * 0.5f

        // 滚动条位置
        seekBarRect.left = paddingLeft + disW
        seekBarRect.top = disH
        seekBarRect.right = width - paddingRight - disW
        seekBarRect.bottom = height - disH

        configurePaint(baseColor, seekBarPaint)
        currentPercentage = getPercentByColor(baseColor)
        // 滑块默认位置
        val x = (width - paddingLeft - paddingRight - sliderWidth) * currentPercentage
        val y = (height - sliderHeight) * 0.5f
        sliderRect.left = paddingLeft + x
        sliderRect.top = y
        sliderRect.right = paddingLeft + sliderWidth + x
        sliderRect.bottom = height - y
        if (!isBindColorWheelView) emitter.onColor(baseColor, false)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            canvas.drawRect(seekBarRect, seekBarPaint)
            slider?.let {
                it.bounds = sliderRect.toRect()
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
                if (isInRectangle(x, y)) {
                    checkPoint(x)
                    invalidate()
                    emitter.onColor(getColorByPercentage(baseColor, currentPercentage), true)
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun isInRectangle(x: Float, y: Float) =
        x >= paddingLeft && x <= width - paddingRight && y >= paddingTop && y <= height - paddingBottom

    private fun checkPoint(x: Float) {
        var currentX = x
        val halfSliderWidth = sliderWidth * 0.5f
        currentX = currentX.coerceAtLeast(paddingLeft + halfSliderWidth)
        currentX = currentX.coerceAtMost(width - paddingRight - halfSliderWidth)
        sliderRect.left = currentX - halfSliderWidth
        sliderRect.right = currentX + halfSliderWidth
        currentPercentage = (currentX - paddingLeft - halfSliderWidth) / (width - paddingLeft - paddingRight - sliderWidth)
    }

    fun setInitialColor(color: Int) {
        this.baseColor = color
        emitter.setInitialColor(baseColor)
    }

    override fun onColor(color: Int, fromUser: Boolean) {
        this.baseColor = color
        configurePaint(baseColor, seekBarPaint)
        if (fromUser) {
            emitter.onColor(getColorByPercentage(baseColor, currentPercentage), fromUser)
        } else {
            currentPercentage = getPercentByColor(baseColor)
            emitter.onColor(baseColor, fromUser)
        }
        invalidate()
    }

    // 绑定ColorWheelView
    fun bindColorWheelView(colorObservable: ColorObservable?) {
        isBindColorWheelView = true
        colorObservable?.subscribe(this)
    }

    // 解绑ColorWheelView
    fun unbindColorWheelView(colorObservable: ColorObservable?) {
        colorObservable?.unsubscribe(this)
    }

    override fun subscribe(observer: ColorObserver?) {
        emitter.subscribe(observer)
    }

    override fun unsubscribe(observer: ColorObserver?) {
        emitter.unsubscribe(observer)
    }

    override fun getColor(): Int = emitter.getColor()

    abstract fun configurePaint(color: Int, paint: Paint)

    abstract fun getColorByPercentage(color: Int, percentage: Float): Int

    abstract fun getPercentByColor(color: Int): Float
}