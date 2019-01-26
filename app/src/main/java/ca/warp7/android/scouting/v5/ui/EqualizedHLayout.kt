package ca.warp7.android.scouting.v5.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

class EqualizedHLayout : ViewGroup {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = suggestedMinimumWidth + paddingLeft + paddingRight
        val desiredHeight = suggestedMinimumHeight + paddingTop + paddingBottom
        setMeasuredDimension(
            resolveIgnoreDesired(desiredWidth, widthMeasureSpec),
            resolveIgnoreDesired(desiredHeight, heightMeasureSpec)
        )
    }

    private fun resolveIgnoreDesired(desiredSize: Int, measureSpec: Int): Int {
        val specMode = View.MeasureSpec.getMode(measureSpec)
        val specSize = View.MeasureSpec.getSize(measureSpec)
        return when (specMode) {
            MeasureSpec.EXACTLY -> specSize
            MeasureSpec.AT_MOST -> specSize
            MeasureSpec.UNSPECIFIED -> desiredSize
            else -> specSize
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val parentHeight = b - t
        val rowHeight = parentHeight / childCount.toDouble()
        for (i in 0 until childCount) {
            getChildAt(i).layout(l, (i * rowHeight).toInt(), r, (i * rowHeight + 1).toInt())
        }
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
}