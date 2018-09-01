package ca.warp7.android.scouting.widgets;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import ca.warp7.android.scouting.model.BaseInputControl;
import ca.warp7.android.scouting.model.ChildInputControl;
import ca.warp7.android.scouting.model.DataConstant;
import ca.warp7.android.scouting.model.ParentInputControlListener;
import ca.warp7.android.scouting.model.ScoutingActivityListener;

/**
 * A counter for the buttons
 */

public class CountedInputControlLayout
        extends FrameLayout
        implements BaseInputControl,
        ParentInputControlListener {

    DataConstant dc;
    ScoutingActivityListener listener;

    TextView counter;
    View subControl;

    public CountedInputControlLayout(@NonNull Context context) {
        super(context);
    }

    public CountedInputControlLayout(Context context,
                                     DataConstant dc,
                                     ScoutingActivityListener listener,
                                     View control) {
        super(context);
        this.dc = dc;
        this.listener = listener;

        addView(control);

        counter = new TextView(context);
        counter.setTextSize(15);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            counter.setElevation(10);
        }

        LayoutParams childLayout = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);

        childLayout.leftMargin = 24;
        childLayout.topMargin = 16;

        counter.setLayoutParams(childLayout);

        addView(counter);

        if (control instanceof ChildInputControl) {
            ((ChildInputControl) control).setParentListener(this);
        }

        subControl = control;

    }

    @Override
    public View getSupportView() {
        return counter;
    }

    @Override
    public void updateControlState() {
        if (subControl instanceof BaseInputControl) {
            ((BaseInputControl) subControl).updateControlState();
        }
    }
}
