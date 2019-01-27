package ca.warp7.android.scouting.v4.widgets;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.view.View;
import ca.warp7.android.scouting.R;
import ca.warp7.android.scouting.v4.abstraction.ScoutingActivityListener;
import ca.warp7.android.scouting.v4.model.DataConstant;

/**
 * A button that measures duration,
 * equivalent in function as a ToggleButton.
 * It records the time of when the button is
 *
 * @since v0.2.0
 */

public class DurationButton
        extends BaseButton {

    private boolean isOn;

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
            setTextColor(ContextCompat.getColor(getContext(), R.color.colorGray));
        } else {
            setEnabled(true);
        }
    }

    private void updateLooks() {
        if (isOn) {
            setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
            setText(dc.getLabelOn());
            getBackground().setColorFilter(
                    ContextCompat.getColor(getContext(), R.color.colorRed),
                    PorterDuff.Mode.MULTIPLY);
        } else {
            setTextColor(ContextCompat.getColor(getContext(), R.color.colorLightGreen));
            setText(dc.getLabel());
            getBackground().clearColorFilter();
        }
    }

}
