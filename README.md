# ColorPicker

[![JCenter](https://img.shields.io/badge/JCenter-1.0.0-brightgreen.svg)](https://bintray.com/maoqiqi/ColorPicker/colorpicker/_latestVersion)
[![Latest Stable Version](https://api.bintray.com/packages/maoqiqi/ColorPicker/colorpicker/images/download.svg)](https://bintray.com/maoqiqi/ColorPicker/colorpicker/_latestVersion)
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
<img src="/screenshot/Screenshot_3.png" width="280px" />


## Download

Download [the JARs](https://jcenter.bintray.com/com/codearms/maoqiqi/colorpicker) or configure this dependency:

```
implementation 'com.codearms.maoqiqi:colorpicker:1.0.0'
```


## Usage

### ColorWheelView

将ColorWheelView添加到需要的布局文件中：

```
<com.codearms.maoqiqi.colorpicker.ColorWheelView
    android:id="@+id/color_wheel_view"
    android:layout_width="match_parent"
    android:layout_height="320dp"
    app:pointer="@drawable/ic_point" />
```

支持自定义滑块，可以是图片，也可以是自定义shape，同时可以指定大小，不指定使用默认大小。

实现ColorObserver观察者接口并从ColorWheelView订阅颜色更新事件。

```
colorWheelView.subscribe(object : ColorObserver {
     override fun onColor(color: Int, fromUser: Boolean) {
         Log.e("info", "observable,color=$color,${colorHex(color)},fromUser=$fromUser")
     }
 })
```

设置选择器的初始颜色值：

```
colorWheelView.setInitialColor(Color.parseColor("#BFB40015"))
```

### ColorSliderView

ColorSliderView是BrightnessSliderView和AlphaSliderView父类，实现拖动滑块变色。

#### BrightnessSliderView

```
<com.codearms.maoqiqi.colorpicker.BrightnessSliderView
    android:id="@+id/brightness_slider_view"
    android:layout_width="match_parent"
    android:layout_height="36dp"
    android:padding="8dp" />
```

绑定ColorWheelView，颜色变化自动监听：

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

绑定BrightnessSliderView，颜色变化自动监听：

```
alphaSliderView.bindColorWheelView(brightnessSliderView)
```

BrightnessSliderView和AlphaSliderView可以绑定ColorWheelView、AlphaSliderView、BrightnessSliderView。

完整的示例代码请查阅示例。


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