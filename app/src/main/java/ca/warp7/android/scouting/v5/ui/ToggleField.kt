package ca.warp7.android.scouting.v5.ui

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TableRow
import ca.warp7.android.scouting.R

/*
https://github.com/llollox/Android-Toggle-Switch
 */
class ToggleField : ToggleSwitchCompat, BaseFieldWidget {

    override val fieldData: FieldData?

    private val white = ContextCompat.getColor(context, R.color.colorAlmostWhite)
    private val gray = ContextCompat.getColor(context, R.color.colorGray)
    private val red = ContextCompat.getColor(context, R.color.colorRed)
    private val lightGreen = ContextCompat.getColor(context, R.color.colorLightGreen)
    private val accent = ContextCompat.getColor(context, R.color.colorAccent)

    constructor(context: Context) : super(context) {
        fieldData = null
    }

    private fun sp2Px(sp: Int): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp.toFloat(), context.resources.displayMetrics)
    }

    internal constructor(data: FieldData) : super(data.context) {
        fieldData = data
        checkedBackgroundColor = accent
        uncheckedBackgroundColor = white

        textSize = sp2Px(18)

        uncheckedTextColor = accent

        elevation = 4f

        var defaultIndex = 0
        val options = mutableListOf<String>()

        data.templateField.options?.forEachIndexed { i, v ->
            if (v.startsWith("default:")) {
                defaultIndex = i
                options.add(v.substring(8))
            } else {
                options.add(v)
            }
        }

        setEntries(options)
        setCheckedPosition(defaultIndex)
        layoutHeight = ViewGroup.LayoutParams.WRAP_CONTENT


        layoutParams = TableRow.LayoutParams().apply {
            gravity = Gravity.CENTER
        }

        //layoutWidth = ViewGroup.LayoutParams.MATCH_PARENT


        setOnClickListener {
            //            data.scoutingActivity.apply {
//                if (timeEnabled && !isSecondLimit) {
//                    actionVibrator?.vibrateAction()
//                    entry!!.add(DataPoint(data.typeIndex, if (isOn) 1 else 0, relativeTime))
//                    feedSecondLimit()
//                    updateControlState()
//                    handler.postDelayed({ updateControlState() }, 1000)
//                }
//            }
        }

        updateControlState()
    }

    override fun updateControlState() {
//        fieldData?.apply {
//            if (!scoutingActivity.timeEnabled) {
//                isEnabled = false
//                setTextColor(gray)
//            } else {
//                isEnabled = true
//                scoutingActivity.entry?.apply {
//                    isOn = count(typeIndex) % 2 != 0
//                    if (isOn) {
//                        setTextColor(white)
//                        background.setColorFilter(red, PorterDuff.Mode.MULTIPLY)
//                    } else {
//                        setTextColor(lightGreen)
//                        background.clearColorFilter()
//                    }
//                }
//            }
//        }
    }
}
