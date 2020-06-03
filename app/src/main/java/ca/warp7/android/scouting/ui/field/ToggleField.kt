package ca.warp7.android.scouting.ui.field

import android.annotation.SuppressLint
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import ca.warp7.android.scouting.R
import ca.warp7.android.scouting.boardfile.mapToList
import ca.warp7.android.scouting.entry.DataPoint
import ca.warp7.android.scouting.ui.toggle.ToggleSwitchCompat

/*
https://github.com/llollox/Android-Toggle-Switch
 */
@SuppressLint("ViewConstructor")
class ToggleField internal constructor(private val data: FieldData) :
        LinearLayout(data.context), BaseFieldWidget {

    private val almostWhite = ContextCompat.getColor(context, R.color.colorAlmostWhite)
    private val almostBlack = ContextCompat.getColor(context, R.color.colorAlmostBlack)
    private val accent = ContextCompat.getColor(context, R.color.colorAccent)

    private var checkedPosition = -1
    private var defaultPosition = 0

    private fun getToggleButtonTextSize(): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                18f, context.resources.displayMetrics)
    }

    init {
        orientation = VERTICAL

        setBackgroundResource(R.drawable.layer_list_bg_group)
        background.mutate()
        gravity = Gravity.CENTER

        TextView(data.context).apply {
            text = data.modifiedName
            setTextColor(almostBlack)
            textSize = 14f
            setPadding(0, 8, 0, 0)
            layoutParams = LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            )
            addView(this)
        }

        defaultPosition = data.templateField.json.optInt("default_choice", 0)
    }

    private val toggleSwitch: ToggleSwitchCompat = ToggleSwitchCompat(data.context).apply {

        val choices = data.templateField.json.getJSONArray("choices")
                .mapToList { data.scoutingActivity.modifyName(it as String) }

        checkedBackgroundColor = accent
        uncheckedBackgroundColor = almostWhite
        textSize = getToggleButtonTextSize()
        uncheckedTextColor = accent
        separatorVisible = false
        elevation = 4f

        setEntries(choices)

        layoutHeight = ViewGroup.LayoutParams.MATCH_PARENT
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT

        setPadding(8, 4, 8, 8)
        setOnChangeListener { index -> onToggle(index) }
    }

    init {
        addView(toggleSwitch)
        updateControlState()
    }

    private fun onToggle(index: Int) {
        if (index != checkedPosition) {
            checkedPosition = index
            val activity = data.scoutingActivity
            activity.vibrateAction()
            val entry = activity.entry
            if (entry != null) {
                entry.add(DataPoint(data.typeIndex, checkedPosition, activity.getRelativeTime()))
                updateControlState()
            }
        }
    }

    override fun updateControlState() {
        val entry = data.scoutingActivity.entry ?: return

        val newPos = entry.lastValue(data.typeIndex)?.value ?: defaultPosition
        if (newPos != checkedPosition) {
            checkedPosition = newPos
            toggleSwitch.setCheckedPosition(checkedPosition)
        }
    }
}
