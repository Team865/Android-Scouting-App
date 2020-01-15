@file:Suppress("unused")

package ca.warp7.android.scouting

import android.os.Handler
import ca.warp7.android.scouting.AbstractActionVibrator
import ca.warp7.android.scouting.boardfile.Boardfile
import ca.warp7.android.scouting.boardfile.ScoutTemplate
import ca.warp7.android.scouting.entry.MutableEntry

interface BaseScoutingActivity {

    /**
     * Current time
     */
    val currentTime get() = (System.currentTimeMillis() / 1000).toInt()

    /**
     * Relative time
     */
    val relativeTime: Double

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
     * whether the activity is in a state not accepting timed data
     */
    val timeEnabled: Boolean

    /**
     * Boardfile
     */
    val boardfile: Boardfile?

    /**
     * Scout template
     */
    val template: ScoutTemplate?

    /**
     * Forces update on the screen
     */

    fun updateTabStates()
}