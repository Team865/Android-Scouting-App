package ca.warp7.android.scouting.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import ca.warp7.android.scouting.BaseScoutingActivity
import ca.warp7.android.scouting.R
import ca.warp7.android.scouting.boardfile.FieldType.*
import ca.warp7.android.scouting.boardfile.TemplateField
import ca.warp7.android.scouting.boardfile.TemplateScreen
import ca.warp7.android.scouting.ui.field.*

/**
 * The fragment that is shown in the biggest portion
 * of ScoutingActivity -- it manages a TableLayout that
 * contains the views from InputControls defined in Specs
 *
 * @author Team 865
 * @since v0.2.0
 */

class EntryScreenFragment : Fragment(), ScoutingEntryTab {


    private var scoutingActivity: BaseScoutingActivity? = null
    private var screenTable: ViewGroup? = null

    private var screen: TemplateScreen? = null

    private var screenFrameLayout: FrameLayout? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        screenFrameLayout = view.findViewById(R.id.screen_frame)

        screenTable = EqualRowLayout(context).apply {
            //isStretchAllColumns = true
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }.also { screenFrameLayout?.addView(it) }

        if (screen != null) {
            layoutTable()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseScoutingActivity) scoutingActivity = context
        screen = scoutingActivity?.template?.screens?.get(arguments?.getInt("tab") ?: 0)
    }

    override fun onDetach() {
        super.onDetach()
        scoutingActivity = null
    }

    /**
     * Creates a view from its definition
     *
     * @return a matching View from InputControls
     */

    private fun createControlFromTemplateField(templateField: TemplateField): View {
        val scoutingActivity = scoutingActivity
        val context = context
        if (scoutingActivity != null && context != null) {
            val data = FieldData(
                context, templateField, scoutingActivity, modifyName(templateField.name),
                scoutingActivity.template?.lookup(templateField) ?: 0 + 1
            )
            return when (templateField.type) {
                Button -> ButtonField(data)
                Checkbox -> CheckboxField(data)
                Switch -> SwitchField(data)
                MultiToggle -> ToggleField(data)
                Unknown -> UndefinedField(data)
            }
        }
        return View(context)
    }

    @SuppressLint("DefaultLocale")
    private fun modifyName(name: String): String {
        return name.split("_".toRegex()).joinToString(" ") { it.toLowerCase().capitalize() }
    }

    private fun layoutRow(row: List<TemplateField>) {
        screenTable?.addView(LinearLayout(context).apply {
            row.forEach {
                addView(createControlFromTemplateField(it).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT, 1.0f
                    )
                })
            }
        })
    }

    /**
     * Get the layout and create the entire table
     */

    private fun layoutTable() {
        screen?.apply {
            layout.forEach { layoutRow(it) }
        }
        screenTable?.requestLayout()
    }

    override fun updateTabState() {
        screenTable?.apply {
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                if (child is ViewGroup) {
                    for (j in 0 until child.childCount) {
                        (child.getChildAt(j) as? BaseFieldWidget)?.updateControlState()
                    }
                }
            }
        }
    }

    companion object {

        /**
         * Creates an fragment instance
         *
         * @param currentTab the tab to create the instance on
         * @return the created instance
         */

        fun createInstance(currentTab: Int): EntryScreenFragment {
            return EntryScreenFragment().also {
                it.arguments = Bundle().apply { putInt("tab", currentTab) }
            }
        }
    }
}
