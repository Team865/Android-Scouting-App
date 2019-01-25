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

    private val white: Int
    private val almostBlack: Int
    private val accent: Int


    constructor(context: Context) : super(context) {
        fieldData = null
        counter = null
        button = null
        white = ContextCompat.getColor(context, R.color.colorWhite)
        almostBlack = ContextCompat.getColor(context, R.color.colorAlmostBlack)
        accent = ContextCompat.getColor(context, R.color.colorAccent)
    }

    internal constructor(data: FieldData) : super(data.context) {
        fieldData = data
        white = ContextCompat.getColor(context, R.color.colorWhite)
        almostBlack = ContextCompat.getColor(context, R.color.colorAlmostBlack)
        accent = ContextCompat.getColor(context, R.color.colorAccent)

        button = Button(data.context).apply {
            isAllCaps = false
            textSize = 18f
            typeface = Typeface.SANS_SERIF
            stateListAnimator = null
            elevation = 4f
            text = data.modifiedName
            setLines(2)
        }.also { addView(it) }

        counter = TextView(data.context).apply {
            textSize = 15f
            elevation = 10f
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                leftMargin = 24
                topMargin = 16
            }
        }.also { addView(it) }

        setOnClickListener {
            data.scoutingActivity.apply {
                if (!isSecondLimit) {
                    actionVibrator?.vibrateAction()
                    entry!!.add(DataPoint(data.typeIndex, 1, currentTime))
                    updateControlState()
                    handler.postDelayed({ updateControlState() }, 1000)
                }
            }
        }
    }

    override fun updateControlState() {
        fieldData?.apply {
            if (!scoutingActivity.timeEnabled) {
                isEnabled = false
                button!!.setTextColor(ContextCompat.getColor(context, R.color.colorGray))
            } else {
                isEnabled = true
                scoutingActivity.entry?.apply {
                    val count = count(typeIndex)
                    counter?.text = count.toString()
                    if (focused(typeIndex)){
                        button!!.setTextColor(white)
                        button.background.setColorFilter(accent, PorterDuff.Mode.CLEAR)

                        counter!!.setTextColor(white)
                    } else {
                        button!!.setTextColor(accent)
                        button.background.clearColorFilter()
                        counter!!.setTextColor(almostBlack)
                    }
                }
            }
        }
    }
}
