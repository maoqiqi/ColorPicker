package com.codearms.maoqiqi.colorpicker

import com.codearms.maoqiqi.colorpicker.ColorObserver

interface ColorObservable {

    fun subscribe(observer: ColorObserver?)

    fun unsubscribe(observer: ColorObserver?)

    fun getColor(): Int
}