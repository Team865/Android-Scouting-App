package ca.warp7.android.scouting;

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


public class InputsFragment
        extends Fragment {

    static InputsFragment createInstance(int currentTab) {
        InputsFragment f = new InputsFragment();

        Bundle args = new Bundle();
        args.putInt("tab", currentTab);

        f.setArguments(args);
        return f;
    }

    InputControls.ActivityListener listener;

    TableLayout inputTable;

    Specs specs;
    Specs.Layout layout;


    public InputsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int tabNumber = getArguments() != null ? getArguments().getInt("tab") : -1;

        specs = Specs.getInstance();
        layout = specs.getLayouts().get(tabNumber);
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

        inputTable = view.findViewById(R.id.input_table);

        layoutTable();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof InputControls.ActivityListener) {
            listener = (InputControls.ActivityListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement InputControls.ActivityListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }


    View createControlFromDataConstant(Specs.DataConstant dc, String idIfNull) {

        if (dc == null) {
            return new InputControls.UnknownControl(getContext(), idIfNull, listener);
        }

        switch (dc.getType()) {
            case Specs.DataConstant.TIMESTAMP:
                return new InputControls.TimerButton(getContext(), dc, listener);

            case Specs.DataConstant.CHECKBOX:
                return new InputControls.Checkbox(getContext(), dc, listener);

            case Specs.DataConstant.DURATION:
                return new InputControls.DurationButton(getContext(), dc, listener);


            case Specs.DataConstant.RATING:

                return new InputControls.LabeledControlLayout(getContext(), dc, listener,
                        new InputControls.SeekBar(getContext(), dc, listener));

            case Specs.DataConstant.CHOICE:

                return new InputControls.LabeledControlLayout(getContext(), dc, listener,
                        new InputControls.ChoicesButton(getContext(), dc, listener));

            default:
                return new InputControls.UnknownControl(getContext(),
                        dc.getLabel(), listener);
        }
    }

    View createSpecifiedControl(String id, int span) {
        Specs.DataConstant dc = specs.getDataConstantByStringID(id);

        View view = createControlFromDataConstant(dc, id);

        TableRow.LayoutParams lp = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT);

        lp.span = span;
        lp.width = 0;

        view.setLayoutParams(lp);

        return view;
    }

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

        inputTable.addView(tr);
    }

    void layoutTable() {

        ArrayList<String[]> fields = layout.getFields();
        inputTable.setWeightSum(fields.size());


        for (String[] fieldRow : fields) {
            layoutRow(fieldRow);
        }
    }

}
