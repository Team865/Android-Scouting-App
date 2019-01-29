package ca.warp7.android.scouting.v5.ui

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import ca.warp7.android.scouting.R
import ca.warp7.android.scouting.v5.entry.DataPoint

/**
 * A Base button for other buttons to extend onto
 * @since v0.2.0
 */

class ButtonField : FrameLayout, BaseFieldWidget {

    override val fieldData: FieldData?
    private val counter: TextView?
    private val button: Button?

    private val white = ContextCompat.getColor(context, R.color.colorWhite)
    private val almostBlack = ContextCompat.getColor(context, R.color.colorAlmostBlack)
    private val almostWhite = ContextCompat.getColor(context, R.color.colorAlmostWhite)
    private val accent = ContextCompat.getColor(context, R.color.colorAccent)

    private var hasReset: Boolean = false
    private var resetTypeIndex: Int = 0
    private var resetValue: Int = 0

    constructor(context: Context) : super(context) {
        fieldData = null
        counter = null
        button = null
    }

    internal constructor(data: FieldData) : super(data.context) {
        fieldData = data
        setBackgroundResource(R.drawable.layer_list_bg_group)
        background.mutate()
        button = Button(data.context).apply {
            isAllCaps = false
            textSize = 18f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            stateListAnimator = null
            text = data.modifiedName
            setLines(2)
            setBackgroundColor(0)
            setOnClickListener {
                data.scoutingActivity.apply {
                    if (timeEnabled) {
                        actionVibrator?.vibrateAction()
                        entry!!.add(DataPoint(data.typeIndex, 1, relativeTime))
                        if (hasReset) {
                            entry!!.add(DataPoint(resetTypeIndex, resetValue, relativeTime))
                        }
                        updateControlState()
                        handler.postDelayed({ updateControlState() }, 1000)
                    }
                }
            }
        }.also { addView(it) }
        counter = TextView(data.context).apply {
            text = "0"
            textSize = 15f
            elevation = 10f
            setTextColor(almostBlack)
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(18, 10, 18, 10)
        }.also { addView(it) }
        data.templateField.options?.forEach {
            if (it.startsWith("resets:")) {
                hasReset = true
                val split = it.substring(7).split("=")
                resetTypeIndex = data.scoutingActivity.template?.lookup(split[0]) ?: 0
                resetValue = split[1].toInt()
            }
        }
        updateControlState()
    }

    override fun updateControlState() {
        fieldData?.apply {
            if (!scoutingActivity.timeEnabled) {
                button!!.isEnabled = false
                button.setTextColor(ContextCompat.getColor(context, R.color.colorGray))
                background.setColorFilter(almostWhite, PorterDuff.Mode.SRC)
            } else {
                button!!.isEnabled = true
                scoutingActivity.entry?.apply {
                    val count = count(typeIndex)
                    counter?.text = count.toString()
                    if (focused(typeIndex)){
                        button.setTextColor(white)
                        background.setColorFilter(accent, PorterDuff.Mode.SRC)
                        counter!!.setTextColor(white)
                    } else {
                        button.setTextColor(accent)
                        background.setColorFilter(almostWhite, PorterDuff.Mode.SRC)
                        counter!!.setTextColor(almostBlack)
                    }
                }
            }
        }
    }
}
