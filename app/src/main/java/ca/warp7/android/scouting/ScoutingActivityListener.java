package ca.warp7.android.scouting;

import android.os.Handler;
import android.os.Vibrator;

/**
 * This interface is to be implemented by the activity that contains
 * these controls to engage in communication
 */

interface ScoutingActivityListener
        extends Entry.Listener {

    /**
     * @return The time Handler of the activity
     */
    Handler getHandler();

    /**
     * @return The Vibrator service of the activity
     */
    Vibrator getVibrator();

    /**
     * @return The entry model object tracking data history
     */
    Entry getEntry();

    /**
     * @return whether the current state of the activity can record time
     */
    boolean timeIsRecordable();

    /**
     * @return whether the activity is in a state not accepting timed data
     */
    boolean timedInputsShouldDisable();

    /**
     * Pushes the current time to the data stack
     *
     * @param type       the type index of the data
     * @param state_flag the on/off state of the data
     */
    void pushCurrentTimeAsValue(int type, int state_flag);

    /**
     * Pushes as string to the activity's output view
     *
     * @param status the status to push
     */
    void pushStatus(String status);
}
