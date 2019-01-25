package ca.warp7.android.scouting.v5.ui

import android.content.Context
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatButton
import ca.warp7.android.scouting.R
import ca.warp7.android.scouting.abstraction.BaseInputControl
import ca.warp7.android.scouting.v5.entry.DataPoint

/**
 * A Base button for other buttons to extend onto
 * @since v0.2.0
 */

class FieldButton : AppCompatButton, BaseInputControl {

    private val fieldData: FieldData?

    constructor(context: Context) : super(context) {
        fieldData = null
    }

    internal constructor(data: FieldData) : super(data.context) {
        fieldData = data

        setOnClickListener {
            data.scoutingActivity.apply {
                if(!isSecondLimit){
                    actionVibrator?.vibrateAction()
                    entry?.add(DataPoint(data.typeIndex, 1, currentTime))
                    updateControlState()
                    handler.postDelayed({updateControlState()}, 1000)
                }
            }
        }

        isAllCaps = false
        textSize = 18f
        //setLines(2)
        minLines = 2
        typeface = Typeface.SANS_SERIF
        stateListAnimator = null
        elevation = 4f

        text = data.modifiedName
    }

    override fun updateControlState() {
        fieldData?.apply {
            if (!scoutingActivity.timeEnabled){
                isEnabled = false
                setTextColor(ContextCompat.getColor(context, R.color.colorGray))
            } else {
                isEnabled = true

            }
        }
    }
}
