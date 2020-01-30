package ca.warp7.android.scouting.ui.field

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import ca.warp7.android.scouting.R
import ca.warp7.android.scouting.entry.DataPoint

class CheckboxField : LinearLayout, BaseFieldWidget {

    override val fieldData: FieldData?
    private val checkBox: CheckBox?

    private val accent = ContextCompat.getColor(context, R.color.colorAccent)
    private val gray = ContextCompat.getColor(context, R.color.colorGray)

    constructor(context: Context) : super(context) {
        fieldData = null
        checkBox = null
    }

    internal constructor(data: FieldData) : super(data.context) {
        fieldData = data

        setBackgroundResource(R.drawable.layer_list_bg_group)
        background.mutate()
        gravity = Gravity.CENTER

        checkBox = CheckBox(data.context).apply {
            layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )
            isAllCaps = false
            textSize = 18f
            setLines(2)
            typeface = Typeface.SANS_SERIF
            text = data.modifiedName
        }.also { addView(it) }

        val clickListener = OnClickListener {
            data.scoutingActivity.apply {
                if (timeEnabled) {
                    vibrateAction()
                    entry!!.add(DataPoint(data.typeIndex, if (checkBox.isChecked) 1 else 0, getRelativeTime()))
                    updateControlState()
                }
            }
        }

        checkBox.setOnClickListener(clickListener)
        this.setOnClickListener(clickListener)
        updateControlState()
    }

    override fun updateControlState() {
        val fieldData = fieldData ?: return
        val checkBox = checkBox ?: return

        if (fieldData.scoutingActivity.timeEnabled) {
            checkBox.isEnabled = true
            checkBox.setTextColor(accent)
            val entry = fieldData.scoutingActivity.entry
            if (entry != null) {
                checkBox.isChecked = entry.count(fieldData.typeIndex) % 2 != 0
            }
        } else {
            checkBox.isEnabled = false
            checkBox.setTextColor(gray)
        }
    }
}
