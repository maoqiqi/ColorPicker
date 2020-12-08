package com.codearms.maoqiqi.colorpicker

import android.graphics.Color

/**
 * Color Observable Emitter
 * author: March
 * date: 2020-12-08 21:01
 * version v1.0.0
 */
class ColorObservableEmitter : ColorObservable {

    private val observers: MutableList<ColorObserver> = mutableListOf()
    private var color = Color.WHITE

    override fun subscribe(observer: ColorObserver?) {
        observer?.let { observers.add(it) }
    }

    override fun unsubscribe(observer: ColorObserver?) {
        observer?.let { observers.remove(observer) }
    }

    override fun getColor(): Int = color

    fun setInitialColor(color: Int) {
        this.color = color
    }

    fun onColor(color: Int, fromUser: Boolean) {
        this.color = color
        observers.forEach { observer -> observer.onColor(color, fromUser) }
    }
}