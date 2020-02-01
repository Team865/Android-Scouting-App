package ca.warp7.android.scouting.ui.field

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.widget.Button
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import ca.warp7.android.scouting.R
import ca.warp7.android.scouting.entry.DataPoint

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
        background.mutate()

        button = Button(data.context).apply {
            isAllCaps = false
            textSize = 18f
            typeface = Typeface.SANS_SERIF
            stateListAnimator = null
            text = data.modifiedName
            setLines(2)
            setBackgroundColor(0)
            setOnClickListener { onClick(data) }
            addView(this)
        }

        updateControlState()
    }

    private fun onClick(data: FieldData) {
        val activity = data.scoutingActivity
        if (activity.isTimeEnabled()) {
            activity.vibrateAction()
            val entry = activity.entry ?: return
            entry.add(DataPoint(data.typeIndex, if (isOn) 1 else 0, activity.getRelativeTime()))
            updateControlState()
        }
    }

    override fun updateControlState() {
        val fieldData = fieldData ?: return
        val button = button ?: return

        if (fieldData.scoutingActivity.isTimeEnabled()) {
            button.isEnabled = true
            val entry = fieldData.scoutingActivity.entry
            if (entry != null) {
                isOn = entry.count(fieldData.typeIndex) % 2 != 0

                if (isOn) {
                    button.setTextColor(white)
                    background.setColorFilter(red, PorterDuff.Mode.SRC)
                } else {
                    button.setTextColor(lightGreen)
                    background.setColorFilter(almostWhite, PorterDuff.Mode.SRC)
                }
            }
        } else {
            button.isEnabled = false
            button.setTextColor(gray)
            background.setColorFilter(almostWhite, PorterDuff.Mode.SRC)
        }
    }
}
