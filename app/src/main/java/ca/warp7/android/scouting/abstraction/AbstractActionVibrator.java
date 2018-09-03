package ca.warp7.android.scouting.abstraction;

/**
 * @since v0.4.2
 */

public interface AbstractActionVibrator {

    /**
     * Vibrate to indicate scouting start
     */
    void vibrateStart();

    /**
     * Vibrate to indicate an action
     */
    void vibrateAction();
}
