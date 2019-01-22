package ca.warp7.android.scouting.widgets;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;
import ca.warp7.android.scouting.R;
import ca.warp7.android.scouting.abstraction.ParentInputControlListener;
import ca.warp7.android.scouting.abstraction.ScoutingActivityListener;
import ca.warp7.android.scouting.model2018.DataConstant;

/**
 * A button that records time as it is pressed
 * @since v0.2.0
 */

public class TimerButton
        extends BaseButton {

    private int counter;

    public TimerButton(Context context) {
        super(context);
    }

    public TimerButton(Context context,
                       DataConstant dc,
                       ScoutingActivityListener listener) {
        super(context, dc, listener);

        setText(dc.getLabel().replace(" ", "\n"));
        updateControlState();
    }

    @Override
    public void onClick(View v) {
        if (listener.timeIsRecordable()) {

            listener.getManagedVibrator().vibrateAction();

            listener.pushCurrentTimeAsValue(dc.getIndex(), 1);
            updateControlState();
            listener.getHandler().postDelayed(this::updateControlState, 1000);

            listener.pushStatus(dc.getLabel() + " - {t}s");
        } else {
            listener.pushStatus("Time Conflict @" + listener.getCurrentRelativeTime() + "s");
        }
    }

    @Override
    public void setParentListener(ParentInputControlListener listener) {
        super.setParentListener(listener);
        updateCounterView(false);
    }

    @Override
    public void updateControlState() {

        if (listener.timedInputsShouldDisable()) {
            setEnabled(false);
            setTextColor(ContextCompat.getColor(getContext(), R.color.colorGray));
        } else {

            counter = listener.getEntry().getCount(dc.getIndex());
            if (listener.dataShouldFocus(dc.getIndex())) {
                updateCounterView(true);
                setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                getBackground().setColorFilter(
                        ContextCompat.getColor(getContext(), R.color.colorAccent),
                        PorterDuff.Mode.MULTIPLY);
            } else {
                getBackground().clearColorFilter();
                setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                updateCounterView(false);
            }
            setEnabled(true);
        }
    }

    private void updateCounterView(boolean white) {
        if (parentSupportView instanceof TextView) {
            TextView counterView = (TextView) parentSupportView;
            counterView.setText(String.valueOf(counter));

            if (white) {
                counterView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
            } else {
                counterView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAlmostBlack));
            }
        }
    }
}
