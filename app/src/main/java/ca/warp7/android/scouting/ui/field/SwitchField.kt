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

    private var isChecked = false
    private var button: Button? = null

    constructor(context: Context) : super(context) {
        fieldData = null
    }

    internal constructor(data: FieldData) : super(data.context) {
        fieldData = data

        button = Button(data.context).apply {
            isAllCaps = false
            textSize = 18f
            typeface = Typeface.SANS_SERIF
            stateListAnimator = null
            text = data.modifiedName
            setLines(2)
            setBackgroundResource(R.drawable.ripple_button)
            background.mutate()
            setOnClickListener { onClick(data) }
            addView(this)
        }

        updateControlState()
    }

    private fun onClick(data: FieldData) {
        val activity = data.scoutingActivity
        if (activity.isTimeEnabled()) {
            activity.vibrateAction()
            val entry = activity.entry
            if (entry != null) {
                val newState = !isChecked
                entry.add(DataPoint(data.typeIndex, if (newState) 1 else 0, activity.getRelativeTime()))
                updateControlState()
            }
        }
    }

    override fun updateControlState() {
        val fieldData = fieldData ?: return
        val button = button ?: return

        if (fieldData.scoutingActivity.isTimeEnabled()) {
            button.isEnabled = true
            val entry = fieldData.scoutingActivity.entry
            if (entry != null) {
                val lastDP  = entry.lastValue(fieldData.typeIndex)
                isChecked = if (lastDP != null) lastDP.value == 1 else false

                if (isChecked) {
                    button.setTextColor(white)
                    button.background.setColorFilter(red, PorterDuff.Mode.SRC)
                } else {
                    button.setTextColor(lightGreen)
                    button.background.setColorFilter(almostWhite, PorterDuff.Mode.SRC)
                }
            }
        } else {
            button.isEnabled = false
            button.setTextColor(gray)
            button.background.setColorFilter(almostWhite, PorterDuff.Mode.SRC)
        }
    }
}
