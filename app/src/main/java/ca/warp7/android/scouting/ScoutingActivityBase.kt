@file:Suppress("unused")

package ca.warp7.android.scouting

import android.os.Handler
import ca.warp7.android.scouting.abstraction.AbstractActionVibrator
import ca.warp7.android.scouting.v5.entry.MutableEntry

interface ScoutingActivityBase {

    /**
     * Current time
     */
    val currentTime get() = (System.currentTimeMillis() / 1000).toInt()

    /**
     * The time Handler of the activity
     */
    val handler: Handler

    /**
     * The managed Vibrator service of the activity
     */
    val actionVibrator: AbstractActionVibrator?

    /**
     * The entry model object tracking data history
     */
    val entry: MutableEntry?

    /**
     * whether the current state of the activity can record time
     */
    val isSecondLimit: Boolean

    /**
     * whether the activity is in a state not accepting timed data
     */
    val timeEnabled: Boolean
}