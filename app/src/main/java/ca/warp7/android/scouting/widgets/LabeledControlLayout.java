package ca.warp7.android.scouting.widgets;

import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import ca.warp7.android.scouting.R;
import ca.warp7.android.scouting.model.BaseInputControl;
import ca.warp7.android.scouting.model.DataConstant;
import ca.warp7.android.scouting.model.ScoutingActivityListener;

/**
 * Creates a box container for a label and another control
 */

public class LabeledControlLayout
        extends LinearLayout
        implements BaseInputControl {

    DataConstant dc;
    ScoutingActivityListener listener;

    View subControl;

    public LabeledControlLayout(Context context) {
        super(context);
    }

    public LabeledControlLayout(Context context,
                                DataConstant dc,
                                ScoutingActivityListener listener,
                                View control) {
        super(context);
        this.dc = dc;
        this.listener = listener;

        setOrientation(VERTICAL);

        // Set the background of the view

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(new Button(getContext()).getBackground());
        } else {
            setBackgroundResource(android.R.drawable.btn_default);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setElevation(4);
        }

        TableRow.LayoutParams childLayout = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT,
                1.0f);

        // Add the views

        TextView label = new TextView(context);
        label.setTextColor(getResources().getColor(R.color.colorAlmostBlack));

        label.setText(dc.getLabel());
        label.setGravity(Gravity.CENTER);
        label.setTextSize(15);

        label.setLayoutParams(childLayout);
        addView(label);


        control.setLayoutParams(childLayout);
        addView(control);
        subControl = control;
    }

    @Override
    public void updateControlState() {
        if (subControl instanceof BaseInputControl) {
            ((BaseInputControl) subControl).updateControlState();
        }
    }
}