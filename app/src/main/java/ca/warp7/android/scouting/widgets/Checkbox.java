package ca.warp7.android.scouting.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;

import ca.warp7.android.scouting.R;
import ca.warp7.android.scouting.interfaces.BaseInputControl;
import ca.warp7.android.scouting.interfaces.ScoutingActivityListener;
import ca.warp7.android.scouting.model.DataConstant;

/**
 * A checkbox that gives true or false values
 */

public class Checkbox
        extends AppCompatCheckBox
        implements BaseInputControl,
        View.OnClickListener {

    DataConstant dc;
    ScoutingActivityListener listener;

    public Checkbox(Context context) {
        super(context);
    }

    public Checkbox(Context context,
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

        setText(dc.getLabel());

        updateControlState();
    }

    @Override
    public void onClick(View v) {
        listener.getManagedVibrator().vibrateAction();
        listener.getEntry().push(dc.getIndex(), isChecked() ? 1 : 0, 1);
        listener.pushStatus(getText().toString() + " - " + (isChecked() ? "On" : "Off"));
    }

    @Override
    public void updateControlState() {
        // Temporary Fix before board format changed
        if (listener.timedInputsShouldDisable()) {
            setEnabled(false);
            setTextColor(getResources().getColor(R.color.colorGray));
        } else {
            setEnabled(true);
            setTextColor(getResources().getColor(R.color.colorAccent));
        }
        setChecked(listener.getEntry().getCount(dc.getIndex()) % 2 != 0);
    }
}
