package com.codearms.maoqiqi.colorpicker

/**
 * Color Observer
 * author: March
 * date: 2020-12-08 21:01
 * version v1.0.0
 */
interface ColorObserver {
    fun onColor(color: Int, fromUser: Boolean)
}