package ca.warp7.android.scouting.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

class EqualRowLayout : ViewGroup {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = suggestedMinimumWidth + paddingLeft + paddingRight
        val desiredHeight = suggestedMinimumHeight + paddingTop + paddingBottom
        val resolvedWidth = resolveIgnoreDesired(desiredWidth, widthMeasureSpec)
        val resolvedHeight = resolveIgnoreDesired(desiredHeight, heightMeasureSpec)
        if (childCount > 0) {
            val rowHeight = resolvedHeight / childCount
            for (i in 0 until childCount) {
                getChildAt(i).also {
                    if (it.visibility != View.GONE) {
                        it.measure(
                                MeasureSpec.makeMeasureSpec(resolvedWidth, MeasureSpec.EXACTLY),
                                MeasureSpec.makeMeasureSpec(rowHeight, MeasureSpec.EXACTLY)
                        )
                    }
                }
            }
        }
        setMeasuredDimension(resolvedWidth, resolvedHeight)
    }

    private fun resolveIgnoreDesired(desiredSize: Int, measureSpec: Int): Int {
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        return when (specMode) {
            MeasureSpec.EXACTLY -> specSize
            MeasureSpec.AT_MOST -> specSize
            MeasureSpec.UNSPECIFIED -> desiredSize
            else -> specSize
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (childCount > 0) {
            val parentHeight = b - t
            val rowHeight = parentHeight / childCount.toDouble()
            for (i in 0 until childCount) {
                getChildAt(i).apply {
                    if (visibility != View.GONE) {
                        layout(0, (i * rowHeight).toInt(), r - l, ((i + 1) * rowHeight).toInt())
                    }
                }
            }
        }
    }

    override fun shouldDelayChildPressedState() = false

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attributeSet: AttributeSet) : super(context, attributeSet)
}