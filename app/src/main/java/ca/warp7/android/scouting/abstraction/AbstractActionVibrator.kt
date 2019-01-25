package ca.warp7.android.scouting.abstraction

/**
 * @since v0.4.2
 */

interface AbstractActionVibrator {

    /**
     * Vibrate to indicate scouting start
     */
    fun vibrateStart()

    /**
     * Vibrate to indicate an action
     */
    fun vibrateAction()
}
