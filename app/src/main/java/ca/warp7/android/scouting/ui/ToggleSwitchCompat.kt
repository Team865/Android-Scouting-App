package ca.warp7.android.scouting.ui

import android.content.Context
import android.util.AttributeSet
import ca.warp7.android.scouting.v6.toggle.BaseToggleSwitch
import ca.warp7.android.scouting.v6.toggle.ToggleSwitchButton

@Suppress("unused")
open class ToggleSwitchCompat : BaseToggleSwitch {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    interface OnChangeListener {
        fun onToggleSwitchChanged(position: Int)
    }

    private var checkedPosition: Int? = null
    private var onChangeListener: ((Int) -> Unit)? = null

    override fun onRedrawn() {
        if (checkedPosition != null) {
            val currentToggleSwitch = buttons[checkedPosition!!]
            currentToggleSwitch.check()
            currentToggleSwitch.isClickable = false
        }
        manageSeparatorVisiblity()
    }

    fun setOnChangeListener(listener: (Int) -> Unit) {
        onChangeListener = listener
    }

    override fun onToggleSwitchClicked(button: ToggleSwitchButton) {

        if (!button.isChecked && isEnabled) {
            if (checkedPosition != null) {
                buttons[checkedPosition!!].uncheck()
            }

            checkedPosition = buttons.indexOf(button)

            button.check()

            manageSeparatorVisiblity()

            onChangeListener?.invoke(checkedPosition!!)
        }
    }

    fun getCheckedPosition(): Int {
        return checkedPosition ?: -1
    }

    fun setCheckedPosition(checkedPosition: Int) {
        this.checkedPosition = checkedPosition
        for ((index, toggleSwitchButton) in buttons.withIndex()) {
            if (checkedPosition == index) {
                toggleSwitchButton.check()
            } else {
                toggleSwitchButton.uncheck()
            }
        }
        manageSeparatorVisiblity()
    }
}