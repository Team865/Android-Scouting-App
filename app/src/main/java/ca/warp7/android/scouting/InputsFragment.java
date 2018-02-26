package ca.warp7.android.scouting;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;


public class InputsFragment

        extends Fragment {

    InputsFragmentListener listener;
    Handler handler;
    Vibrator vibrator;

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
        handler = listener.getHandler();
        vibrator = listener.getVibrator();

        ArrayList<String[]> fields = layout.getFields();
        inputTable.setWeightSum(fields.size());


        for (String[] fieldRow : fields) {
            TableRow tr = createLayoutRow();

            Specs.DataConstant dc;

            if (fieldRow.length == 1) {
                tr.addView(createSpecifiedView(fieldRow[0], 2));

            } else {
                for (String fieldID : fieldRow) {
                    tr.addView(createSpecifiedView(fieldID, 1));
                }
            }

            inputTable.addView(tr);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof InputsFragmentListener) {
            listener = (InputsFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement InputsFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    View createSpecifiedView(String id, int span) {
        Specs.DataConstant dc = specs.getDataConstantByStringID(id);
        View view;

        if (dc != null) {

            switch (dc.getType()) {
                case Specs.DataConstant.CHECKBOX:
                    view = createLayoutCheckBox(dc.getLabel());
                    break;

                case Specs.DataConstant.DURATION:
                    view = createLayoutDurationButton(dc.getLabel(), dc.getLabelOn());
                    break;

                case Specs.DataConstant.TIMESTAMP:
                default:
                    view = createLayoutButton(dc.getLabel());
            }
        } else {
            view = createLayoutButton(id);
        }

        ((TableRow.LayoutParams) view.getLayoutParams()).span = span;
        return view;
    }

    CheckBox createLayoutCheckBox(String text) {

        CheckBox checkBox = new CheckBox(getContext());

        checkBox.setText(text);
        checkBox.setTextSize(20);
        checkBox.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        checkBox.setTextColor(getResources().getColor(R.color.colorAccent));

        TableRow.LayoutParams lp = createCellParams();

        checkBox.setLayoutParams(lp);
        //checkBox.setGravity(Gravity.CENTER);

        return checkBox;
    }

    DurationButton createLayoutDurationButton(String off, String on) {
        DurationButton button = new DurationButton(getContext());

        button.setTexts(off, on);

        button.setAllCaps(false);
        button.setTextSize(20);

        button.updateLooks();

        button.setLayoutParams(createCellParams());
        button.setLines(2);

        return button;
    }

    Button createLayoutButton(String text) {
        Button button = new Button(getContext());

        button.setText(text.replace(" ", "\n"));
        button.setAllCaps(false);
        button.setTextSize(20);
        button.setTypeface(Typeface.SANS_SERIF);
        button.setTextColor(getResources().getColor(R.color.colorAccent));

        button.setLayoutParams(createCellParams());
        button.setLines(2);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Button b = (Button) v;

                b.setTextColor(0xFFFFFFFF);
                b.getBackground().setColorFilter(
                        getResources().getColor(R.color.colorAccent),
                        PorterDuff.Mode.MULTIPLY);

                vibrator.vibrate(30);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        b.setTextColor(getResources().getColor(R.color.colorAccent));
                        b.getBackground().clearColorFilter();
                    }
                }, 1000);
            }
        });

        return button;
    }

    TableRow createLayoutRow(){
        TableRow tableRow = new TableRow(getContext());

        tableRow.setLayoutParams(createRowParams());

        return tableRow;
    }


    static TableRow.LayoutParams createCellParams(){
        TableRow.LayoutParams lp = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.MATCH_PARENT);

        lp.width = 0;

        return lp;
    }

    static TableLayout.LayoutParams createRowParams(){
        return new TableLayout.LayoutParams(
                TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.MATCH_PARENT, 1.0f);
    }

    static InputsFragment createInstance(int currentTab) {
        InputsFragment f = new InputsFragment();

        Bundle args = new Bundle();
        args.putInt("tab", currentTab);

        f.setArguments(args);
        return f;
    }

    interface InputsFragmentListener {
        Handler getHandler();
        Vibrator getVibrator();
    }

    private class DurationButton
            extends AppCompatButton implements View.OnClickListener {

        boolean isOn = false;
        String off;
        String on;

        public DurationButton(Context context) {
            super(context);
            setOnClickListener(this);
        }

        void setTexts(String off, String on) {
            this.off = off;
            this.on = on;
        }

        void updateLooks() {
            if (isOn) {
                setTextColor(0xFFFFFFFF);
                setTypeface(null);
                setText(on);
                getBackground().setColorFilter(
                        getResources().getColor(R.color.colorRed),
                        PorterDuff.Mode.MULTIPLY);
            } else {
                //setTextColor(getResources().getColor(R.color.colorLightGreen));
                setTextColor(0xFFFFFFFF);
                setTypeface(Typeface.SANS_SERIF);
                setText(off);
                //getBackground().clearColorFilter();
                getBackground().setColorFilter(
                        getResources().getColor(R.color.colorLightGreen),
                        PorterDuff.Mode.MULTIPLY);
            }
        }

        @Override
        public void onClick(View v) {
            isOn = !isOn;
            updateLooks();
            vibrator.vibrate(30);
        }
    }
}
