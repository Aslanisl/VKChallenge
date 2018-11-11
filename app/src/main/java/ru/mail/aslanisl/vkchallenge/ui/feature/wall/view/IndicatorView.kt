package ru.mail.aslanisl.vkchallenge.ui.feature.wall.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.AttrRes
import ru.mail.aslanisl.vkchallenge.R

class IndicatorView
@JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        orientation = HORIZONTAL
    }

    private val indicatorHeight by lazy {
        context.resources.getDimensionPixelSize(R.dimen.indicator_height)
    }
    private val indicatorSpacing by lazy {
        context.resources.getDimensionPixelSize(R.dimen.indicator_spacing)
    }

    private var selectedPosition = 0

    fun setItemCount(count: Int){
        removeAllViews()
        selectedPosition = 0
        for (i in 0 until count){
            addView(getIndicatorView(i == count - 1))
        }
        selectItem(selectedPosition)
    }

    fun selectItem(position: Int){
        getChildAt(selectedPosition)?.isEnabled = false
        selectedPosition = position
        getChildAt(selectedPosition)?.isEnabled = true
    }

    private fun getIndicatorView(lastItem: Boolean): View {
        val view = View(context)
        val layoutParams = LinearLayout.LayoutParams(0, indicatorHeight, 1f)
        if (lastItem.not()){
            layoutParams.marginEnd = indicatorSpacing
        }
        view.layoutParams = layoutParams
        view.setBackgroundResource(R.drawable.bg_indicator)
        view.isEnabled = false
        return view
    }
}