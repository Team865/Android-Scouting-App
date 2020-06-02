package ca.warp7.android.scouting.ui.field

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.widget.Button
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import ca.warp7.android.scouting.R
import ca.warp7.android.scouting.entry.DataPoint

@SuppressLint("ViewConstructor")
class SwitchField internal constructor(private val data: FieldData) :
        FrameLayout(data.context), BaseFieldWidget {

    private val white = ContextCompat.getColor(context, R.color.colorWhite)
    private val gray = ContextCompat.getColor(context, R.color.colorGray)
    private val red = ContextCompat.getColor(context, R.color.colorRed)
    private val lightGreen = ContextCompat.getColor(context, R.color.colorLightGreen)
    private val almostWhite = ContextCompat.getColor(context, R.color.colorAlmostWhite)
    private val accent = ContextCompat.getColor(context, R.color.colorAccent)

    private var isChecked = false

    private val button: Button = Button(data.context).apply {
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

    private val isLite: Boolean =
            data.templateField.json.optBoolean("is_lite", false)

    init {
        updateControlState()
    }

    private fun onClick(data: FieldData) {
        val activity = data.scoutingActivity
        if (activity.isTimeEnabled() || isLite) {
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
        if (data.scoutingActivity.isTimeEnabled() || isLite) {
            button.isEnabled = true
            val entry = data.scoutingActivity.entry
            if (entry != null) {
                val lastDP = entry.lastValue(data.typeIndex)
                isChecked = if (lastDP != null) lastDP.value == 1 else false

                if (isChecked) {
                    button.setTextColor(white)
                    if (isLite) {
                        button.background.setColorFilter(accent, PorterDuff.Mode.SRC)
                    } else {
                        button.background.setColorFilter(red, PorterDuff.Mode.SRC)
                    }
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
