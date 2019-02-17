package ca.warp7.android.scouting.v5.ui

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import ca.warp7.android.scouting.R
import ca.warp7.android.scouting.v5.entry.DataPoint

/*
https://github.com/llollox/Android-Toggle-Switch
 */
class ToggleField : LinearLayout, BaseFieldWidget {

    override val fieldData: FieldData?

    private val almostWhite = ContextCompat.getColor(context, R.color.colorAlmostWhite)
    private val almostBlack = ContextCompat.getColor(context, R.color.colorAlmostBlack)
    private val accent = ContextCompat.getColor(context, R.color.colorAccent)

    private val toggleSwitch: ToggleSwitchCompat?
    private var checkedPosition = -1
    private var defaultPosition = 0

    constructor(context: Context) : super(context) {
        fieldData = null
        toggleSwitch = null
    }

    private fun sp2Px(sp: Int): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp.toFloat(), context.resources.displayMetrics)
    }

    internal constructor(data: FieldData) : super(data.context) {
        fieldData = data
        orientation = VERTICAL

        setBackgroundResource(R.drawable.layer_list_bg_group)
        background.mutate()
        gravity = Gravity.CENTER

        TextView(data.context).apply {
            text = data.modifiedName
            setTextColor(almostBlack)
            textSize = 14f
            setPadding(0, 8, 0, 0)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }.also { addView(it) }

        toggleSwitch = ToggleSwitchCompat(data.context).apply {
            checkedBackgroundColor = accent
            uncheckedBackgroundColor = almostWhite
            textSize = sp2Px(18)
            uncheckedTextColor = accent
            separatorVisible = false
            elevation = 4f
            val options = mutableListOf<String>()
            data.templateField.options?.forEachIndexed { i, v ->
                if (v.startsWith("default:")) {
                    defaultPosition = i
                    options.add(v.substring(8))
                } else options.add(v)
            }
            setEntries(options)
            layoutHeight = ViewGroup.LayoutParams.MATCH_PARENT
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            setPadding(8, 4, 8, 8)
            setOnChangeListener {
                if (it != checkedPosition) {
                    checkedPosition = it
                    data.scoutingActivity.apply {
                        actionVibrator?.vibrateAction()
                        entry!!.add(DataPoint(data.typeIndex, checkedPosition, relativeTime))
                        updateControlState()
                    }
                }
            }

        }.also { addView(it) }
        updateControlState()
    }

    override fun updateControlState() {
        fieldData?.apply {
            scoutingActivity.entry?.apply {
                val newPos = lastValue(typeIndex)?.value ?: defaultPosition
                if (newPos != checkedPosition) {
                    checkedPosition = newPos
                    toggleSwitch?.setCheckedPosition(checkedPosition)
                }
            }
        }
    }
}
