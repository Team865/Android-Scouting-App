package ca.warp7.android.scouting.v4.widgets;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import ca.warp7.android.scouting.abstraction.BaseInputControl;
import ca.warp7.android.scouting.abstraction.ChildInputControl;
import ca.warp7.android.scouting.abstraction.ParentInputControlListener;
import ca.warp7.android.scouting.abstraction.ScoutingActivityListener;
import ca.warp7.android.scouting.model.DataConstant;

/**
 * A counter for the buttons
 *
 * @since v0.3.0
 */

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class CountedInputControlLayout
        extends FrameLayout
        implements BaseInputControl,
        ParentInputControlListener {

    private DataConstant dc;
    private ScoutingActivityListener listener;

    private TextView counter;
    private View subControl;

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
