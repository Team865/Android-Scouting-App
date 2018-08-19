package ca.warp7.android.scouting;

import android.os.Handler;

import ca.warp7.android.scouting.model.Entry;

/**
 * This interface is to be implemented by the activity that contains
 * these controls to engage in communication
 *
 * @author Team 865
 */

interface ScoutingActivityListener
        extends Entry.Timekeeper {

    /**
     * @return The time Handler of the activity
     */
    Handler getHandler();

    /**
     * @return The managed Vibrator service of the activity
     */
    ManagedPreferences.ActionVibrator getManagedVibrator();

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
     * @return whether a specific control should focus at the current time
     */
    @SuppressWarnings("unused")
    boolean dataShouldFocus(int dataType);

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
