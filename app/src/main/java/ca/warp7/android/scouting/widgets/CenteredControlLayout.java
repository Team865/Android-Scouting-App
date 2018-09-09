package ca.warp7.android.scouting.widgets;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import ca.warp7.android.scouting.R;
import ca.warp7.android.scouting.abstraction.BaseInputControl;
import ca.warp7.android.scouting.abstraction.ScoutingActivityListener;
import ca.warp7.android.scouting.model.DataConstant;

/**
 * Creates a box container that centers the control inside it
 *
 * @since v0.3.0
 */

public class CenteredControlLayout
        extends LinearLayout
        implements BaseInputControl,
        View.OnClickListener {

    DataConstant dc;
    ScoutingActivityListener listener;

    View subControl;

    public CenteredControlLayout(Context context) {
        super(context);
    }

    public CenteredControlLayout(Context context,
                                 DataConstant dc,
                                 ScoutingActivityListener listener,
                                 View control) {
        super(context);
        this.dc = dc;
        this.listener = listener;

        setBackgroundResource(R.drawable.layer_list_bg_group);

        LayoutParams childLayout;
        childLayout = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        setGravity(Gravity.CENTER);
        control.setLayoutParams(childLayout);

        addView(control);
        setOnClickListener(this);

        subControl = control;
        updateControlState();
    }

    @Override
    public void onClick(View v) {
        if (!listener.timedInputsShouldDisable()
                && subControl instanceof OnClickListener) {
            subControl.performClick();
        }
    }

    @Override
    public void updateControlState() {
        setEnabled(!listener.timedInputsShouldDisable());
        if (subControl instanceof BaseInputControl) {
            ((BaseInputControl) subControl).updateControlState();
        }
    }
}
