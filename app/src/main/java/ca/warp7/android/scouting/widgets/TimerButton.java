package ca.warp7.android.scouting.widgets;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.View;
import android.widget.TextView;

import ca.warp7.android.scouting.R;
import ca.warp7.android.scouting.model.DataConstant;
import ca.warp7.android.scouting.model.ParentInputControlListener;
import ca.warp7.android.scouting.model.ScoutingActivityListener;

/**
 * A button that records time as it is pressed
 */

public class TimerButton
        extends BaseButton {

    int counter;

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
            listener.getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateControlState();
                }
            }, 1000);

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
            setTextColor(getResources().getColor(R.color.colorGray));
        } else {

            counter = listener.getEntry().getCount(dc.getIndex());
            if (listener.dataShouldFocus(dc.getIndex())) {
                updateCounterView(true);
                setTextColor(getResources().getColor(R.color.colorWhite));
                getBackground().setColorFilter(
                        getResources().getColor(R.color.colorAccent),
                        PorterDuff.Mode.MULTIPLY);
            } else {
                getBackground().clearColorFilter();
                setTextColor(getResources().getColor(R.color.colorAccent));
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
                counterView.setTextColor(getResources().getColor(R.color.colorWhite));
            } else {
                counterView.setTextColor(getResources().getColor(R.color.colorAlmostBlack));
            }
        }
    }
}
