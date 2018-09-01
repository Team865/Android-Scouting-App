package ca.warp7.android.scouting.widgets;

import android.content.Context;
import android.support.v7.widget.AppCompatSeekBar;

import ca.warp7.android.scouting.model.BaseInputControl;
import ca.warp7.android.scouting.model.DataConstant;
import ca.warp7.android.scouting.model.ScoutingActivityListener;

/**
 * Creates a ratings bar based on the maximum value specified
 */

public class SeekBar
        extends AppCompatSeekBar
        implements BaseInputControl,
        AppCompatSeekBar.OnSeekBarChangeListener {

    DataConstant dc;
    ScoutingActivityListener listener;

    int lastProgress;

    public SeekBar(Context context) {
        super(context);
    }

    public SeekBar(Context context,
                   DataConstant dc,
                   ScoutingActivityListener listener) {
        super(context);
        this.dc = dc;
        this.listener = listener;

        setOnSeekBarChangeListener(this);
        setBackgroundColor(0);
        setMax(dc.getMax());
        updateControlState();
    }

    @Override
    public void onProgressChanged(android.widget.SeekBar seekBar,
                                  int progress,
                                  boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(android.widget.SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(android.widget.SeekBar seekBar) {

        if (getProgress() != lastProgress) {
            listener.getManagedVibrator().vibrateAction();
            lastProgress = getProgress();

            listener.getEntry().push(dc.getIndex(), lastProgress, 1);
            listener.pushStatus(dc.getLabel() + " - " + lastProgress + "/" + dc.getMax());
        }
    }

    @Override
    public void updateControlState() {
        lastProgress = listener.getEntry().getLastValue(dc.getIndex());
        setProgress(lastProgress);
    }
}
