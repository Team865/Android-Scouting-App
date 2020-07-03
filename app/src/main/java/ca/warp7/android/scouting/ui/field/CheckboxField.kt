package ca.warp7.android.scouting.ui.field

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.view.Gravity
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import ca.warp7.android.scouting.R
import ca.warp7.android.scouting.entry.DataPoint

@SuppressLint("ViewConstructor")
class CheckboxField internal constructor(private val data: FieldData) :
        LinearLayout(data.context), BaseFieldWidget {

    private val accent = ContextCompat.getColor(context, R.color.accent)
    private val gray = ContextCompat.getColor(context, R.color.buttonDisabled)

    init {
        setBackgroundResource(R.drawable.ripple_button)
        background.mutate()
        gravity = Gravity.CENTER
    }

    private val checkBox: CheckBox = CheckBox(data.context).apply {
        layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        )
        isAllCaps = false
        textSize = 17f
        setLines(2)
        typeface = Typeface.SANS_SERIF
        text = data.modifiedName

        setOnClickListener {
            onClick(data, this.isChecked)
        }

        addView(this)
    }

    init {
        setOnClickListener {
            val checked = !checkBox.isChecked
            checkBox.isChecked = checked
            onClick(data, checked)
        }
        updateControlState()
    }

    private fun onClick(data: FieldData, isChecked: Boolean) {
        val activity = data.scoutingActivity

        if (activity.isTimeEnabled()) {
            activity.vibrateAction()
            val entry = activity.entry
            if (entry != null) {
                entry.add(DataPoint(data.typeIndex, if (isChecked) 1 else 0, activity.getRelativeTime()))
                updateControlState()
            }
        }
    }

    override fun updateControlState() {
        if (data.scoutingActivity.isTimeEnabled()) {
            checkBox.isEnabled = true
            this.isEnabled = true
            checkBox.setTextColor(accent)
            val entry = data.scoutingActivity.entry
            if (entry != null) {
                val lastDP = entry.lastValue(data.typeIndex)
                checkBox.isChecked = if (lastDP != null) lastDP.value == 1 else false
            }
        } else {
            checkBox.isEnabled = false
            this.isEnabled = false
            checkBox.setTextColor(gray)
        }
    }
}
