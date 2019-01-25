package ca.warp7.android.scouting.v5.ui

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatButton
import ca.warp7.android.scouting.R
import ca.warp7.android.scouting.v5.entry.DataPoint

/**
 * A Base button for other buttons to extend onto
 * @since v0.2.0
 */

class SwitchField : AppCompatButton, BaseFieldWidget {

    override val fieldData: FieldData?

    private val white = ContextCompat.getColor(context, R.color.colorWhite)
    private val gray = ContextCompat.getColor(context, R.color.colorGray)
    private val red = ContextCompat.getColor(context, R.color.colorRed)
    private val lightGreen = ContextCompat.getColor(context, R.color.colorLightGreen)

    private var isOn = false

    constructor(context: Context) : super(context) {
        fieldData = null
    }

    internal constructor(data: FieldData) : super(data.context) {
        fieldData = data

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
                    entry!!.add(DataPoint(data.typeIndex, if (isOn) 1 else 0, relativeTime))
                    feedSecondLimit()
                    updateControlState()
                    handler.postDelayed({ updateControlState() }, 1000)
                }
            }
        }

        updateControlState()
    }

    override fun updateControlState() {
        fieldData?.apply {
            if (!scoutingActivity.timeEnabled) {
                isEnabled = false
                setTextColor(gray)
            } else {
                isEnabled = true
                scoutingActivity.entry?.apply {
                    isOn = count(typeIndex) % 2 != 0
                    if (isOn) {
                        setTextColor(white)
                        background.setColorFilter(red, PorterDuff.Mode.MULTIPLY)
                    } else {
                        setTextColor(lightGreen)
                        background.clearColorFilter()
                    }
                }
            }
        }
    }
}
