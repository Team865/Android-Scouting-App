package ca.warp7.android.scouting.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import ca.warp7.android.scouting.interfaces.BaseInputControl;
import ca.warp7.android.scouting.interfaces.ChildInputControl;
import ca.warp7.android.scouting.interfaces.ParentInputControlListener;
import ca.warp7.android.scouting.interfaces.ScoutingActivityListener;
import ca.warp7.android.scouting.model.DataConstant;

/**
 * A Base button for other buttons to extend onto
 * @since v0.2.0
 */

public abstract class BaseButton
        extends AppCompatButton
        implements BaseInputControl,
        ChildInputControl,
        View.OnClickListener {

    protected DataConstant dc;
    protected ScoutingActivityListener listener;
    protected ParentInputControlListener parentInputControlListener;
    protected View parentSupportView;

    public BaseButton(Context context) {
        super(context);
    }

    public BaseButton(Context context,
                      DataConstant dc,
                      ScoutingActivityListener listener) {
        super(context);
        this.dc = dc;
        this.listener = listener;

        setOnClickListener(this);

        setAllCaps(false);
        setTextSize(18);
        setLines(2);
        setTypeface(Typeface.SANS_SERIF);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setStateListAnimator(null);
            setElevation(4);
        }
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void setParentListener(ParentInputControlListener listener) {
        parentInputControlListener = listener;
        parentSupportView = listener.getSupportView();
    }
}
