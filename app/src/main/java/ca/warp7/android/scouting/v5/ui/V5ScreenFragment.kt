package ca.warp7.android.scouting.v5.ui

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import ca.warp7.android.scouting.R
import ca.warp7.android.scouting.v5.BaseScoutingActivity
import ca.warp7.android.scouting.v5.boardfile.TemplateField
import ca.warp7.android.scouting.v5.boardfile.TemplateScreen
import ca.warp7.android.scouting.v5.boardfile.V5FieldType.*

/**
 * The fragment that is shown in the biggest portion
 * of ScoutingActivity -- it manages a TableLayout that
 * contains the views from InputControls defined in Specs
 *
 * @author Team 865
 * @since v0.2.0
 */

class V5ScreenFragment : Fragment(), V5Tab {


    private var scoutingActivity: BaseScoutingActivity? = null
    private var screenTable: TableLayout? = null

    private var screen: TemplateScreen? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_inputs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        screenTable = view.findViewById(R.id.input_table)

        if (screen != null) {
            layoutTable()
        }
    }

    override fun onAttach(context: Context?) {
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

//        if (dc == null) {
//            return UndefinedInputsIndicator(context!!, idIfNull, scoutingActivity!!)
//        }

        val scoutingActivity = scoutingActivity
        val context = context
        if (scoutingActivity != null && context != null) {
            val data = FieldData(context, templateField, scoutingActivity, modifyName(templateField.name),
                scoutingActivity.template?.lookup(templateField) ?: 0 + 1)
            return when (templateField.type) {
                Choice -> TODO()
                Checkbox -> TODO()
                Button -> CountedButtonField(data)
                Toggle -> TODO()
                Switch -> TODO()
                else -> FieldUndefined(data)
            }
        }
        return View(context)
    }

    private fun modifyName(name: String): String {
        return name.split("_".toRegex()).joinToString(" ") { it.capitalize() }
    }

    /**
     * Get a specific view by its ID and its span in the table
     *
     * @return the specified view with added layout
     */

    private fun createControlFromIdAndSpan(field: TemplateField, span: Int): View {
        val view = createControlFromTemplateField(field)
        val lp = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT)
        lp.span = span
        lp.width = 0
        view.layoutParams = lp
        return view
    }

    /**
     * Layouts a row in the table
     *
     * @param row an array of identifiers
     */

    private fun layoutRow(row: List<TemplateField>) {
        val tr = TableRow(context)

        tr.layoutParams = TableLayout.LayoutParams(
            TableLayout.LayoutParams.MATCH_PARENT,
            TableLayout.LayoutParams.MATCH_PARENT, 1.0f
        )

        if (row.size == 1) {
            tr.addView(createControlFromIdAndSpan(row[0], 2))
        } else {
            for (i in 0..1) {
                tr.addView(createControlFromIdAndSpan(row[i], 1))
            }
        }

        screenTable?.addView(tr)
    }

    /**
     * Get the layout and create the entire table
     */

    private fun layoutTable() {
        screen?.apply {
            screenTable?.weightSum = fields.size.toFloat()
            fields.forEach { layoutRow(it) }
        }
    }

    override fun updateTabState() {
        screenTable?.apply {
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                if (child is TableRow) {
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

        fun createInstance(currentTab: Int): V5ScreenFragment {
            return V5ScreenFragment().also {
                it.arguments = Bundle().apply { putInt("tab", currentTab) }
            }
        }
    }
}
