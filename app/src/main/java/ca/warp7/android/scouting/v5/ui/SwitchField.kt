package ca.warp7.android.scouting.v5.ui

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.widget.Button
import android.widget.FrameLayout
import ca.warp7.android.scouting.R
import ca.warp7.android.scouting.v5.entry.DataPoint

/**
 * A Base button for other buttons to extend onto
 * @since v0.2.0
 */

class SwitchField : FrameLayout, BaseFieldWidget {

    override val fieldData: FieldData?

    private val white = ContextCompat.getColor(context, R.color.colorWhite)
    private val gray = ContextCompat.getColor(context, R.color.colorGray)
    private val red = ContextCompat.getColor(context, R.color.colorRed)
    private val lightGreen = ContextCompat.getColor(context, R.color.colorLightGreen)
    private val almostWhite = ContextCompat.getColor(context, R.color.colorAlmostWhite)

    private var isOn = false
    private var button: Button? = null

    constructor(context: Context) : super(context) {
        fieldData = null
    }

    internal constructor(data: FieldData) : super(data.context) {
        fieldData = data

        setBackgroundResource(R.drawable.layer_list_bg_group)

        button = Button(data.context).apply {
            isAllCaps = false
            textSize = 18f
            typeface = Typeface.SANS_SERIF
            stateListAnimator = null
            text = data.modifiedName
            setLines(2)
            setOnClickListener {
                data.scoutingActivity.apply {
                    if (timeEnabled && !isSecondLimit) {
                        actionVibrator?.vibrateAction()
                        entry!!.add(DataPoint(data.typeIndex, if (isOn) 1 else 0, relativeTime))
                        feedSecondLimit()
                        updateControlState()
                        handler.postDelayed({ updateControlState() }, 1000)
                    }
                }
            }
        }.also { addView(it) }

        updateControlState()
    }

    override fun updateControlState() {
        fieldData?.apply {
            val button = button!!
            if (!scoutingActivity.timeEnabled) {
                button.isEnabled = false
                button.setTextColor(gray)
                button.background.setColorFilter(almostWhite, PorterDuff.Mode.MULTIPLY)
            } else {
                button.isEnabled = true
                scoutingActivity.entry?.apply {
                    isOn = count(typeIndex) % 2 != 0
                    if (isOn) {
                        button.setTextColor(white)
                        button.background.setColorFilter(red, PorterDuff.Mode.MULTIPLY)
                    } else {
                        button.setTextColor(lightGreen)
                        button.background.setColorFilter(almostWhite, PorterDuff.Mode.MULTIPLY)
                    }
                }
            }
        }
    }
}
