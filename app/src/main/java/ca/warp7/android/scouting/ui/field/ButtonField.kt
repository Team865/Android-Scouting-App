package ca.warp7.android.scouting.ui.field

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import ca.warp7.android.scouting.R
import ca.warp7.android.scouting.entry.DataPoint

class ButtonField : FrameLayout, BaseFieldWidget {

    private val fieldData: FieldData?
    private val counter: TextView?
    private val button: Button?

    private val white = ContextCompat.getColor(context, R.color.colorWhite)
    private val almostBlack = ContextCompat.getColor(context, R.color.colorAlmostBlack)
    private val almostWhite = ContextCompat.getColor(context, R.color.colorAlmostWhite)
    private val accent = ContextCompat.getColor(context, R.color.colorAccent)

    constructor(context: Context) : super(context) {
        fieldData = null
        counter = null
        button = null
    }

    internal constructor(data: FieldData) : super(data.context) {
        fieldData = data

        button = Button(data.context).apply {
            isAllCaps = false
            textSize = 18f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            stateListAnimator = null
            text = data.modifiedName
            setLines(2)
            setBackgroundResource(R.drawable.ripple_button)
            this.background.mutate()
            setOnClickListener { onClick(data) }
            addView(this)
        }

        counter = TextView(data.context).apply {
            text = "0"
            textSize = 15f
            elevation = 10f
            setTextColor(almostBlack)
            layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )
            setPadding(18, 10, 18, 10)
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
                entry.add(DataPoint(data.typeIndex, 1, activity.getRelativeTime()))
                updateControlState()
            }
        }
    }

    override fun updateControlState() {
        val fieldData = fieldData ?: return
        val button = button ?: return
        val counter = counter ?: return

        if (fieldData.scoutingActivity.isTimeEnabled()) {
            button.isEnabled = true

            val entry = fieldData.scoutingActivity.entry
            if (entry != null) {

                val count = entry.count(fieldData.typeIndex)
                counter.text = count.toString()

                if (entry.isFocused(fieldData.typeIndex)) {
                    button.setTextColor(white)
                    button.background.setColorFilter(accent, PorterDuff.Mode.SRC)
                    counter.setTextColor(white)
                } else {
                    button.setTextColor(accent)
                    button.background.setColorFilter(almostWhite, PorterDuff.Mode.SRC)
                    counter.setTextColor(almostBlack)
                }
            }
        } else {
            button.isEnabled = false
            button.setTextColor(ContextCompat.getColor(context, R.color.colorGray))
            button.background.setColorFilter(almostWhite, PorterDuff.Mode.SRC)
        }
    }
}
