package ca.warp7.android.scouting.ui.field

import android.content.Context
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton

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
            data.scoutingActivity.vibrateAction()
        }
    }
}
