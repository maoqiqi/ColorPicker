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

package com.codearms.maoqiqi.colorpicker.demo

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.codearms.maoqiqi.colorpicker.ColorObserver
import com.codearms.maoqiqi.colorpicker.demo.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.apply {
            colorWheelView.subscribe(observable1)
            colorWheelView.setInitialColor(Color.parseColor("#BFB40015"))

            brightnessSliderView.bindColorWheelView(colorWheelView)
            brightnessSliderView.subscribe(observable2)

            alphaSliderView.bindColorWheelView(brightnessSliderView)
            alphaSliderView.subscribe(observable3)
        }
    }

    private val observable1: ColorObserver = object : ColorObserver {
        override fun onColor(color: Int, fromUser: Boolean) {
            Log.e("info", "observable1,color=$color,${colorHex(color)},fromUser=$fromUser")
        }
    }

    private val observable2: ColorObserver = object : ColorObserver {
        override fun onColor(color: Int, fromUser: Boolean) {
            Log.e("info", "observable2,color=$color,${colorHex(color)},fromUser=$fromUser")
        }
    }

    private val observable3: ColorObserver = object : ColorObserver {
        override fun onColor(color: Int, fromUser: Boolean) {
            Log.e("info", "observable3,color=$color,${colorHex(color)},fromUser=$fromUser")
            binding.view.setBackgroundColor(color)
            binding.editHex.setText(colorHex(color))
        }
    }

    private fun colorHex(color: Int): String {
        val a = Color.alpha(color)
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        return String.format(Locale.getDefault(), "#%02X%02X%02X%02X", a, r, g, b)
    }

    override fun onDestroy() {
        binding.apply {
            alphaSliderView.unsubscribe(brightnessSliderView)
            brightnessSliderView.unbindColorWheelView(colorWheelView)
            alphaSliderView.unsubscribe(observable3)
            brightnessSliderView.unsubscribe(observable2)
            colorWheelView.unsubscribe(observable1)
        }
        super.onDestroy()
    }
}