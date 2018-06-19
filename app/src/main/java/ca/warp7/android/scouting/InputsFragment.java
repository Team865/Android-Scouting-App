package ca.warp7.android.scouting;

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

import java.util.ArrayList;
import java.util.List;

/**
 * The fragment that is shown in the biggest portion
 * of ScoutingActivity -- it manages a TableLayout that
 * contains the views from InputControls defined in Specs
 *
 * @author Team 865
 */

public class InputsFragment
        extends Fragment {


    private ScoutingActivityListener mListener;

    private TableLayout mInputTable;

    private Specs mSpecs;
    private Specs.Layout mLayout;

    private List<View> mInputControls = new ArrayList<>();


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
                    + " must implement InputControls.ScoutingActivityListener");
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

    View createControlFromDataConstant(Specs.DataConstant dc, String idIfNull) {

        if (dc == null) {
            return new InputControls.UnknownControl(getContext(), idIfNull, mListener);
        }

        switch (dc.getType()) {
            case Specs.DataConstant.TIMESTAMP:
                //return new InputControls.TimerButton(getContext(), dc, mListener);
                return new InputControls.CountedControlLayout(getContext(), dc, mListener,
                        new InputControls.TimerButton(getContext(), dc, mListener));

            case Specs.DataConstant.CHECKBOX:
                return new InputControls.CenteredControlLayout(getContext(), dc, mListener,
                        new InputControls.Checkbox(getContext(), dc, mListener));

            case Specs.DataConstant.DURATION:
                return new InputControls.DurationButton(getContext(), dc, mListener);


            case Specs.DataConstant.RATING:

                return new InputControls.LabeledControlLayout(getContext(), dc, mListener,
                        new InputControls.SeekBar(getContext(), dc, mListener));

            case Specs.DataConstant.CHOICE:

                return new InputControls.LabeledControlLayout(getContext(), dc, mListener,
                        new InputControls.ChoicesButton(getContext(), dc, mListener));

            default:
                return new InputControls.UnknownControl(getContext(),
                        dc.getLabel(), mListener);
        }
    }

    /**
     * Get a specific view by its ID and its span in the table
     *
     * @return the specified view with added layout
     */

    View createSpecifiedControl(String id, int span) {
        Specs.DataConstant dc = mSpecs.getDataConstantByStringID(id);

        View view = createControlFromDataConstant(dc, id);

        TableRow.LayoutParams lp = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT);

        lp.span = span;
        lp.width = 0;

        view.setLayoutParams(lp);

        mInputControls.add(view);

        return view;
    }

    /**
     * Layouts a row in the table
     *
     * @param fieldRow an array of identifiers
     */

    void layoutRow(String[] fieldRow) {
        TableRow tr = new TableRow(getContext());

        tr.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.MATCH_PARENT, 1.0f));

        if (fieldRow.length == 1) {
            tr.addView(createSpecifiedControl(fieldRow[0], 2));

        } else {
            for (String fieldID : fieldRow) {
                tr.addView(createSpecifiedControl(fieldID, 1));
            }
        }

        mInputTable.addView(tr);
    }

    /**
     * Get the layout and create the entire table
     */

    void layoutTable() {

        ArrayList<String[]> fields = mLayout.getFields();
        mInputTable.setWeightSum(fields.size());


        for (String[] fieldRow : fields) {
            layoutRow(fieldRow);
        }
    }

    /**
     * Update the states of input views
     */

    void updateStates() {

        /*if (mInputTable != null) {
            mInputTable.removeAllViews();
        }

        if (mSpecs != null) {
            layoutTable();
        }*/

        for (View control : mInputControls) {
            if (control instanceof InputControls.BaseControl) {
                ((InputControls.BaseControl) control).updateControlState();
            }
        }
    }

    /**
     * Creates an fragment instance
     *
     * @param currentTab the tab to create the instance on
     * @return the created instance
     */

    static InputsFragment createInstance(int currentTab) {
        InputsFragment f = new InputsFragment();

        Bundle args = new Bundle();
        args.putInt("tab", currentTab);

        f.setArguments(args);
        return f;
    }
}
