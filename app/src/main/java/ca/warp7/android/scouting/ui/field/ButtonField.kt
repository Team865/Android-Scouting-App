package ca.warp7.android.scouting.ui.field

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import ca.warp7.android.scouting.R
import ca.warp7.android.scouting.entry.DataPoint

@SuppressLint("ViewConstructor")
class ButtonField internal constructor(private val data: FieldData) :
        FrameLayout(data.context), BaseFieldWidget {

    private val invertedText = ContextCompat.getColor(context, R.color.invertedButtonText)
    private val primaryDark = ContextCompat.getColor(context, R.color.primaryDark)
    private val buttonBack = ContextCompat.getColor(context, R.color.buttonBackground)
    private val accent = ContextCompat.getColor(context, R.color.accent)
    private val disabled = ContextCompat.getColor(context, R.color.buttonDisabled)

    private val almostWhiteFilter = colorFilter(buttonBack)
    private val accentFilter = colorFilter(accent)

    private val button: Button = Button(data.context).apply {
        isAllCaps = false
        textSize = 17f
        typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        stateListAnimator = null
        text = data.modifiedName
        setPadding(4, 4, 4, 4)
        setLines(2)
        setBackgroundResource(R.drawable.ripple_button)
        this.background.mutate()
        setOnClickListener { onClick(data) }
        addView(this)
    }

    private val counter: TextView = TextView(data.context).apply {
        text = "0"
        textSize = 15f
        elevation = 10f
        setTextColor(primaryDark)
        layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        )
        setPadding(18, 10, 18, 10)
        addView(this)
    }

    init {
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
        if (data.scoutingActivity.isTimeEnabled()) {
            button.isEnabled = true

            val entry = data.scoutingActivity.entry
            if (entry != null) {

                val count = entry.count(data.typeIndex)
                counter.text = count.toString()

                if (entry.isFocused(data.typeIndex)) {
                    button.setTextColor(invertedText)
                    button.background.colorFilter = accentFilter
                    counter.setTextColor(invertedText)
                } else {
                    button.setTextColor(accent)
                    button.background.colorFilter = almostWhiteFilter
                    counter.setTextColor(primaryDark)
                }
            }
        } else {
            button.isEnabled = false
            button.setTextColor(disabled)
            button.background.colorFilter = almostWhiteFilter
        }
    }
}
