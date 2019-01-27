package ca.warp7.android.scouting.v4.widgets;

import android.content.Context;
import android.support.v7.widget.AppCompatSeekBar;
import ca.warp7.android.scouting.v4.abstraction.BaseInputControl;
import ca.warp7.android.scouting.v4.abstraction.ScoutingActivityListener;
import ca.warp7.android.scouting.v4.model.DataConstant;

/**
 * Creates a ratings bar based on the maximum value specified
 *
 * @since v0.2.0
 */

public class SeekBar
        extends AppCompatSeekBar
        implements BaseInputControl,
        AppCompatSeekBar.OnSeekBarChangeListener {

    private DataConstant dc;
    private ScoutingActivityListener listener;

    private int lastProgress;

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
