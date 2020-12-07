package com.codearms.maoqiqi.colorpicker

interface ColorObserver {
    fun onColor(color: Int, fromUser: Boolean)
}