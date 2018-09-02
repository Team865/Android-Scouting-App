package ca.warp7.android.scouting.widgets;

import android.content.Context;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;

import ca.warp7.android.scouting.interfaces.BaseInputControl;
import ca.warp7.android.scouting.interfaces.ScoutingActivityListener;
import ca.warp7.android.scouting.model.DataConstant;

/**
 * Creates a box container that centers the control inside it
 * @since v0.3.0
 */

public class CenteredControlLayout
        extends ConstraintLayout
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

        // Set the background of the view

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(new Button(getContext()).getBackground());
        } else {
            setBackgroundResource(android.R.drawable.btn_default);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setElevation(4);
        }

        LayoutParams childLayout;

        childLayout = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);

        childLayout.leftToLeft = LayoutParams.PARENT_ID;
        childLayout.rightToRight = LayoutParams.PARENT_ID;
        childLayout.topToTop = LayoutParams.PARENT_ID;
        childLayout.bottomToBottom = LayoutParams.PARENT_ID;

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
