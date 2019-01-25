package ca.warp7.android.scouting.v5.ui

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import ca.warp7.android.scouting.R
import ca.warp7.android.scouting.v5.entry.DataPoint

/**
 * A Base button for other buttons to extend onto
 * @since v0.2.0
 */

class CountedButtonField : FrameLayout, BaseFieldWidget {

    override val fieldData: FieldData?
    private val counter: TextView?
    private val button: Button?

    private val white = ContextCompat.getColor(context, R.color.colorWhite)
    private val almostBlack = ContextCompat.getColor(context, R.color.colorAlmostBlack)
    private val accent = ContextCompat.getColor(context, R.color.colorAccent)

    constructor(context: Context) : super(context) {
        fieldData = null
        counter = null
        button = null
    }

    internal constructor(data: FieldData) : super(data.context) {
        fieldData = data

        button = Button(data.context).apply {
            isAllCaps = false
            textSize = 18f
            typeface = Typeface.SANS_SERIF
            stateListAnimator = null
            elevation = 4f
            text = data.modifiedName
            setLines(2)
            setOnClickListener {
                data.scoutingActivity.apply {
                    if (timeEnabled && !isSecondLimit) {
                        actionVibrator?.vibrateAction()
                        entry!!.add(DataPoint(data.typeIndex, 1, relativeTime))
                        feedSecondLimit()
                        updateControlState()
                        handler.postDelayed({ updateControlState() }, 1000)
                    }
                }
            }
        }.also { addView(it) }

        counter = TextView(data.context).apply {
            text = "0"
            textSize = 18f
            elevation = 10f
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                leftMargin = 24
                topMargin = 16
            }
        }.also { addView(it) }

        updateControlState()
    }

    override fun updateControlState() {
        fieldData?.apply {
            if (!scoutingActivity.timeEnabled) {
                button!!.isEnabled = false
                button.setTextColor(ContextCompat.getColor(context, R.color.colorGray))
            } else {
                button!!.isEnabled = true
                scoutingActivity.entry?.apply {
                    val count = count(typeIndex)
                    counter?.text = count.toString()
                    if (focused(typeIndex)){
                        button.setTextColor(white)
                        button.background.setColorFilter(accent, PorterDuff.Mode.MULTIPLY)
                        counter!!.setTextColor(white)
                    } else {
                        button.setTextColor(accent)
                        button.background.clearColorFilter()
                        counter!!.setTextColor(almostBlack)
                    }
                }
            }
        }
    }
}
