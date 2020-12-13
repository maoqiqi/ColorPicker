# ColorPicker

[![JCenter](https://img.shields.io/badge/JCenter-1.0.0-brightgreen.svg)](https://bintray.com/maoqiqi/ColorPicker/colorpicker/_latestVersion)
[![Min Sdk Version](https://img.shields.io/badge/API-16%2B-brightgreen.svg)](https://developer.android.com/about/versions/android-4.1.html)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Author](https://img.shields.io/badge/Author-March-orange.svg)](fengqi.mao.march@gmail.com)

English | [中文](README_zh_CN.md)

ColorPicker is a standard Android color selector.


## Catalog

* [Screenshot](#Screenshot)
* [Download](#Download)
* [Usage](#Usage)
* [Description](#Description)
* [About](#About)
* [License](#License)


## Screenshot

<img src="/screenshot/Screenshot_1.png" width="280px" />
<img src="/screenshot/Screenshot_2.png" width="280px" />
<img src="/screenshot/Screenshot_3.gif" width="280px" />


## Download

Download [the JARs](https://jcenter.bintray.com/com/codearms/maoqiqi/colorpicker) or configure this dependency:

```
implementation 'com.codearms.maoqiqi:colorpicker:1.0.0'
```


## Usage

### ColorWheelView

Add the ColorWheelView to the desired layout file：

```
<com.codearms.maoqiqi.colorpicker.ColorWheelView
    android:id="@+id/color_wheel_view"
    android:layout_width="match_parent"
    android:layout_height="320dp"
    app:pointer="@drawable/ic_point" />
```

Supports custom slider, which can be image or shape, and can specify the size, if not the default size.

Implement the ColorObserver observer interface and subscribe to color update events from the ColorWheelView。

```
colorWheelView.subscribe(object : ColorObserver {
     override fun onColor(color: Int, fromUser: Boolean) {
         Log.e("info", "observable,color=$color,${colorHex(color)},fromUser=$fromUser")
     }
 })
```

Sets the initial color value for the selector：

```
colorWheelView.setInitialColor(Color.parseColor("#BFB40015"))
```

### ColorSliderView

ColorSliderView is the BrightnessSliderView and AlphaSliderView parent class，Change color by dragging the slider。

#### BrightnessSliderView

```
<com.codearms.maoqiqi.colorpicker.BrightnessSliderView
    android:id="@+id/brightness_slider_view"
    android:layout_width="match_parent"
    android:layout_height="36dp"
    android:padding="8dp" />
```

Bind ColorWheelView to automatically listen for color changes：

```
brightnessSliderView.bindColorWheelView(colorWheelView)
```

#### AlphaSliderView

```
<com.codearms.maoqiqi.colorpicker.AlphaSliderView
    android:id="@+id/alpha_slider_view"
    android:layout_width="match_parent"
    android:layout_height="36dp"
    android:padding="8dp"
    app:slider="@drawable/shape_slider" />
```

Bind BrightnessSliderView to automatically listen for color changes：

```
alphaSliderView.bindColorWheelView(brightnessSliderView)
```

BrightnessSliderView and AlphaSliderView can be tied to ColorWheelView, AlphaSliderView, and BrightnessSliderView.


## License

```
   Copyright [2020] [March]

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```