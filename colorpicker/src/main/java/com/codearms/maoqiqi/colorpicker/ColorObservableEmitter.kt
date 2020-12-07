package com.codearms.maoqiqi.colorpicker

import android.graphics.Color

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
        for (observer in observers) {
            observer.onColor(color, fromUser)
        }
    }
}