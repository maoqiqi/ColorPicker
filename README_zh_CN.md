# ColorPicker

[![JCenter](https://img.shields.io/badge/JCenter-1.0.0-brightgreen.svg)](https://bintray.com/maoqiqi/ColorPicker/colorpicker/_latestVersion)
[![Min Sdk Version](https://img.shields.io/badge/API-16%2B-brightgreen.svg)](https://developer.android.com/about/versions/android-4.1.html)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Author](https://img.shields.io/badge/Author-March-orange.svg)](fengqi.mao.march@gmail.com)

[English](README.md) | 中文

ColorPicker 一款标准的Android颜色选择器。


## 目录

* [截图](#截图)
* [下载](#下载)
* [用法](#用法)
* [Description](#Description)
* [About](#About)
* [开源协议](#开源协议)


## 截图

<img src="/screenshot/Screenshot_1.png" width="280px" />
<img src="/screenshot/Screenshot_2.png" width="280px" />
<img src="/screenshot/Screenshot_3.png" width="280px" />


## 下载

下载[JAR包](https://jcenter.bintray.com/com/codearms/maoqiqi/colorpicker)或配置此依赖项:

```
implementation 'com.codearms.maoqiqi:colorpicker:1.0.0'
```


## 用法

### ColorWheelView

将ColorWheelView添加到需要的布局文件中：

```
<com.codearms.maoqiqi.colorpicker.ColorWheelView
    android:id="@+id/color_wheel_view"
    android:layout_width="match_parent"
    android:layout_height="320dp"
    app:pointer="@drawable/ic_point" />
```

支持自定义滑块，可以是图片，也可以是自定义shape，同时可以指定大小，如果不指定使用默认大小。

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


## 开源协议

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