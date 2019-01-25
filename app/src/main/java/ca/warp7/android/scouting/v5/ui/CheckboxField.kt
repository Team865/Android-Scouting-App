package ca.warp7.android.scouting.v5.ui

import android.content.Context
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.widget.CheckBox
import android.widget.LinearLayout
import ca.warp7.android.scouting.R
import ca.warp7.android.scouting.v5.entry.DataPoint

/**
 * A Base button for other buttons to extend onto
 * @since v0.2.0
 */

class CheckboxField : LinearLayout, BaseFieldWidget {

    override val fieldData: FieldData?
    private val checkBox: CheckBox?

    private val white = ContextCompat.getColor(context, R.color.colorWhite)
    private val almostBlack = ContextCompat.getColor(context, R.color.colorAlmostBlack)
    private val accent = ContextCompat.getColor(context, R.color.colorAccent)
    private val gray = ContextCompat.getColor(context, R.color.colorGray)

    constructor(context: Context) : super(context) {
        fieldData = null
        checkBox = null
    }

    internal constructor(data: FieldData) : super(data.context) {
        fieldData = data

        setBackgroundResource(R.drawable.layer_list_bg_group)
        gravity = Gravity.CENTER

        checkBox = CheckBox(data.context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            isAllCaps = false
            textSize = 18f
            setLines(2)
            typeface = Typeface.SANS_SERIF
            text = data.modifiedName
            setOnClickListener {
                data.scoutingActivity.apply {
                    if (timeEnabled){
                        actionVibrator?.vibrateAction()
                        entry!!.add(DataPoint(data.typeIndex, if (isChecked) 1 else 0, relativeTime))
                        updateControlState()
                    }
                }
            }
        }.also { addView(it) }

        updateControlState()
    }

    override fun updateControlState() {
        fieldData?.apply {
            if (!scoutingActivity.timeEnabled) {
                checkBox!!.isEnabled = false
                checkBox.setTextColor(gray)
            } else {
                checkBox!!.isEnabled = true
                checkBox.setTextColor(accent)
                scoutingActivity.entry?.apply {
                    checkBox.isChecked = count(typeIndex) % 2 != 0
                }
            }
        }
    }
}
