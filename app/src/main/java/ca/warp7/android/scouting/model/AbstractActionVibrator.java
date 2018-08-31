package ca.warp7.android.scouting.model;

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
