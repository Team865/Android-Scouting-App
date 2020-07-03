package ca.warp7.android.scouting.ui

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

class EntryScreenFragment : Fragment(), ScoutingEntryTab {

    private var scoutingActivity: BaseScoutingActivity? = null
    private var screenTable: ViewGroup? = null
    private var screen: TemplateScreen? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        screenTable = EqualRowLayout(context)
        val screenFrameLayout = view.findViewById<FrameLayout>(R.id.screen_frame)
        screenFrameLayout.addView(screenTable)

        if (screen != null) {
            layoutTable()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseScoutingActivity) {
            scoutingActivity = context
            val template = context.template ?: return
            screen = template.screens[arguments?.getInt("tab") ?: 0]
        }
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
            val typeIndex = scoutingActivity.template?.lookup(templateField) ?: 0 + 1
            val data = FieldData(
                    context,
                    templateField,
                    scoutingActivity,
                    scoutingActivity.modifyName(templateField.name),
                    typeIndex
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

    private fun layoutRow(row: List<TemplateField>) {
        val screenTable = screenTable ?: return
        screenTable.addView(LinearLayout(context).apply {
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
        val screen = screen ?: return
        screen.layout.forEach { layoutRow(it) }
        screenTable!!.requestLayout()
    }

    override fun updateTabState() {
        val screenTable = screenTable ?: return
        for (i in 0 until screenTable.childCount) {
            val child = screenTable.getChildAt(i)
            if (child is ViewGroup) {
                for (j in 0 until child.childCount) {
                    (child.getChildAt(j) as? BaseFieldWidget)?.updateControlState()
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
