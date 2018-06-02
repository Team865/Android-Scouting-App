package ca.warp7.android.scouting;

import android.os.Handler;
import android.os.Vibrator;

/**
 * This interface is to be implemented by the activity that contains
 * these controls to make available communication
 */
interface ScoutingActivityListener {

    /**
     * @return The time Handler of the activity
     */
    Handler getHandler();

    /**
     * @return The Vibrator service of the activity
     */
    Vibrator getVibrator();

    /**
     * @return The encoder object tracking data history
     */
    Encoder getEncoder();

    boolean canUpdateTime();

    void pushCurrentTimeAsValue(int t, int s);

    void pushStatus(String status);
}
