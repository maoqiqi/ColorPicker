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

import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet

/**
 * Brightness Color Slider View
 * author: March
 * date: 2020-12-08 21:01
 * version v1.0.0
 */
class BrightnessSliderView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ColorSliderView(context, attrs, defStyleAttr) {

    override fun configurePaint(color: Int, paint: Paint) {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hsv[2] = 0f
        val startColor = Color.HSVToColor(hsv)
        hsv[2] = 1f
        val endColor = Color.HSVToColor(hsv)
        val rect = getSeekBarRect()
        paint.shader = LinearGradient(rect.left, 0f, rect.right, 0f, startColor, endColor, Shader.TileMode.CLAMP)
    }

    override fun getColorByColor(color: Int, percentage: Float): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hsv[2] = percentage
        return Color.HSVToColor(hsv)
    }

    override fun getPercentByColor(color: Int): Float {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        return hsv[2]
    }
}