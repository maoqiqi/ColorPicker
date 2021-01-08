package com.codearms.maoqiqi.colorpicker

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View

/**
 * Shine
 * author: March
 * date: 2020-12-08 21:01
 * version v1.0.0
 */
class BlurMaskView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr), ColorObserver {

    private var centerX = 0f
    private var centerY = 0f
    private var radius = 0f
    private var light: Drawable? = null
    private var lightRect = Rect()

    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.GREEN }

    init {
        val typedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.BlurMaskView)
        light = typedArray.getDrawable(R.styleable.BlurMaskView_light)
        typedArray.recycle()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val width = w - paddingLeft - paddingRight
        val height = h - paddingTop - paddingBottom
        centerX = paddingLeft + width * 0.5f
        centerY = paddingTop + height * 0.5f
        radius = width.coerceAtMost(height) * 0.25f

        light?.let { drawable: Drawable ->
            if (drawable.intrinsicWidth > 0 && drawable.intrinsicHeight > 0) lightRect.apply {
                left = (centerX - drawable.intrinsicWidth / 2).toInt()
                top = (centerY - drawable.intrinsicHeight / 2).toInt()
                right = (centerX + drawable.intrinsicWidth / 2).toInt()
                bottom = (centerY + drawable.intrinsicHeight / 2).toInt()
            }
        }

        paint.maskFilter = BlurMaskFilter(radius, BlurMaskFilter.Blur.SOLID)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            canvas.drawCircle(centerX, centerY, radius, paint)
            light?.let {
                it.bounds = lightRect
                it.draw(canvas)
            }
        }
    }

    fun setInitialColor(color: Int) {
        paint.color = color
    }

    override fun onColor(color: Int, fromUser: Boolean) {
        paint.color = color
        invalidate()
    }
}