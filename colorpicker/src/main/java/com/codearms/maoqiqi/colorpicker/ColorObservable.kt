package com.codearms.maoqiqi.colorpicker

/**
 * Color Observable
 * author: March
 * date: 2020-12-08 21:01
 * version v1.0.0
 */
interface ColorObservable {

    fun subscribe(observer: ColorObserver?)

    fun unsubscribe(observer: ColorObserver?)

    fun getColor(): Int
}