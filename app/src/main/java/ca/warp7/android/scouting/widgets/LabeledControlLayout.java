package ca.warp7.android.scouting.widgets;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import ca.warp7.android.scouting.R;
import ca.warp7.android.scouting.abstraction.BaseInputControl;
import ca.warp7.android.scouting.abstraction.ScoutingActivityListener;
import ca.warp7.android.scouting.model2018.DataConstant;

/**
 * Creates a box container for a label and another control
 * @since v0.3.0
 */

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class LabeledControlLayout
        extends LinearLayout
        implements BaseInputControl {

    private DataConstant dc;
    private ScoutingActivityListener listener;

    private View subControl;

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

        setBackgroundResource(R.drawable.layer_list_bg_group);

        TableRow.LayoutParams childLayout = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT,
                1.0f);

        // Add the views

        TextView label = new TextView(context);
        label.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAlmostBlack));

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
