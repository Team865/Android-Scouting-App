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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;


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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inputs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inputTable = view.findViewById(R.id.input_table);
        handler = listener.getHandler();
        vibrator = listener.getVibrator();

        for (int i = 0; i < 1; i++){
            TableRow tr = createLayoutRow();
            tr.addView(createLayoutButton("" + i, 1));
            tr.addView(createLayoutButton("Hello World", 1));
            inputTable.addView(tr);
        }

        TableRow tr2 = createLayoutRow();
        tr2.addView(createLayoutButton(layout.getTitle(), 2));
        inputTable.addView(tr2);

        for (int i = 0; i < 2; i++){
            TableRow tr = createLayoutRow();
            tr.addView(createLayoutButton("" + i, 1));
            tr.addView(createLayoutButton("Hello World", 1));
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


    Button createLayoutButton(String text, int span) {
        Button button = new Button(getContext());

        button.setText(text);
        button.setAllCaps(false);
        button.setTextSize(20);
        button.setTypeface(Typeface.SANS_SERIF);
        button.setTextColor(getResources().getColor(R.color.colorAccent));


        TableRow.LayoutParams layoutParams = createCellParams();
        layoutParams.width = 0;
        layoutParams.span = span;

        button.setLayoutParams(layoutParams);


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
        return new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT);
    }

    static TableLayout.LayoutParams createRowParams(){
        return new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
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
}
