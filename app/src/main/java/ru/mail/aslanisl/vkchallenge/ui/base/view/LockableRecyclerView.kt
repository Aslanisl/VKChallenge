package ru.mail.aslanisl.vkchallenge.ui.base.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.annotation.AttrRes
import androidx.recyclerview.widget.RecyclerView

class LockableRecyclerView
@JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {
    var scrollable = true

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return when (ev.action) {
            MotionEvent.ACTION_DOWN -> scrollable && super.onTouchEvent(ev)
            else -> super.onTouchEvent(ev)
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return scrollable && super.onInterceptTouchEvent(ev)
    }
}