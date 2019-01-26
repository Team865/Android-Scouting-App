package ca.warp7.android.scouting.v5.ui

import android.content.Context
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.TableRow
import android.widget.TextView
import ca.warp7.android.scouting.R
import ca.warp7.android.scouting.abstraction.BaseInputControl

/**
 * Creates a box container for a label and another control
 *
 * @since v0.3.0
 */

class ChoicesField : LinearLayout, BaseInputControl {

    private val button: TextView?

    private val accent = ContextCompat.getColor(context, R.color.colorAccent)

    constructor(context: Context) : super(context) {
        button = null
    }

    constructor(data: FieldData) : super(data.context) {

        orientation = LinearLayout.VERTICAL

        setBackgroundResource(R.drawable.layer_list_bg_group)

        gravity = Gravity.CENTER

        val childLayout = TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.MATCH_PARENT
            //,1.0f
        )

        val label = TextView(context)
        label.setTextColor(ContextCompat.getColor(context, R.color.colorAlmostBlack))

        label.text = data.modifiedName
        label.gravity = Gravity.CENTER
        label.textSize = 15f

        //label.layoutParams = childLayout
        addView(label)

        addView(View(data.context).apply {
            layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT).also {
                it.height = 40
            }
        })

        //layoutParams = TableRow.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)

        button = TextView(data.context).apply {
            text = data.templateField.options?.getOrNull(0) ?: "None"
            setTextColor(accent)
            isAllCaps = false
            textSize = 18f
            typeface = Typeface.SANS_SERIF
            gravity = Gravity.CENTER
        }.also { addView(it) }

        updateControlState()
        //
        //        control.setLayoutParams(childLayout);
        //        addView(control);


    }

    override fun updateControlState() {}
}
