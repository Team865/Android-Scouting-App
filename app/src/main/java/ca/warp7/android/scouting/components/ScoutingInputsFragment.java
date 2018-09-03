package ca.warp7.android.scouting.components;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.List;

import ca.warp7.android.scouting.R;
import ca.warp7.android.scouting.abstraction.BaseInputControl;
import ca.warp7.android.scouting.abstraction.ScoutingActivityListener;
import ca.warp7.android.scouting.abstraction.ScoutingTab;
import ca.warp7.android.scouting.constants.ID;
import ca.warp7.android.scouting.model.DataConstant;
import ca.warp7.android.scouting.model.ScoutingLayout;
import ca.warp7.android.scouting.model.Specs;
import ca.warp7.android.scouting.widgets.CenteredControlLayout;
import ca.warp7.android.scouting.widgets.Checkbox;
import ca.warp7.android.scouting.widgets.ChoicesButton;
import ca.warp7.android.scouting.widgets.CountedInputControlLayout;
import ca.warp7.android.scouting.widgets.DurationButton;
import ca.warp7.android.scouting.widgets.LabeledControlLayout;
import ca.warp7.android.scouting.widgets.TimerButton;
import ca.warp7.android.scouting.widgets.UndefinedInputsIndicator;

/**
 * The fragment that is shown in the biggest portion
 * of ScoutingActivity -- it manages a TableLayout that
 * contains the views from InputControls defined in Specs
 *
 * @author Team 865
 * @since v0.2.0
 */

public class ScoutingInputsFragment
        extends Fragment implements ScoutingTab {


    private ScoutingActivityListener mListener;

    private TableLayout mInputTable;

    private Specs mSpecs;
    private ScoutingLayout mLayout;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int tabNumber = getArguments() != null ? getArguments().getInt("tab") : -1;

        mSpecs = Specs.getInstance();

        if (mSpecs == null) {
            Activity activity = getActivity();
            if (activity != null) {
                Specs.setInstance(activity.getIntent().getStringExtra(ID.MSG_SPECS_FILE));
                mSpecs = Specs.getInstance();
            }
        }

        mLayout = mSpecs.getLayouts().get(tabNumber);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inputs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mInputTable = view.findViewById(R.id.input_table);

        if (mSpecs != null) {
            layoutTable();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof ScoutingActivityListener) {
            mListener = (ScoutingActivityListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ScoutingActivityListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Creates a view from its definition
     *
     * @param dc       the data constant
     * @param idIfNull the display value if control is undefined
     * @return a matching View from InputControls
     */

    private View createControlFromDataConstant(DataConstant dc, String idIfNull) {

        if (dc == null) {
            return new UndefinedInputsIndicator(getContext(), idIfNull, mListener);
        }

        switch (dc.getType()) {
            case DataConstant.TIMESTAMP:
                //return new InputControls.TimerButton(getContext(), dc, mListener);
                return new CountedInputControlLayout(getContext(), dc, mListener,
                        new TimerButton(getContext(), dc, mListener));

            case DataConstant.CHECKBOX:
                return new CenteredControlLayout(getContext(), dc, mListener,
                        new Checkbox(getContext(), dc, mListener));

            case DataConstant.DURATION:
                return new DurationButton(getContext(), dc, mListener);


            case DataConstant.RATING:

                return new LabeledControlLayout(getContext(), dc, mListener,
                        new ca.warp7.android.scouting.widgets.SeekBar(getContext(), dc, mListener));

            case DataConstant.CHOICE:

                return new LabeledControlLayout(getContext(), dc, mListener,
                        new ChoicesButton(getContext(), dc, mListener));

            default:
                return new UndefinedInputsIndicator(getContext(), dc.getLabel(), mListener);
        }
    }

    /**
     * Get a specific view by its ID and its span in the table
     *
     * @return the specified view with added layout
     */

    private View createControlFromIdAndSpan(String id, int span) {
        DataConstant dc = mSpecs.getDataConstantByStringID(id);

        View view = createControlFromDataConstant(dc, id);

        TableRow.LayoutParams lp = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT);

        lp.span = span;
        lp.width = 0;

        view.setLayoutParams(lp);

        return view;
    }

    /**
     * Layouts a row in the table
     *
     * @param fieldRow an array of identifiers
     */

    private void layoutRow(String[] fieldRow) {
        TableRow tr = new TableRow(getContext());

        tr.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.MATCH_PARENT, 1.0f));

        if (fieldRow.length == 1) {
            tr.addView(createControlFromIdAndSpan(fieldRow[0], 2));

        } else {
            for (String fieldID : fieldRow) {
                tr.addView(createControlFromIdAndSpan(fieldID, 1));
            }
        }

        mInputTable.addView(tr);
    }

    /**
     * Get the layout and create the entire table
     */

    private void layoutTable() {

        List<String[]> fields = mLayout.getFields();
        mInputTable.setWeightSum(fields.size());


        for (String[] fieldRow : fields) {
            layoutRow(fieldRow);
        }
    }

    @Override
    public void updateTabState() {

        if (mInputTable != null) {
            for (int i = 0; i < mInputTable.getChildCount(); i++) {
                View child = mInputTable.getChildAt(i);
                if (child instanceof TableRow) {
                    TableRow row = (TableRow) child;
                    for (int j = 0; j < row.getChildCount(); j++) {
                        View view = row.getChildAt(j);
                        if (view instanceof BaseInputControl) {
                            ((BaseInputControl) view).updateControlState();
                        }
                    }
                }
            }
        }
    }

    /**
     * Creates an fragment instance
     *
     * @param currentTab the tab to create the instance on
     * @return the created instance
     */

    public static ScoutingInputsFragment createInstance(int currentTab) {
        ScoutingInputsFragment f = new ScoutingInputsFragment();

        Bundle args = new Bundle();
        args.putInt("tab", currentTab);

        f.setArguments(args);
        return f;
    }
}
