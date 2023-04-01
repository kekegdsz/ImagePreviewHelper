package com.ky.android.photo.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class NoScrollViewPager : ViewPager {
    private val TAG = NoScrollViewPager::class.java.simpleName

    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
    }

    override fun setCurrentItem(item: Int, smoothScroll: Boolean) {
        super.setCurrentItem(item, smoothScroll)
    }

    override fun setCurrentItem(item: Int) {
        super.setCurrentItem(item, false)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        try {
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> {}
                MotionEvent.ACTION_MOVE -> {}
                MotionEvent.ACTION_UP -> {}
            }
            return super.onInterceptTouchEvent(ev)
        } catch (ex: IllegalArgumentException) {
            Log.w(TAG, "onInterceptTouchEvent() ", ex)
            ex.printStackTrace()
        }
        return false
    }
}