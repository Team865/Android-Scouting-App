package ca.warp7.android.scouting.widgets;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.View;

import ca.warp7.android.scouting.R;
import ca.warp7.android.scouting.abstraction.ScoutingActivityListener;
import ca.warp7.android.scouting.model.DataConstant;

/**
 * A button that measures duration,
 * equivalent in function as a ToggleButton.
 * It records the time of when the button is
 * @since v0.2.0
 */

public class DurationButton
        extends BaseButton {

    boolean isOn;

    public DurationButton(Context context) {
        super(context);
    }

    public DurationButton(Context context,
                          DataConstant dc,
                          ScoutingActivityListener listener) {
        super(context, dc, listener);
        updateControlState();
    }

    @Override
    public void onClick(View v) {
        if (listener.timeIsRecordable()) {
            isOn = !isOn;
            listener.pushCurrentTimeAsValue(dc.getIndex(), isOn ? 1 : 0);
            listener.pushStatus(getText().toString() + " - {t}s");

            updateLooks();
            listener.getManagedVibrator().vibrateAction();

        } else {
            listener.pushStatus("Time Conflict @" + listener.getCurrentRelativeTime() + "s");
        }
    }

    @Override
    public void updateControlState() {
        isOn = listener.getEntry().getCount(dc.getIndex()) % 2 != 0;

        updateLooks();

        if (listener.timedInputsShouldDisable()) {
            setEnabled(false);
            setTextColor(getResources().getColor(R.color.colorGray));
        } else {
            setEnabled(true);
        }
    }

    void updateLooks() {
        if (isOn) {
            setTextColor(getResources().getColor(R.color.colorWhite));
            setText(dc.getLabelOn());
            getBackground().setColorFilter(
                    getResources().getColor(R.color.colorRed),
                    PorterDuff.Mode.MULTIPLY);
        } else {
            setTextColor(getResources().getColor(R.color.colorLightGreen));
            setText(dc.getLabel());
            getBackground().clearColorFilter();
        }
    }

}
