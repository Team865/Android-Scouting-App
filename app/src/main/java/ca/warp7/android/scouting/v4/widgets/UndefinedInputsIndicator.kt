package ca.warp7.android.scouting.v4.widgets

import android.content.Context
import android.graphics.PorterDuff
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatButton
import ca.warp7.android.scouting.R
import ca.warp7.android.scouting.abstraction.ScoutingActivityListener

/**
 * Creates a placeholder button that shows definition errors
 * @since v0.2.0
 */

class UndefinedInputsIndicator : AppCompatButton {


    constructor(context: Context) : super(context)

    constructor(context: Context, text: String, listener: ScoutingActivityListener) : super(context) {
        textSize = 18f
        setText(text)
        setOnClickListener {
            setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
            background.setColorFilter(
                ContextCompat.getColor(context, android.R.color.black), PorterDuff.Mode.MULTIPLY
            )
            listener.apply {
                managedVibrator.vibrateAction()
                handler.postDelayed({
                    setTextColor(ContextCompat.getColor(context, android.R.color.black))
                    background.clearColorFilter()
                }, 1000)
            }
        }
    }
}
