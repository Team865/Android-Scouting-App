package ca.warp7.android.scouting.v5.ui

import android.content.Context
import android.graphics.PorterDuff
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatButton
import android.view.Gravity
import ca.warp7.android.scouting.R

/**
 * Creates a placeholder button that shows definition errors
 * @since v0.2.0
 */

class UndefinedField : AppCompatButton {
    constructor(context: Context) : super(context)
    constructor(data: FieldData) : super(data.context) {
        textSize = 18f
        text = data.templateField.name
        gravity = Gravity.CENTER
        setOnClickListener {
            setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
            background.setColorFilter(
                ContextCompat.getColor(context, android.R.color.black), PorterDuff.Mode.MULTIPLY
            )
            data.scoutingActivity.apply {
                actionVibrator?.vibrateAction()
                handler.postDelayed({
                    setTextColor(ContextCompat.getColor(context, android.R.color.black))
                    background.clearColorFilter()
                }, 1000)
            }
        }
    }
}
